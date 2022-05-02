package com.sergiuoltean.kie;

import com.castine.timload.map291.ClientAccts;
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

	private final KieService<ClientAccts> kieService;

	@Autowired
	public RuleProcessor(KieService<ClientAccts> kieService) {
		this.kieService = kieService;
		this.kieService.setResultClass(ClientAccts.class);
	}

	@Override
	public void process(Exchange exchange) throws Exception {
		List<List<String>> data = (List<List<String>>) exchange.getIn().getBody();
		List<ClientAccts> cliAccL = data.stream().map(this::mapImportClientAccts).collect(Collectors.toList());
		if (!CollectionUtils.isEmpty(cliAccL)) {
			List<? super Object> facts = new ArrayList<>(cliAccL);
			final var containerId = "map291-1.0.0-SNAPSHOT";
			final var mySession = "mySession";
			exchange.getIn().setBody(kieService.executeCommands(containerId, facts, mySession));
		} else {
			log.info("Nothing to process");
		}
	}

	private ClientAccts mapImportClientAccts(List<String> line) {
		ClientAccts cliAct = new ClientAccts();
		cliAct.setClientSeqNo(Long.valueOf(line.get(1)));
		cliAct.setShortDesc(line.get(2));
		cliAct.setLongDesc(line.get(3));
		cliAct.setCRC1(line.get(4));
		cliAct.setUDF1(line.get(5));
		cliAct.setUDF2(line.get(6));
		return cliAct;
	}
}
