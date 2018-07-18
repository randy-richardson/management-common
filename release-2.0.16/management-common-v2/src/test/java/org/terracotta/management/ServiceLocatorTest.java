package org.terracotta.management;

import static org.junit.Assert.*;

import java.io.Serializable;

import org.junit.After;
import org.junit.Test;
import org.terracotta.management.resource.AgentEntityV2;

public class ServiceLocatorTest {

  @Test
  public void stringTest() {

    ServiceLocator locator = new ServiceLocator();
    locator.loadService(String.class, "value");
    ServiceLocator.load(locator);
    String value = ServiceLocator.locate(String.class);
    assertSame("value", value);
  }

  @Test
  public void classWithHierarchyTest(){

    ServiceLocator locator = new ServiceLocator();

    ConfigServiceImpl configServiceImpl = new ConfigServiceImpl();
    AgentEntityV2 agentEntity = new AgentEntityV2();
    locator.loadService(ConfigService.class, configServiceImpl).loadService(Serializable.class, agentEntity);
    ServiceLocator.load(locator);

    ConfigService value = ServiceLocator.locate(ConfigService.class);
    assertSame(configServiceImpl, value);
    Serializable value2 = ServiceLocator.locate(Serializable.class);
    assertSame(agentEntity, value2);
  }

  @Test
  public void callToLoadMethodShouldFreezeTheListOfServicesTest(){
    ServiceLocator locator = new ServiceLocator();

    ConfigServiceImpl configServiceImpl = new ConfigServiceImpl();
    AgentEntityV2 agentEntity = new AgentEntityV2();
    locator.loadService(ConfigService.class, configServiceImpl);
    //after that call it should be impossible to change the services
    ServiceLocator.load(locator);

    locator.loadService(Serializable.class, agentEntity);
    // no Serializable can be found since we froze the list of services with load()
    assertNull(ServiceLocator.locate(Serializable.class));
  }
  

  @Test(expected = IllegalStateException.class)
  public void throwExceptionIfLoadIsNotCalledBeforeFirstLocate() {
    ServiceLocator.locate(Serializable.class);
  }
  
  @Test(expected = IllegalStateException.class)
  public void throwExceptionIfLoadIsCalledTwice() {
    ServiceLocator locator = new ServiceLocator();
    ConfigServiceImpl configServiceImpl = new ConfigServiceImpl();
    AgentEntityV2 agentEntity = new AgentEntityV2();
    locator.loadService(ConfigService.class, configServiceImpl).loadService(Serializable.class, agentEntity);
    ServiceLocator.load(locator);
    // badaboom !
    ServiceLocator.load(locator);

  }
  

  public interface ConfigService {

    boolean saveConfig() throws ServiceExecutionException;

  }

  public class ConfigServiceImpl implements ConfigService {

    @Override
    public boolean saveConfig() throws ServiceExecutionException {
      return false;
    }

  }

  @After
  /**
   * the service locator must be unloaded between each test
   */
  public void tearDown() {
    ServiceLocator.unload();
  }

}

