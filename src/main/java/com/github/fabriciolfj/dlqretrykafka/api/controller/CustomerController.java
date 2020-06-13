package com.github.fabriciolfj.dlqretrykafka.api.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fabriciolfj.dlqretrykafka.domain.integracao.producer.NormalTopicProducer;
import com.github.fabriciolfj.dlqretrykafka.domain.model.Customer;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/customers")
public class CustomerController {

    private final NormalTopicProducer producer;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void create(@RequestBody Customer customer) throws JsonProcessingException {
        producer.send(customer);
    }
}
