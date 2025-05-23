package io.eventuate.examples.tram.sagas.ordersandcustomers.endtoendtests;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ClassUtils;

public abstract class ApplicationUnderTest {

  protected Logger logger = LoggerFactory.getLogger(getClass());

  public static ApplicationUnderTest make() {
    try {
      String className = ApplicationUnderTest.class.getName() + "Using" + System.getProperty("endToEndTestMode", "TestContainers");
      System.out.println("endToEndTestMode: " + className);
      return (ApplicationUnderTest) ClassUtils.forName(className, ApplicationUnderTest.class.getClassLoader()).newInstance();
    } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
      throw new RuntimeException(e);
    }
  }

  public abstract void start();


  public String apiGatewayBaseUrl(String hostName, String path, String... pathElements) {
    return BaseUrlUtils.baseUrl(hostName, path, getApigatewayPort(), pathElements);
  }
  abstract int getApigatewayPort();
  abstract int getCustomerServicePort();
  abstract int getOrderServicePort();
  abstract boolean exposesSwaggerUiForBackendServices();

  public String getJwt() {
    throw new UnsupportedOperationException("implement me");
  }

  public String getApiGatewayBaseURI(String hostName) {
    return String.format("http://%s", hostName);

  }
}
