# Tasks Service

A demo service designed to create tasks, assign them, and update their statuses.

## Getting Started

1. Clone. Make sure Git is installed.
```shell
git clone https://github.com/NadChel/task-service
```
2. Launch Docker Compose.
3. Run `docker-compose` located in the root of the project.
```shell
docker-compose up
```
4. Make sure the service is running. The logs should include a message similar to this.
```none
task-service-1  | 2026-05-10T16:51:06.360Z  INFO 1 --- [task-service] [           main] c.e.task_service.TaskServiceApplication  : Started TaskServiceApplication in 9.41 seconds (process running for 10.321)
```
5. Open `http://localhost:8080/swagger-ui.html` in your web browser of choice and issue requests.
6. *(optional)* Creating tasks and assigning them to users sends events to a `tasks` Kafka topic. To consume them, you may execute this command in a separate terminal, specifying the Kafka container id (e.g. copied from the Docker Desktop GUI).
```shell
docker exec -it <KAFKA CONTAINER ID> /opt/kafka/bin/kafka-console-consumer.sh --topic tasks --from-beginning --bootstrap-server localhost:9092
```
Example message on task creation:
```json
{"type":"CREATED","task":{"id":"3b3a001d-1d15-4d75-a064-b9c5bd021977","name":"Fix ABC","status":"TO_DO","assigneeId":null,"description":"Fix ABC ASAP"}}
```

## Notes

* For simplicity, the service also exposes an endpoint to create new users (potential assignees). In a real-world scenario, it would be a responsibility of a separate service.
* All endpoints are public: security concerns are beyond the scope of this demo.
* Ensure the ports 8080, 5432, 9092 are available or else reconfigure them in `docker-compose.yaml`.