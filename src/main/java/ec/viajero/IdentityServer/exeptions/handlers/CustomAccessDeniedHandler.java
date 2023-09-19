package ec.viajero.IdentityServer.exeptions.handlers;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.server.authorization.ServerAccessDeniedHandler;
import org.springframework.web.server.ServerWebExchange;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import ec.viajero.IdentityServer.exeptions.CustomError;
import ec.viajero.IdentityServer.exeptions.ErrorCodes;
import ec.viajero.IdentityServer.exeptions.ErrorDetails;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
public class CustomAccessDeniedHandler implements ServerAccessDeniedHandler {

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, AccessDeniedException exception)  {
       ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.FORBIDDEN);
        response.getHeaders().add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        ObjectMapper objectMapper = new ObjectMapper();
      
        CustomError error =  CustomError.builder().traceId(UUID.randomUUID().toString())
                             .status(HttpStatus.FORBIDDEN)
                             .timestamp(new Date().getTime())
                             .errors(List.of(new ErrorDetails(ErrorCodes.NOT_AUTHORIZED_EC,"Not authorized","https//error/details#123")))
                             .build();
        log.error("AccessDeniedException was throw with message >> {} and error {}", exception.getMessage(),error.toString());         

        String responseBody = "{}";
        try {
            responseBody = objectMapper.writeValueAsString(error);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        byte[] bytes = responseBody.getBytes(StandardCharsets.UTF_8);
        DataBuffer buffer = response.bufferFactory().wrap(bytes);
        return response.writeWith(Mono.just(buffer));
    }
    
  
}