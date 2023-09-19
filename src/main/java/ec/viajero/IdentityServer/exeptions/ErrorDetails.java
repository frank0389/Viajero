package ec.viajero.IdentityServer.exeptions;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class ErrorDetails {
    private Long errorCode;
    private String errorMessage;
    private String referenceUrl;
    
    @Override
    public String toString() {
        return "ErrorDetails {" +
                "errorCode='" + errorCode + '\'' +
                ",errorMessage='" + errorMessage + '\'' +
                ",referenceUrl='" + referenceUrl + '\'' +
                "}";
    }
}