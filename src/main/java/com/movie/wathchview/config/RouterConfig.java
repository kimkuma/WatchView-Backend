package com.movie.wathchview.config;

import com.movie.wathchview.handler.FirstHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.*;

@Configuration
public class RouterConfig {
    @Bean
    public RouterFunction<ServerResponse> route(FirstHandler handler) {
        return RouterFunctions.route(RequestPredicates.GET("/hello").and(RequestPredicates.accept(MediaType.TEXT_PLAIN)), handler::hello);
    }
}
