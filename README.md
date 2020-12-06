# Exemplo DQL em kafka

###### Configurações
- O consumidor deve confirmar o recebimento manualmente da mensagem.
- Neste exemplo adotei o MANUAL_IMMEDIATE, informe imediatamente o broker que o consumidor, processou a mensagem com êxito.
- Caso dê algum erro durante o processamento, antes da confirmação, o sistema tentará consumir a mensagem novamente.

- Link para referencia ao retry em kafka, usando spring cloud stream https://docs.spring.io/spring-cloud-stream/docs/Chelsea.RC1/reference/html/_apache_kafka_binder.html
