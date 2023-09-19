package ec.viajero.IdentityServer.model;
import java.time.Instant;
import java.util.List;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import io.r2dbc.postgresql.codec.Json;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name="UM_USER")
public class User {

    @Id
    private Long id;

    @NotNull
    @Column("user_uuid")
    private String uuid;

    @NotNull
    @Column("user_name")
    private String userName;

    @NotNull
    @Column("password_hash")
    private String password;

    @NotNull
    @Column("first_name")
    private String firstName;

    @Column("last_name")
    private String lastName;

    @NotNull
    @Column("dni")
    private String dni;

    @NotNull
    @Email
    @Column("email")
    private String email;

    @NotNull
    @Column("phone")
    private String phone;

    @Column("company")
    private String company;

    @Column("activated")
    private Boolean activated;

    @Column("block")
    private Boolean block;

    @Column("lang_key")
    private String langKey;

    @Column("activation_key")
    private String activationKey;

    @Column("reset_key")
    private String resetKey;

    @Column("reset_date")
    private Instant resetDate;

    @Column("created_by")
    private String createdBy;

    @Column("created_date")
    private Instant createdDate;

    @Column("last_modified_by")
    private String lastModifiedBy; 

    @Column("last_modified_date")
    private Instant lastModifiedDate; 
    
    @Column("metadata")
    private Json metadata;

    @Transient
    private List<Role> roles;


    @Override
    public boolean equals(Object o) {
            
        if (this == o) {
            return true;
        }
        if (!(o instanceof User)) {
            return false;
        }
        return id != null && id.equals(((User) o).id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    public static String columnName(String property){

            if(property.equalsIgnoreCase("userName"))
                return "user_name";
                if(property.equalsIgnoreCase("firstName"))
                return "first_name";
                if(property.equalsIgnoreCase( "lastName"))
                return "last_name";
                return property;
    }

    @Override
    public String toString() {
        return "User {" +
                "id=" + id +
                ", uuid='" + uuid + '\'' +
                ", userName='" + userName + '\'' +
                ", password='" + "*********" + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", dni='" + dni + '\'' +
                ", email='" + email + '\'' +
                ", company='" + company + '\'' +
                ", activated='" + activated + '\'' +
                ", createdBy='" + createdBy + '\'' +
                ", createdDate='" + createdDate + '\'' +
                ", metadata='" + metadata + '\'' +
                ", roles=" + roles +
                '}';
    }   
    
}