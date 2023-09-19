package ec.viajero.IdentityServer.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class KeyAndPasswordDTO {
    private String key;
    private String newPassword;

    @Override
    public String toString() {
        return "KeyAndPasswordDTO{" +
                "key='" + key + '\'' +
                ",newPassword= [*******]" +
                '}';
    }
}