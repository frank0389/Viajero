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
public class BlockUserDTO {
    @NotNull
    private String uuid;
    @NotNull
    private Boolean block; 

    @Override
    public String toString() {
        return "BlockUserDTO {" +
                "uuid="+uuid+ '\'' +
                "block='" + block + '\'' +
                "}";
    }
}