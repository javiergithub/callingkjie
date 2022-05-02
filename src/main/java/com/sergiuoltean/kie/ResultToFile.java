package com.sergiuoltean.kie;

import com.castine.timload.map291.ClientAccts;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

@Component
public class ResultToFile implements Processor {

	private final ResultProcessor resultProcessor;

	public ResultToFile(ResultProcessor resultProcessor) {
		this.resultProcessor = resultProcessor;
	}

	@Override
	public void process(Exchange exchange) throws Exception {
		final Message message = exchange.getIn();
		byte[] result = getCsvBytes(message.getBody(List.class));
		Files.write(Paths.get("src/main/resources/done/ready.csv"), result);
	}

	private byte[] getCsvBytes(List<ClientAccts> list) throws JsonProcessingException {
		if (!list.isEmpty()) {
			return resultProcessor.process(list);
		} else {
			return "".getBytes();
		}
	}
}