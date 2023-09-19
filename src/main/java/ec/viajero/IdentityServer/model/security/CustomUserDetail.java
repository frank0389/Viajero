package ec.viajero.IdentityServer.model.security;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;


public class CustomUserDetail implements UserDetails {

    private String username;
    private String password;
    private String firstName;
    private String lastName;
    private String dni;
    private String email;
    private String phone;
    private String langKey;
    private List<GrantedAuthority> grantedAuthorities;

    public CustomUserDetail(String username, String password, String firstName, 
                            String lastName, String dni, String email, String phone, String langKey, List<GrantedAuthority> grantedAuthorities  ) {
           this.username = username;
           this.password=password;
           this.firstName=firstName;
           this.lastName=lastName;
           this.dni=dni;
           this.email=email;
           this.phone=phone;
           this.langKey= langKey;
           this.grantedAuthorities= grantedAuthorities;
    }
    
    @Override
    public Collection<GrantedAuthority> getAuthorities() {
        return grantedAuthorities;
    }

    @Override
    public String getPassword() {
        return password;
       
    }

    @Override
    public String getUsername() {
      return username;
    }

    @Override
    public boolean isAccountNonExpired() {
       return true;
    }

    @Override
    public boolean isAccountNonLocked() {
       return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
       return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getLangKey() {
        return langKey;
    }

   
    public void setLangKey(String langKey) {
        this.langKey = langKey;
    }

    @Override
    public String toString() {
        return "CustomUserDetail {" +
                ", userName='" + username + '\'' +
                ", password='" + "*********" + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", dni='" + dni + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ",authorities=" + grantedAuthorities +
                '}';
    }   

  

}