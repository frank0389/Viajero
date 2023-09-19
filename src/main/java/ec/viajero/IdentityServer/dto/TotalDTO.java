package ec.viajero.IdentityServer.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TotalDTO {
    private long total;

    @Override
    public String toString() {
        return "TotalDTO {" +
                "total='" + total + '\'' +
                "}";
    }
}