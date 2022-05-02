package com.sergiuoltean.kie;

import com.castine.timload.map291.ClientAccts;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class ResultProcessor {

  public byte[] process(List<ClientAccts> list) throws JsonProcessingException {
    CsvMapper csvMapper = new CsvMapper();
    CsvSchema schema = csvMapper.schemaFor(ClientAccts.class).withHeader().withoutQuoteChar()
            .withColumnSeparator(',');
    return csvMapper.writer(schema).writeValueAsBytes(list);
  }

}
