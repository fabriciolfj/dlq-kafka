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

@Component
@Slf4j
@RequiredArgsConstructor
public class DlqTopicConsumer {

    public static final String RETRY_COUNT_HEADER_KEY = "retryCount";
    public static final String ORIGINAL_TOPIC_HEADER_KEY = "originalTopic";
    private final KafkaTemplate<String, String> kafkaTemplate;

    @KafkaListener(id = "dlq-topic-consumer", groupId = "dlq-topic-group", topics = "dlq-topic")
    public void consume(ConsumerRecord<?, ?> consumerRecord, Acknowledgment ack) {
        String json = consumerRecord.value().toString();
        try {
            Header originalTopicHeader = consumerRecord.headers().lastHeader(ORIGINAL_TOPIC_HEADER_KEY);

            if (originalTopicHeader != null) {
                String originalTopic = new String(originalTopicHeader.value(), UTF_8);
                Header retryCountHeader = consumerRecord.headers().lastHeader(RETRY_COUNT_HEADER_KEY);
                int retryCount = 0;

                if(retryCountHeader != null) {
                    retryCount = Integer.parseInt(new String(retryCountHeader.value(), UTF_8));
                }

                if(retryCount < 5) {
                    retryCount += 1;
                    log.info("Reenviando a mensagem, tentativa {}", retryCount);
                    ProducerRecord<String, String> record = new ProducerRecord<>(originalTopic, json);
                    byte[] retryCountHeaderInByte = Integer.valueOf(retryCount).toString().getBytes(UTF_8);
                    record.headers().add(RETRY_COUNT_HEADER_KEY, retryCountHeaderInByte);
                    log.info("Esperando 5 segundos para reenviar");
                    Thread.sleep(5000);
                    kafkaTemplate.send(record);
                    return;
                } else {
                    log.error("Limite de tentativas atingida {}", json);
                }

                return;
            }

            log.error("Não foi possível ler a mensagem DLQ porque está faltando o cabeçalho originalTopic \"");
        } catch (Exception e) {
            log.error("Não foi possível processar a mensagem DLQ {}", json);
        } finally {
            ack.acknowledge();
        }
    }
}
