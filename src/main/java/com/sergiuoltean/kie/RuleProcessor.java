package com.sergiuoltean.kie;

import com.test.importproducts.ImportProduct;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

@Slf4j
@Component
public class RuleProcessor implements Processor {

  private final KieService<ImportProduct> kieService;

  @Autowired
  public RuleProcessor(KieService<ImportProduct> kieService) {
    this.kieService = kieService;
    this.kieService.setResultClass(ImportProduct.class);
  }

  @Override
  public void process(Exchange exchange) throws Exception {
    List<List<String>> data = (List<List<String>>) exchange.getIn().getBody();
    List<ImportProduct> products = data.stream().map(this::mapImportProducts).collect(Collectors.toList());
    if (!CollectionUtils.isEmpty(products)) {
      List<? super Object> facts = new ArrayList<>(products);
      final var containerId = "ImportProducts_1.0.0-SNAPSHOT";
      final var mySession = "mySession";
      exchange.getIn().setBody(kieService.executeCommands(containerId, facts, mySession));
    } else {
      log.info("Nothing to process");
    }
  }

  private ImportProduct mapImportProducts(List<String> line) {
    ImportProduct importProduct = new ImportProduct();
    importProduct.setCategory(line.get(0));
    importProduct.setId(line.get(1));
    importProduct.setStatus(line.get(2));
    importProduct.setPrice(Double.valueOf(line.get(3)));
    return importProduct;
  }
}
