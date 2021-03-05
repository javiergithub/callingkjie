package com.sergiuoltean.kie;

import lombok.extern.slf4j.Slf4j;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MainRoute extends RouteBuilder {

  private final ResultToFile resultToFile;
  private final RuleProcessor ruleProcessor;

  @Autowired
  public MainRoute(RuleProcessor ruleProcessor, ResultToFile resultToFile) {
    this.resultToFile = resultToFile;
    this.ruleProcessor = ruleProcessor;
  }

  @Override
  public void configure() {

    from("file:src/main/resources/poll?noop=true")
            .unmarshal().csv()
            .process(ruleProcessor)
            .process(resultToFile)
            .end();

  }
}
