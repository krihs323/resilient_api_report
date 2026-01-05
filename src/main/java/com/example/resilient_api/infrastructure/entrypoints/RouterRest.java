package com.example.resilient_api.infrastructure.entrypoints;

import com.example.resilient_api.infrastructure.entrypoints.handler.ReportHandlerImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springdoc.webflux.core.fn.SpringdocRouteBuilder.route;


@Configuration
public class RouterRest {

    @Bean
    public RouterFunction<ServerResponse> routerFunction(ReportHandlerImpl personHandler) {
        return route()
                .POST("/report",
                        personHandler::createReport,
                        ops -> ops.beanClass(ReportHandlerImpl.class).beanMethod("createReport"))
                .PUT("/report",
                        personHandler::updateReport,
                        ops -> ops.beanClass(ReportHandlerImpl.class).beanMethod("updateReport"))
                .build();
    }
}