package ec.viajero.IdentityServer.dto;

import java.util.Objects;

import javax.validation.constraints.NotNull;


import ec.viajero.IdentityServer.model.EmailTemplate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EmailTemplateDTO {
    private Long id;

    @NotNull
    private String subject;

    @NotNull
    private String body;

    @NotNull
    private String enSubject;

    @NotNull
    private String enBody;
    
    @NotNull
    private String type;

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof EmailTemplate)) {
            return false;
        }
        return Objects.equals(id, ((EmailTemplateDTO) o).id);
    }

    @Override
    public String toString() {
        return "EmailTemplate {" +
                "id="+id+ '\'' +
                "subject='" + subject + '\'' +
                "type='" + type + '\'' +
                "body='" + body + '\'' +
                "}";
    }
}