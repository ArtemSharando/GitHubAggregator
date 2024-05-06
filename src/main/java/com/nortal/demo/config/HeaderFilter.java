package com.nortal.demo.config;

import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

@Component
@Order(-1)
public class HeaderFilter implements WebFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        var request = exchange.getRequest();
        var response = exchange.getResponse();

        String acceptHeader = request.getHeaders().getFirst("Accept");

        if (!MediaType.APPLICATION_JSON_VALUE.equals(acceptHeader)) {
            response.setStatusCode(HttpStatus.NOT_ACCEPTABLE);
            String message = String.format("{\"status\": %d, \"Message\": \"Invalid Accept header\"}", HttpStatus.NOT_ACCEPTABLE.value());
            DataBuffer buffer = response.bufferFactory().wrap(message.getBytes(StandardCharsets.UTF_8));
            return response.writeWith(Mono.just(buffer));
        }

        return chain.filter(exchange);
    }
}