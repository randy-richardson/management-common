package org.terracotta.management.l1bridge;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.InputStream;
import java.io.ObjectInputStream;

public class RemoteCallDescriptorTest {

  @Test
  public void versionCompatibilityTest() throws Exception {
    InputStream in = getClass().getResourceAsStream("original-RemoteCallDescriptor.ser");
    ObjectInputStream ois = new ObjectInputStream(in);
    try {
      RemoteCallDescriptor remoteCallDescriptor = (RemoteCallDescriptor) ois.readObject();
      assertNotNull(remoteCallDescriptor);
      assertEquals(remoteCallDescriptor.getTicket(),"test-ticket");
      assertEquals(remoteCallDescriptor.getToken(),"test-token");
      assertEquals(remoteCallDescriptor.getIaCallbackUrl(),"test-callback-url");
      assertEquals(remoteCallDescriptor.getMethodName(),"testMethodName");
    } finally {
      ois.close();
    }
  }

}