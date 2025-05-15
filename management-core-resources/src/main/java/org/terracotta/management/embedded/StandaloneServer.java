/*
 * All content copyright (c) 2003-2012 Terracotta, Inc., except as may otherwise be noted in a separate copyright notice. All rights reserved.
 * Copyright IBM Corp. 2024, 2025
* */

package org.terracotta.management.embedded;

import java.util.Collections;
import java.util.EnumSet;
import java.util.List;

import javax.net.ssl.SSLContext;
import jakarta.servlet.DispatcherType;
import jakarta.servlet.ServletContextListener;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.SecureRequestCustomizer;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.SslConnectionFactory;
import org.eclipse.jetty.ee10.servlet.FilterHolder;
import org.eclipse.jetty.ee10.servlet.ServletContextHandler;
import org.eclipse.jetty.ee10.servlet.ServletHolder;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.glassfish.jersey.servlet.ServletContainer;

/**
 * <p>A standalone server implementation for agents embedded at the Terracotta monitorable entity.</p>
 *
 * @author Ludovic Orban
 * @author brandony
 */
public final class StandaloneServer implements StandaloneServerInterface {
  public static final String EMBEDDED_CTXT = "/tc-management-api";

  private final List<FilterDetail> filterDetails;

  private final List<ServletContextListener> servletListeners;

  private volatile Server server;

  private final String applicationClassName;

  private final String host;

  private final int port;

  private final SSLContext sslCtxt;

  private final boolean needClientAuth;

  /**
   * Create a standalone management server.
   * @param filterDetails a list of {@link FilterDetail} to add. Can be null;
   * @param servletListeners a list of {@link ServletContextListener} to add. Can be null.
   * @param applicationClassName the {@link jakarta.ws.rs.core.Application} implementation to deploy.
   * @param host the host or IP address to bind. Mandatory unless port is &lt; 0.
   * @param port the port to bind. Can be &lt; 0 to mean do not bind.
   * @param sslCtxt the {@link SSLContext} to use. Can be null if no SSL is desired.
   * @param needClientAuth true to mandate client SSL auth, false otherwise.
   */
  public StandaloneServer(List<FilterDetail> filterDetails, List<ServletContextListener> servletListeners, String applicationClassName,
      String host, int port, SSLContext sslCtxt, boolean needClientAuth) {
    super();
    this.filterDetails = filterDetails;
    this.servletListeners = servletListeners;
    this.applicationClassName = applicationClassName;
    this.host = host;
    this.port = port;
    this.sslCtxt = sslCtxt;
    this.needClientAuth = needClientAuth;
  }

  @Override
  public void start() throws Exception {
    if (port < 0) {
      return;
    }
    if (port == 0) {
      throw new IllegalArgumentException("port must be set");
    }
    if (applicationClassName == null) {
      throw new IllegalArgumentException("applicationClassName must be set");
    }
    if (server != null) {
      throw new IllegalStateException("server already started");
    }

    try {
      // Create a basic jetty server object without declaring the port.  Since we are configuring connectors
      // directly we'll be setting ports on those connectors.
      server = new Server();
      // HttpConfiguration is a collection of configuration information appropriate for http and https. The default
      // scheme for http is <code>http</code> of course, as the default for secured http is <code>https</code> but
      // we show setting the scheme to show it can be done.  The port for secured communication is also set here.
      HttpConfiguration httpConfig = new HttpConfiguration();
      httpConfig.setSecureScheme("https");
      httpConfig.setSecurePort(port);
      httpConfig.setSendServerVersion(false);
      ServerConnector connector;
      if (sslCtxt != null) {
        // A new HttpConfiguration object is needed for the next connector and you can pass the old one as an
        // argument to effectively clone the contents. On this HttpConfiguration object we add a
        // SecureRequestCustomizer which is how a new connector is able to resolve the https connection before
        // handing control over to the Jetty Server.
        HttpConfiguration httpsConfig = new HttpConfiguration(httpConfig);
        SecureRequestCustomizer src = new SecureRequestCustomizer();
        src.setSniHostCheck(false);
        httpsConfig.addCustomizer(src);

        SslContextFactory.Server sslContextFactory = new SslContextFactory.Server();
        sslContextFactory.setSslContext(sslCtxt);
        sslContextFactory.setNeedClientAuth(needClientAuth);
        // HTTPS connector
        // We create a second ServerConnector, passing in the http configuration we just made along with the
        // previously created ssl context factory. Next we set the port and a longer idle timeout.
        connector = new ServerConnector(server,
            new SslConnectionFactory(sslContextFactory, "http/1.1"),
            new HttpConnectionFactory(httpsConfig));
      } else {
        // HTTP connector
        // The first server connector we create is the one for http, passing in the http configuration we configured
        // above so it can get things like the output buffer size, etc. We also set the port (8080) and configure an
        // idle timeout.
        connector = new ServerConnector(server, new HttpConnectionFactory(httpConfig));
      }

      connector.setHost(host);
      connector.setPort(port);
      server.setConnectors(new Connector[]{connector});

      ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
      context.setContextPath(EMBEDDED_CTXT);
      server.setHandler(context);

      ServletHolder servletHolder = new ServletHolder(new ServletContainer());
      // make sure com.sun.jersey.core.util.FeaturesAndProperties.FEATURE_XMLROOTELEMENT_PROCESSING is set to true
      // so that a list of @XmlRootElement(name = "configuration") is <configurations>
      servletHolder.setInitParameter("com.sun.jersey.config.feature.XmlRootElementProcessing", "true");
      servletHolder.setInitParameter("jakarta.ws.rs.Application", applicationClassName);
      // not needed anymore thanks to the jackson-jaxrs-json-provider
      // servletHolder.setInitParameter("com.sun.jersey.api.json.POJOMappingFeature", "true");
      servletHolder.setInitParameter("com.sun.jersey.spi.container.ContainerRequestFilters",
          "com.sun.jersey.api.container.filter.GZIPContentEncodingFilter");
      servletHolder.setInitParameter("com.sun.jersey.spi.container.ContainerResponseFilters",
          "com.sun.jersey.api.container.filter.GZIPContentEncodingFilter");
      context.addServlet(servletHolder, "/*");

      if (servletListeners != null) {
        context.setEventListeners(Collections.unmodifiableList(servletListeners));
      }

      if (filterDetails != null) {
        for (FilterDetail f : filterDetails) {
          FilterHolder filterHolder = new FilterHolder(f.getFilter());
          EnumSet<DispatcherType> dTypes = null;

          if (f.getDispatcherNames() != null) {
            dTypes = EnumSet.noneOf(DispatcherType.class);

            for (String dn : f.getDispatcherNames()) {
              dTypes.add(DispatcherType.valueOf(dn));
            }
          }

          context.addFilter(filterHolder, f.getPathSpec(), dTypes);
        }
      }

      server.start();
    } catch (Exception e) {
      server.stop();
      server = null;
      throw e;
    }
  }

  @Override
  public void stop() throws Exception {
    if (server == null || port < 0) {
      return;
    }

    try {
      server.stop();
      server.join();
    } finally {
      server = null;
    }
  }
}