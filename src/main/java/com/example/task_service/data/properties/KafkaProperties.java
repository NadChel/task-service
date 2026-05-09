package com.example.task_service.data.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Getter
@Setter
@ConfigurationProperties(prefix = "spring.kafka")
public class KafkaProperties {

    private List<String> bootstrapServers;
}
