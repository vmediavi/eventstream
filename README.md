Start Kafka:
docker-compose up -d

Run Spring Boot:
./mvnw spring-boot:run

Open your browser:
http://localhost:8080

Produce test events to Kafka:
docker exec -it <kafka-container-id> kafka-console-producer \
--broker-list localhost:9092 --topic records

Example message:
{"type":"new", "payload":{"id":"10", "name":"User 10"}}

{"type":"delete", "payload":{"id":"10", "name":"User 10"}}

{"type":"update", "payload":{"id":"10", "name":"User 999"}}

