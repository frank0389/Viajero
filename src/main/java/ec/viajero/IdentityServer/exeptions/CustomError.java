package ec.viajero.IdentityServer.exeptions;

import java.util.List;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomError {
    private String traceId;
    private Long timestamp;
    private HttpStatus status;
    private List<ErrorDetails> errors;
}