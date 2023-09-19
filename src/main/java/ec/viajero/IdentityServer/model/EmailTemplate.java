package ec.viajero.IdentityServer.model;

import java.util.Objects;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name="UM_EMAIL_TEMPLATE")
public class EmailTemplate {
    
    @Id
    private Long id;

    @NotNull
    @Column("subject")
    private String subject;

    @NotNull
    @Length(min = 10, max = 2000)
    @Column("body")
    private String body;

    @NotNull
    @Column("en_subject")
    private String enSubject;

    @NotNull
    @Column("en_body")
    @Length(min = 10, max = 2000)
    private String enBody;

    @NotNull
    @Column("type")
    private Type type;

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
        return Objects.equals(type, ((EmailTemplate) o).type);
    }

    @Override
    public String toString() {
        return "EmailTemplate {" +
                "id="+id+ '\'' +
                "subject='" + subject + '\'' +
                "type='" + type + '\'' +
                "body='" + body + '\'' +
                "enSubject='" + enSubject + '\'' +
                "enBody='" + enBody + '\'' +
                "}";
    }

    public enum Type {
        AccountActivation,
        AccountConfirmation,
        PasswordReset,
        AccountRecovery,
        AccountLogin
    }
}