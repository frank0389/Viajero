package ec.viajero.IdentityServer.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class JWTTokenDTO {

    @JsonProperty("id_token")
    private String idToken;

    
    @Override
    public String toString() {
        return "JWTTokenDTO{" +
                "idToken='" + idToken + '\'' +
                '}';
    }

}