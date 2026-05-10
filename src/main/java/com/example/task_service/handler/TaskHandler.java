package com.example.task_service.handler;

import com.example.task_service.data.dto.request.AssignmentRequestDto;
import com.example.task_service.data.dto.request.StatusRequestDto;
import com.example.task_service.data.dto.request.TaskRequestDto;
import com.example.task_service.data.dto.response.ErrorResponseDto;
import com.example.task_service.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
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
            @Parameter(name = "page", description = "A zero-based page number. The default is 0."),
            @Parameter(name = "size", description = "A number of records per page. The default is 20.")
    }, description = "Retrieves tasks.")
    @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json"), description = "The task page.")
    public Mono<ServerResponse> findAll(ServerRequest request) {
        return Mono.fromCallable(() -> getPageable(request))
                .map(service::findAll)
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(tasks -> ServerResponse.ok().bodyValue(tasks));
    }

    private static PageRequest getPageable(ServerRequest request) {
        int page = request.queryParam("page").map(Integer::parseInt).orElse(0);
        int size = request.queryParam("size").map(Integer::parseInt).orElse(20);
        return PageRequest.of(page, size);
    }

    @Operation(parameters = @Parameter(in = ParameterIn.PATH, name = "id", required = true),
            description = "Retrieves a task by its id.")
    @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json"), description = "The task.")
    @ApiResponse(responseCode = "400", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class)),
            description = "If `id` does not conform to the UUID format.")
    @ApiResponse(responseCode = "404", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class)),
            description = "If `id` does not match any existing task.")
    public Mono<ServerResponse> findById(ServerRequest request) {
        return Mono.fromCallable(() -> UUID.fromString(request.pathVariable("id")))
                .map(service::findById)
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(Mono::justOrEmpty)
                .switchIfEmpty(Mono.error(new EntityNotFoundException("No such task.")))
                .flatMap(task -> ServerResponse.ok().bodyValue(task))
                .onErrorResume(EntityNotFoundException.class, t -> ServerResponse.status(HttpStatus.NOT_FOUND).bodyValue(ErrorResponseDto.from(t)))
                .onErrorResume(IllegalArgumentException.class, t -> ServerResponse.badRequest().bodyValue(ErrorResponseDto.from(t)));
    }

    @Operation(requestBody = @RequestBody(content = @Content(schema = @Schema(implementation = TaskRequestDto.class)), required = true),
            description = "Creates a task.")
    @ApiResponse(responseCode = "201", content = @Content(mediaType = "application/json"), description = "The created task.")
    public Mono<ServerResponse> save(ServerRequest request) {
        return request.bodyToMono(TaskRequestDto.class)
                .map(service::save)
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(task -> ServerResponse.status(HttpStatus.CREATED).bodyValue(task));
    }

    @Operation(parameters = @Parameter(in = ParameterIn.PATH, name = "id", required = true),
            requestBody = @RequestBody(content = @Content(schema = @Schema(implementation = AssignmentRequestDto.class)), required = true),
            description = "Assigns a task matching the provided id to the specified user.")
    @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json"), description = "The assigned task.")
    @ApiResponse(responseCode = "404", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class)),
            description = "If `id` or `userId` does not match any existing task or user respectively.")
    public Mono<ServerResponse> updateAssignee(ServerRequest request) {
        return request.bodyToMono(AssignmentRequestDto.class)
                .map(AssignmentRequestDto::getUserId)
                .map(userId -> service.updateAssignee(UUID.fromString(request.pathVariable("id")), userId))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(task -> ServerResponse.ok().bodyValue(task))
                .onErrorResume(EntityNotFoundException.class, t -> ServerResponse.status(HttpStatus.NOT_FOUND).bodyValue(ErrorResponseDto.from(t)));
    }

    @Operation(parameters = @Parameter(in = ParameterIn.PATH, name = "id", required = true),
            requestBody = @RequestBody(content = @Content(schema = @Schema(implementation = StatusRequestDto.class)), required = true),
            description = """
                    Updates a task's status given its id and a new status.
                    
                    Supported statuses: TO_DO, IN_PROGRESS, DONE.""")
    @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json"), description = "The updated task.")
    @ApiResponse(responseCode = "400", content = @Content(mediaType = "application/json"),
            description = "If `status` is invalid.")
    @ApiResponse(responseCode = "404", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class)),
            description = "If `id` does not match any existing task.")
    public Mono<ServerResponse> updateStatus(ServerRequest request) {
        return request.bodyToMono(StatusRequestDto.class)
                .map(StatusRequestDto::getStatus)
                .map(status -> service.updateStatus(UUID.fromString(request.pathVariable("id")), status))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(task -> ServerResponse.ok().bodyValue(task))
                .onErrorResume(EntityNotFoundException.class, t -> ServerResponse.status(HttpStatus.NOT_FOUND).bodyValue(ErrorResponseDto.from(t)));
    }
}
