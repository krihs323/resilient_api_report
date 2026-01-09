package com.example.resilient_api.infrastructure.entrypoints.handler;

import com.example.resilient_api.domain.api.ReportServicePort;
import com.example.resilient_api.domain.enums.TechnicalMessage;
import com.example.resilient_api.domain.exceptions.BusinessException;
import com.example.resilient_api.domain.exceptions.CustomException;
import com.example.resilient_api.domain.exceptions.TechnicalException;
import com.example.resilient_api.infrastructure.entrypoints.dto.BootcampReportDTO;
import com.example.resilient_api.infrastructure.entrypoints.dto.ReportDTO;
import com.example.resilient_api.infrastructure.entrypoints.dto.UpdatePersonReportRequestDTO;
import com.example.resilient_api.infrastructure.entrypoints.mapper.BootcampReportMapper;
import com.example.resilient_api.infrastructure.entrypoints.mapper.ReportMapper;
import com.example.resilient_api.infrastructure.entrypoints.mapper.UpdatePersonReportRequestMapper;
import com.example.resilient_api.infrastructure.entrypoints.util.APIResponse;
import com.example.resilient_api.infrastructure.entrypoints.util.ErrorDTO;
import com.example.resilient_api.infrastructure.validation.ObjectValidator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;

import java.time.Instant;
import java.util.List;

import static com.example.resilient_api.infrastructure.entrypoints.util.Constants.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class ReportHandlerImpl {

    private final ReportServicePort reportServicePort;
    private final ReportMapper reportMapper;
    private final ObjectValidator objectValidator;
    private final UpdatePersonReportRequestMapper updatePersonReportRequestMapper;
    private final BootcampReportMapper bootcampReportMapper;


    @Operation(
            summary = "Registrar un nuevo reporte",
            description = "Crea un reporte ",
            requestBody = @RequestBody(
                    description = "Información de la reporta a registrar",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ReportDTO.class)
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "201", description = "Reporte creado exitosamente"),
                    @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
            })
    public Mono<ServerResponse> createReport(ServerRequest request) {
        String messageId = getMessageId(request);
        return request.bodyToMono(ReportDTO.class).flatMap(objectValidator::validate)
                .flatMap(report ->
                        reportServicePort.registerReport(reportMapper.reportDTOToReport(report), messageId)
                                .doOnSuccess(v -> log.info("Report created successfully with messageId: {}", messageId))
                                .then(ServerResponse
                                        .status(HttpStatus.CREATED)
                                        .bodyValue(TechnicalMessage.REPORT_CREATED.getMessage()))
                )
                .contextWrite(Context.of(X_MESSAGE_ID, messageId))
                .doOnError(ex -> log.error(REPORT_ERROR, ex))
                .onErrorResume(BusinessException.class, ex -> buildErrorResponse(
                        HttpStatus.BAD_REQUEST,
                        messageId,
                        TechnicalMessage.INVALID_PARAMETERS,
                        List.of(ErrorDTO.builder()
                                .code(ex.getTechnicalMessage().getCode())
                                .message(ex.getTechnicalMessage().getMessage())
                                .param(ex.getTechnicalMessage().getParam())
                                .build())))
                .onErrorResume(TechnicalException.class, ex -> buildErrorResponse(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        messageId,
                        TechnicalMessage.INTERNAL_ERROR,
                        List.of(ErrorDTO.builder()
                                .code(ex.getTechnicalMessage().getCode())
                                .message(ex.getTechnicalMessage().getMessage())
                                .param(ex.getTechnicalMessage().getParam())
                                .build())))
                .onErrorResume(CustomException.class, ex -> buildErrorResponse(
                        HttpStatus.BAD_REQUEST,
                        messageId,
                        TechnicalMessage.INVALID_REQUEST,
                        List.of(ErrorDTO.builder()
                                .code(TechnicalMessage.INVALID_REQUEST.getCode())
                                .message(ex.getMessage())
                                .build())))
                .onErrorResume(ex -> {
                    log.error("Unexpected error occurred for messageId: {}", messageId, ex);
                    return buildErrorResponse(
                            HttpStatus.INTERNAL_SERVER_ERROR,
                            messageId,
                            TechnicalMessage.INTERNAL_ERROR,
                            List.of(ErrorDTO.builder()
                                    .code(TechnicalMessage.INTERNAL_ERROR.getCode())
                                    .message(TechnicalMessage.INTERNAL_ERROR.getMessage())
                                    .build()));
                });
    }

    @Operation(
            summary = "Actualizar un reporte de Bootcamp, agregando personas registradas",
            description = "Actualiza un reporte",
            requestBody = @RequestBody(
                    description = "Información del reporta a actualizar",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ReportDTO.class)
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "201", description = "Reporte creado exitosamente"),
                    @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
            })
    public Mono<ServerResponse> updateReport(ServerRequest request) {
        String messageId = getMessageId(request);
        return request.bodyToMono(UpdatePersonReportRequestDTO.class).flatMap(objectValidator::validate)
                .flatMap(report ->
                        reportServicePort.updateReport(updatePersonReportRequestMapper.personDTOToPerson(report), messageId)
                                .doOnSuccess(v -> log.info("Report created successfully with messageId: {}", messageId))
                                .then(ServerResponse
                                        .status(HttpStatus.CREATED)
                                        .bodyValue(TechnicalMessage.REPORT_UPDATED.getMessage()))
                )
                .contextWrite(Context.of(X_MESSAGE_ID, messageId))
                .doOnError(ex -> log.error(REPORT_ERROR, ex))
                .onErrorResume(BusinessException.class, ex -> buildErrorResponse(
                        HttpStatus.BAD_REQUEST,
                        messageId,
                        TechnicalMessage.INVALID_PARAMETERS,
                        List.of(ErrorDTO.builder()
                                .code(ex.getTechnicalMessage().getCode())
                                .message(ex.getTechnicalMessage().getMessage())
                                .param(ex.getTechnicalMessage().getParam())
                                .build())))
                .onErrorResume(TechnicalException.class, ex -> buildErrorResponse(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        messageId,
                        TechnicalMessage.INTERNAL_ERROR,
                        List.of(ErrorDTO.builder()
                                .code(ex.getTechnicalMessage().getCode())
                                .message(ex.getTechnicalMessage().getMessage())
                                .param(ex.getTechnicalMessage().getParam())
                                .build())))
                .onErrorResume(CustomException.class, ex -> buildErrorResponse(
                        HttpStatus.BAD_REQUEST,
                        messageId,
                        TechnicalMessage.INVALID_REQUEST,
                        List.of(ErrorDTO.builder()
                                .code(TechnicalMessage.INVALID_REQUEST.getCode())
                                .message(ex.getMessage())
                                .build())))
                .onErrorResume(ex -> {
                    log.error("Unexpected error occurred for messageId: {}", messageId, ex);
                    return buildErrorResponse(
                            HttpStatus.INTERNAL_SERVER_ERROR,
                            messageId,
                            TechnicalMessage.INTERNAL_ERROR,
                            List.of(ErrorDTO.builder()
                                    .code(TechnicalMessage.INTERNAL_ERROR.getCode())
                                    .message(TechnicalMessage.INTERNAL_ERROR.getMessage())
                                    .build()));
                });
    }

    @Operation(parameters = {})
    public Mono<ServerResponse> listTopBootcamps(ServerRequest request) {
        String messageId = getMessageId(request);
        Flux<BootcampReportDTO> resultMono = reportServicePort.listTopBootcamps(messageId).map(bootcampReportMapper::bootcampReportToBootcampReportDTO);
        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(resultMono, BootcampReportDTO.class);

    }


    private Mono<ServerResponse> buildErrorResponse(HttpStatus httpStatus, String identifier, TechnicalMessage error,
                                                    List<ErrorDTO> errors) {
        return Mono.defer(() -> {
            APIResponse apiErrorResponse = APIResponse
                    .builder()
                    .code(error.getCode())
                    .message(error.getMessage())
                    .identifier(identifier)
                    .date(Instant.now().toString())
                    .errors(errors)
                    .build();
            return ServerResponse.status(httpStatus)
                    .bodyValue(apiErrorResponse);
        });
    }

    private String getMessageId(ServerRequest serverRequest) {
        return serverRequest.headers().firstHeader(X_MESSAGE_ID);
    }
}
