package com.terracotta.management;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

/**
 * @author: Anthony Dahanne
 * 
 *          This class is to be extended to test that we did not forget any rest
 *          resources in the Jersey Application
 * 
 */
public class JerseyApplicationTestCommon {
  protected static final String[] PACKAGE_STARTS_WITH_FILTERS = new String[] { "java", "javax", "org.apache", "com.sun", "org.codehaus",
                                                                               "org.hibernate", "org.glassfish.jersey",
                                                                               "com.fasterxml.jackson"};

  private static final ClassFilter JERSEY_FILTER = new ClassFilter() {
    @Override
    public boolean accept(Class<?> clazz) {
      return (clazz.isAnnotationPresent(javax.ws.rs.ext.Provider.class) ||
              clazz.isAnnotationPresent(javax.ws.rs.Path.class));
    }
  };

  protected Set<Class<?>> annotatedClassesFound() throws IOException, ClassNotFoundException {
    List<String> classpathElements = getClasspathElements();

    Set<Class<?>> jerseyAnnotatedClasses = new HashSet<Class<?>>();
    for (String cpElement : classpathElements) {
      if (cpElement.endsWith(".jar")) {
        System.out.println("last scanned jar : " + cpElement);
        jerseyAnnotatedClasses.addAll(filterAnnotatedClassesFromJar(cpElement));
      } else {
        System.out.println("last scanned path : " + cpElement);
        jerseyAnnotatedClasses.addAll(filterAnnotatedClassesFromDirectory(cpElement));
      }
    }
    return jerseyAnnotatedClasses;
  }

  private List<String> getClasspathElements() throws IOException {
    List<String> elements = new ArrayList<String>();
    String classpath = System.getProperty("java.class.path");
    System.out.println("XXX Current classpath: " + classpath);
    if (classpath.contains("surefirebooter")) {
      JarFile surefireBooter = new JarFile(classpath);
      Manifest manifest = surefireBooter.getManifest();
      classpath = manifest.getMainAttributes().getValue("Class-path");
      surefireBooter.close();
      for (String urlElement : classpath.split(" ")) {
        elements.add(FileUtils.toFile(new URL(urlElement)).getAbsolutePath());
      }
    } else {
      for (String path : classpath.split(File.pathSeparator)) {
        elements.add(path);
      }
    }
    return elements;
  }

  private Set<Class<?>> filterAnnotatedClassesFromDirectory(String cpElement)
      throws ClassNotFoundException {
    Set<Class<?>> classes = new HashSet<Class<?>>();
    File rootPath = new File(cpElement);
    File[] files = rootPath.listFiles();
    for (File file : files) {
      filterAnnotatedClassesFromRootPath(file, classes, rootPath);
    }
    return classes;
  }

  private Set<Class<?>> filterAnnotatedClassesFromJar(String cpElement) throws IOException {
    Set<Class<?>> classes = new HashSet<Class<?>>();
    JarFile jarFile = new JarFile(cpElement);
    Enumeration<JarEntry> entries = jarFile.entries();
    while (entries.hasMoreElements()) {
      JarEntry entry = entries.nextElement();
      String entryName = entry.getName();
      if (entryName.endsWith(".class")) {
        String className = entryName.replace('/', '.').replace('\\', '.').replace(".class", "");
        try {
          if (packageOfClassNotFiltered(className)) {
            Class<?> clazz = Class.forName(className);
            if (JERSEY_FILTER.accept(clazz)) {
              classes.add(clazz);
            }
          }
        } catch (Throwable e) {
          // not a problem
        }
      }
    }
    jarFile.close();
    return classes;
  }

  private static boolean packageOfClassNotFiltered(String className) {
    for (String filter : PACKAGE_STARTS_WITH_FILTERS) {
      if (className.startsWith(filter)) {
        return false;
      }
    }
    return true;
  }

  protected static Set<Class<?>> filterClassesFromJaxRSPackages(Set<Class<?>> classesToFilter) {
    Set<Class<?>> filteredClasses = new HashSet<Class<?>>();
    for (Class<?> classTofilter : classesToFilter) {
      if (packageOfClassNotFiltered(classTofilter.getName())) {
        filteredClasses.add(classTofilter);
      }
    }
    return filteredClasses;
  }

  private void filterAnnotatedClassesFromRootPath(File file, Set<Class<?>> classes, File rootPath)
      throws ClassNotFoundException {
    if (file.isFile() && file.getAbsolutePath().endsWith(".class")) {
      String replace = file.getAbsolutePath().replace(rootPath.getAbsolutePath() + File.separator, "");
      String className = replace.replace(File.separator, ".").substring(0, replace.length() - 6);
      try {
        Class<?> clazz = Class.forName(className);
        if (JERSEY_FILTER.accept(clazz)) {
          classes.add(clazz);
        }
      } catch (Throwable e) {
        // not a problem
      }
    } else if (file.isDirectory()) {
      File[] listOfFiles = file.listFiles();
      if (listOfFiles != null) {
        for (File listOfFile : listOfFiles) {
          filterAnnotatedClassesFromRootPath(listOfFile, classes, rootPath);
        }
      }
    }
  }

  private static interface ClassFilter {
    public boolean accept(Class<?> clazz);
  }
}
