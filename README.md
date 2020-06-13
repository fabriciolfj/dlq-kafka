# Exemplo DQL em kafka

###### Configurações
- O consumidor deve confirmar o recebimento manualmente da mensagem.
- Neste exemplo adotei o MANUAL_IMMEDIATE, informe imediatamente o broker que o consumidor, processou a mensagem com êxito.
- Caso dê algum erro durante o processamento, antes da confirmação, o sistema tentará consumir a mensagem novamente.