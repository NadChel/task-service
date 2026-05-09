package com.example.task_service.handler;

import com.example.task_service.data.dto.request.UserRequestDto;
import com.example.task_service.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Component
@RequiredArgsConstructor
public class UserHandler {

    private final UserService service;

    @Operation(requestBody = @RequestBody(content = @Content(schema = @Schema(implementation = UserRequestDto.class)), required = true),
            description = "Creates user.")
    @ApiResponse(responseCode = "201", content = @Content(mediaType = "application/json"), description = "A created user.")
    public Mono<ServerResponse> save(ServerRequest request) {
        return request.bodyToMono(UserRequestDto.class)
                .map(service::save)
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(user -> ServerResponse.status(HttpStatus.CREATED).bodyValue(user));
    }
}
