package ec.viajero.IdentityServer.dto;

import java.util.List;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.JsonNode;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;



@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDTO {

    private String uuid;

    @NotNull
    @Size(max = 50)
    private String userName;

    private String password;

    @NotNull
    @Size(max = 50)
    private String firstName;

    @Size(max = 50)
    private String lastName;

    @NotNull
    @Size(max = 255)
    private String dni;

    @NotNull
    @Email
    @Size(max = 320)
    private String email;

    @NotNull
    private String phone;

    private String langKey;

    private String company;

    private JsonNode metadata; 
    
    private List<RoleDTO> roles; 
    
    private boolean block;

    private boolean activated;

    @JsonIgnore
    private String resetKey;

    @JsonIgnore
    private String activationKey;


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UserDTO)) {
            return false;
        }
        return this.uuid != null && this.uuid.equals(((UserDTO) o).uuid);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }


    @Override
    public String toString() {
        return "UserDTO {" +
                "uuid=" + uuid +
                ", userName='" + userName + '\'' +
                ", password='" + "*********" + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", dni='" + dni + '\'' +
                ", email='" + email + '\'' +
                ", company='" + company + '\'' +
                ", activated='" + activated + '\'' +
                ", block='" + block + '\'' +
                ", metadata='" + metadata + '\'' +
                ", roles=" + roles +
                '}';
    }   


}