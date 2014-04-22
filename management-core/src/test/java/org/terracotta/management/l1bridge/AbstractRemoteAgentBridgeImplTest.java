package org.terracotta.management.l1bridge;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.terracotta.management.ServiceLocator;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.StringWriter;
import java.io.Writer;

import static org.hamcrest.CoreMatchers.equalTo;

/**
 * @author Ludovic Orban
 */
public class AbstractRemoteAgentBridgeImplTest {

  @After
  public void tearDown() {
    ServiceLocator.unload();
  }

  @Test(expected = RemoteCallException.class)
  public void given_nonexistent_service_when_invoke() throws Exception {
    AbstractRemoteAgentEndpointImpl impl = new AbstractRemoteAgentEndpointImpl() {
      @Override
      public String getVersion() {
        return "1.2.3";
      }
      @Override
      public String getAgency() {
        return "test";
      }
    };

    impl.invoke(new RemoteCallDescriptor(null, null, null, "com.terracotta.test.NonExistentService", "methodThatDoesNotExist", new Class[0], new Object[0]));
  }

  @Test(expected = RemoteCallException.class)
  public void given_nonexistent_service_method_when_invoke() throws Exception {
    ServiceLocator serviceLocator = new ServiceLocator();
    serviceLocator.loadService(CalcService.class, new CalcServiceImpl());
    ServiceLocator.load(serviceLocator);

    AbstractRemoteAgentEndpointImpl impl = new AbstractRemoteAgentEndpointImpl() {
      @Override
      public String getVersion() {
        return "1.2.3";
      }
      @Override
      public String getAgency() {
        return "test";
      }
    };

    impl.invoke(new RemoteCallDescriptor(null, null, null, CalcService.class.getName(), "methodThatDoesNotExist", new Class[0], new Object[0]));
  }

  @Test(expected = RemoteCallException.class)
  public void given_wrong_service_method_args_when_invoke() throws Exception {
    ServiceLocator serviceLocator = new ServiceLocator();
    serviceLocator.loadService(CalcService.class, new CalcServiceImpl());
    ServiceLocator.load(serviceLocator);

    AbstractRemoteAgentEndpointImpl impl = new AbstractRemoteAgentEndpointImpl() {
      @Override
      public String getVersion() {
        return "1.2.3";
      }
      @Override
      public String getAgency() {
        return "test";
      }
    };

    impl.invoke(new RemoteCallDescriptor(null, null, null, CalcService.class.getName(), "add", new Class[] {String.class}, new Object[] {""}));
  }

  @Test(expected = RemoteCallException.class)
  public void given_unserializable_service_method_args_when_invoke() throws Exception {
    ServiceLocator serviceLocator = new ServiceLocator();
    serviceLocator.loadService(CalcService.class, new CalcServiceImpl());
    ServiceLocator.load(serviceLocator);

    AbstractRemoteAgentEndpointImpl impl = new AbstractRemoteAgentEndpointImpl() {
      @Override
      public String getVersion() {
        return "1.2.3";
      }
      @Override
      public String getAgency() {
        return "test";
      }
    };

    impl.invoke(new RemoteCallDescriptor(null, null, null, CalcService.class.getName(), "print", new Class[] {Writer.class}, new Object[] {new StringWriter()}));
  }

  @Test(expected = RemoteCallException.class)
  public void given_service_method_throwing_exception_when_invoke() throws Exception {
    ServiceLocator serviceLocator = new ServiceLocator();
    serviceLocator.loadService(CalcService.class, new CalcServiceImpl());
    ServiceLocator.load(serviceLocator);

    AbstractRemoteAgentEndpointImpl impl = new AbstractRemoteAgentEndpointImpl() {
      @Override
      public String getVersion() {
        return "1.2.3";
      }
      @Override
      public String getAgency() {
        return "test";
      }
    };

    impl.invoke(new RemoteCallDescriptor(null, null, null, CalcService.class.getName(), "brokenMethod", new Class[0], new Object[0]));
  }

  @Test
  public void success_given_valid_args_when_invoke() throws Exception {
    ServiceLocator serviceLocator = new ServiceLocator();
    serviceLocator.loadService(CalcService.class, new CalcServiceImpl());
    ServiceLocator.load(serviceLocator);

    AbstractRemoteAgentEndpointImpl impl = new AbstractRemoteAgentEndpointImpl() {
      @Override
      public String getVersion() {
        return "1.2.3";
      }
      @Override
      public String getAgency() {
        return "test";
      }
    };

    byte[] response = impl.invoke(new RemoteCallDescriptor(null, null, null, CalcService.class.getName(), "add", new Class[] { int.class, int.class }, new Object[] { 5, 10 }));
    Assert.assertThat((Integer)deserialize(response), equalTo(15));
  }

  private static Object deserialize(byte[] bytes) throws IOException, ClassNotFoundException {
    ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bytes));
    try {
      return ois.readObject();
    } finally {
      ois.close();
    }
  }

}

interface CalcService {
  int add(int i1, int i2);
  Writer print(Writer writer) throws IOException;
  void brokenMethod();
}

class CalcServiceImpl implements CalcService {
  @Override
  public int add(int i1, int i2) {
    return i1 + i2;
  }

  @Override
  public Writer print(Writer writer) throws IOException {
    writer.append("hello");
    return writer;
  }

  @Override
  public void brokenMethod() {
    throw new RuntimeException();
  }
}