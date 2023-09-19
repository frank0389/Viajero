package ec.viajero.IdentityServer.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ValidateJWTTokenDTO {
    private boolean valid;
    private String message;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ValidateJWTTokenDTO)) {
            return false;
        }
        return this.valid == (((ValidateJWTTokenDTO) o).valid);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }


    @Override
    public String toString() {
        return "ValidateJWTTokenDTO {" +
                "valid=" + valid +
                ",message=" + message +
                '}';
    }   
}