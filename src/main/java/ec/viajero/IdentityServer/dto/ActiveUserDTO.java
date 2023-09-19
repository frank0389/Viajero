package ec.viajero.IdentityServer.dto;

import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ActiveUserDTO {
    @NotNull
    private String uuid;
    
    @Override
    public String toString() {
        return "ActiveUserDTO {" +
                "uuid="+uuid+ '\'' +
                "}";
    }

}