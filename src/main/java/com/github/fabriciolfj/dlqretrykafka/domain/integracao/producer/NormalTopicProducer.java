package com.github.fabriciolfj.dlqretrykafka.domain.integracao.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fabriciolfj.dlqretrykafka.domain.model.Customer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
@Slf4j
public class NormalTopicProducer {

    private final KafkaTemplate<String, String> template;
    private final ObjectMapper mapper;

    public void send(final Customer customer) throws JsonProcessingException {
        var json = mapper.writeValueAsString(customer);
        template.send("normal-topic", json);
    }
}
