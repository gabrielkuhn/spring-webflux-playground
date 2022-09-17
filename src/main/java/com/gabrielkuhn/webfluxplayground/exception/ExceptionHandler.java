package com.gabrielkuhn.webfluxplayground.exception;

import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.*;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.Optional;

@Order(-2)
@Component
public class ExceptionHandler extends AbstractErrorWebExceptionHandler {


    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    public ExceptionHandler(ErrorAttributes errorAttributes, WebProperties webproperties,
                            ApplicationContext applicationContext, ServerCodecConfigurer configurer) {
        super(errorAttributes, webproperties.getResources(), applicationContext);
        this.setMessageWriters(configurer.getWriters());
    }

    @SuppressWarnings("NullableProblems")
    @Override
    protected RouterFunction<ServerResponse> getRoutingFunction(ErrorAttributes errorAttributes) {
        return RouterFunctions.route(RequestPredicates.all(), this::renderErrorResponse);
    }

    private Mono<ServerResponse> renderErrorResponse(ServerRequest request) {

        Map<String, Object> errorPropertiesMap = getErrorAttributes(request,
                ErrorAttributeOptions.of(ErrorAttributeOptions.Include.MESSAGE));

        int httpStatus = (int) Optional.ofNullable(errorPropertiesMap.get("status"))
                .orElse(HttpStatus.INTERNAL_SERVER_ERROR.value());

        return ServerResponse.status(httpStatus)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(errorPropertiesMap));
    }
}
