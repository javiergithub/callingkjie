package com.sergiuoltean.kie;

import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.kie.api.KieServices;
import org.kie.api.command.Command;
import org.kie.api.command.KieCommands;
import org.kie.api.runtime.ClassObjectFilter;
import org.kie.api.runtime.ExecutionResults;
import org.kie.api.runtime.ObjectFilter;
import org.kie.server.api.model.KieServiceResponse.ResponseType;
import org.kie.server.api.model.ServiceResponse;
import org.kie.server.client.KieServicesClient;
import org.kie.server.client.RuleServicesClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class KieService<T> {

  private KieServicesClient kieServicesClient;
  private Class resultClass;

  @Autowired
  public KieService(KieServicesClient kieServicesClient) {
    this.kieServicesClient = kieServicesClient;
  }

  public void setResultClass(Class resultClass) {
    this.resultClass = resultClass;
  }

  public List<T> executeCommands(String containerId, List<?> facts, String sessionName) {
    String outIdentifier = "out";
    RuleServicesClient rulesClient = kieServicesClient.getServicesClient(RuleServicesClient.class);

    Command<?> batchCommand = prepareCommands(facts, sessionName, outIdentifier);
    ServiceResponse<ExecutionResults> executeResponse = rulesClient
            .executeCommandsWithResults(containerId, batchCommand);

    if (executeResponse.getType() == ResponseType.SUCCESS) {
      log.debug("Commands executed with success!");
      ExecutionResults result = executeResponse.getResult();
      return (List<T>) result.getValue(outIdentifier);
    } else {
      throw new RuntimeException("Execution failed");
    }
  }

  private Command<?> prepareCommands(List<?> facts, String sessionName, String outIdentifier) {
    KieCommands commandsFactory = KieServices.Factory.get().getCommands();
    List<Command> commands = facts.stream().map(commandsFactory::newInsert).collect(Collectors.toList());
    commands.add(commandsFactory.newFireAllRules());
    ObjectFilter factsFilter = new ClassObjectFilter(resultClass);
    commands.add(commandsFactory.newGetObjects(factsFilter, outIdentifier));
    return commandsFactory.newBatchExecution(commands, sessionName);
  }


}
