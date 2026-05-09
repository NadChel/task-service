package com.example.task_service.configuration;

import com.example.task_service.handler.TaskHandler;
import com.example.task_service.handler.UserHandler;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springdoc.core.annotations.RouterOperation;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.PATCH;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;

@Configuration
@RequiredArgsConstructor
public class WebConfig {

    private final TaskHandler taskHandler;
    private final UserHandler userHandler;

    @Bean
    @RouterOperation(beanClass = TaskHandler.class, beanMethod = "findAll")
    public RouterFunction<@NotNull ServerResponse> findTasks() {
        return RouterFunctions
                .route(GET("/tasks"), taskHandler::findAll);
    }

    @Bean
    @RouterOperation(beanClass = TaskHandler.class, beanMethod = "findById")
    public RouterFunction<@NotNull ServerResponse> findTask() {
        return RouterFunctions
                .route(GET("/tasks/{id}"), taskHandler::findById);
    }

    @Bean
    @RouterOperation(beanClass = TaskHandler.class, beanMethod = "save")
    public RouterFunction<@NotNull ServerResponse> createTask() {
        return RouterFunctions
                .route(POST("/tasks"), taskHandler::save);
    }

    @Bean
    @RouterOperation(beanClass = TaskHandler.class, beanMethod = "updateAssignee")
    public RouterFunction<@NotNull ServerResponse> updateAssignee() {
        return RouterFunctions
                .route(PATCH("/tasks/{id}/assignee"), taskHandler::updateAssignee);
    }

    @Bean
    @RouterOperation(beanClass = TaskHandler.class, beanMethod = "updateStatus")
    public RouterFunction<@NotNull ServerResponse> updateStatus() {
        return RouterFunctions
                .route(PATCH("/tasks/{id}/status"), taskHandler::updateStatus);
    }

    @Bean
    @RouterOperation(beanClass = UserHandler.class, beanMethod = "save")
    public RouterFunction<@NotNull ServerResponse> saveUser() {
        return RouterFunctions
                .route(POST("/users"), userHandler::save);
    }
}
