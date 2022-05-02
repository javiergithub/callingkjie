package com.sergiuoltean.kie;

import org.kie.server.api.marshalling.MarshallingFormat;
import org.kie.server.client.KieServicesClient;
import org.kie.server.client.KieServicesConfiguration;
import org.kie.server.client.KieServicesFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfiguration {

  @Bean
  public KieServicesClient kieServicesClient(
          @Value("${KIE_SERVER_REST_ENDPOINT:172.17.0.3:8080/kie-server/services/rest/server}") String kieRestEndpoint,
          @Value("${KIE_SERVER_USER:admin}") String kieUser,
          @Value("${KIE_SERVER_PASSWORD:admin}") String kiePassword,
          @Value("${EXECUTION_TIMEOUT:100000}") Integer executionTimeout) {
    KieServicesConfiguration conf = KieServicesFactory.newRestConfiguration(kieRestEndpoint, kieUser, kiePassword);
    conf.setMarshallingFormat(MarshallingFormat.JSON);
    conf.setTimeout(executionTimeout);
    return KieServicesFactory.newKieServicesClient(conf);
  }
}