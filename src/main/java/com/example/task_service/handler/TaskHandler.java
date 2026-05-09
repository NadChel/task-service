package com.example.task_service.handler;

import com.example.task_service.data.dto.request.AssigneeRequestDto;
import com.example.task_service.data.dto.request.StatusRequestDto;
import com.example.task_service.data.dto.request.TaskRequestDto;
import com.example.task_service.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class TaskHandler {

    private final TaskService service;

    @Operation(parameters = {
            @Parameter(name = "page", description = "Zero-based page number. Default is 0"),
            @Parameter(name = "size", description = "Number of records per page. Default is 20")
    }, description = "Retrieves tasks.")
    @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json"), description = "Task page.")
    public Mono<ServerResponse> findAll(ServerRequest request) {
        Pageable pageable = getPageable(request);
        return Mono.fromCallable(() -> service.findTasks(pageable))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(tasks -> ServerResponse.ok().bodyValue(tasks));
    }

    private static PageRequest getPageable(ServerRequest request) {
        return PageRequest.of(
                request.queryParam("page").map(Integer::parseInt).orElse(0),
                request.queryParam("size").map(Integer::parseInt).orElse(20)
        );
    }

    @Operation(parameters = @Parameter(in = ParameterIn.PATH, name = "id"), description = "Retrieves task by id.")
    @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json"), description = "Task.")
    public Mono<ServerResponse> findById(ServerRequest request) {
        return Mono.fromCallable(() -> UUID.fromString(request.pathVariable("id")))
                .map(service::findTask)
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(Mono::justOrEmpty)
                .flatMap(task -> ServerResponse.ok().bodyValue(task));
    }

    @Operation(requestBody = @RequestBody(content = @Content(schema = @Schema(implementation = TaskRequestDto.class)), required = true),
            description = "Creates task.")
    @ApiResponse(responseCode = "201", content = @Content(mediaType = "application/json"), description = "A created task.")
    public Mono<ServerResponse> save(ServerRequest request) {
        return request.bodyToMono(TaskRequestDto.class)
                .map(service::save)
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(task -> ServerResponse.status(HttpStatus.CREATED).bodyValue(task));
    }

    @Operation(parameters = @Parameter(in = ParameterIn.PATH, name = "id"),
            requestBody = @RequestBody(content = @Content(schema = @Schema(implementation = AssigneeRequestDto.class))),
            description = "Assigns a task with a provided id to a specified user.")
    @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json"), description = "Updated task.")
    public Mono<ServerResponse> updateAssignee(ServerRequest request) {
        return request.bodyToMono(AssigneeRequestDto.class)
                .map(assignee -> service.updateAssignee(UUID.fromString(request.pathVariable("id")), assignee))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(task -> ServerResponse.ok().bodyValue(task));
    }

    @Operation(parameters = @Parameter(in = ParameterIn.PATH, name = "id"),
            requestBody = @RequestBody(content = @Content(schema = @Schema(implementation = StatusRequestDto.class))),
            description = """
                    Updates task status given a task id and a new status.
                    
                    Supported values: TO_DO, IN_PROGRESS, DONE""")
    public Mono<ServerResponse> updateStatus(ServerRequest request) {
        return request.bodyToMono(StatusRequestDto.class)
                .map(status -> service.updateStatus(UUID.fromString(request.pathVariable("id")), status))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(task -> ServerResponse.ok().bodyValue(task));
    }
}
