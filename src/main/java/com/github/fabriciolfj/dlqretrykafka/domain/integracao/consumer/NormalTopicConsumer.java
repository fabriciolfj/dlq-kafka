package com.github.fabriciolfj.dlqretrykafka.domain.integracao.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Header;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import static java.nio.charset.StandardCharsets.UTF_8;

@RequiredArgsConstructor
@Component
@Slf4j
public class NormalTopicConsumer {

    public static final String RETRY_COUNT_HEADER_KEY = "retryCount";
    public static final String ORIGINAL_TOPIC_HEADER_KEY = "originalTopic";
    public static final String DLQ_TOPIC = "dlq-topic";

    private final KafkaTemplate<String, String> kafkaTemplate;

    @KafkaListener(id = "normal-topic-consumer", groupId = "normal-topic-group", topics = "normal-topic")
    public void consume(final ConsumerRecord<?,?> consumerRecord, final Acknowledgment ack) {
        final String json = consumerRecord.value().toString();
        try {
            log.info("Consumindo mensagem normal {}", json);
            throw new RuntimeException("Simulando uma falha");
        } catch (Exception e) {
            log.info("Enviando a mensagem para dlq");
            final String topicOriginal = consumerRecord.topic();
            final ProducerRecord<String, String> record = new ProducerRecord<>(DLQ_TOPIC, json);
            record.headers().add(ORIGINAL_TOPIC_HEADER_KEY, topicOriginal.getBytes(UTF_8));

            Header retryCount = consumerRecord.headers().lastHeader(RETRY_COUNT_HEADER_KEY);

            if(retryCount != null) {
                record.headers().add(retryCount);
            }

            kafkaTemplate.send(record);
        } finally {
            ack.acknowledge();
        }
    }
}
