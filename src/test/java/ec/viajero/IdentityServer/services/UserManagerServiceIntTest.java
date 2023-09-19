package ec.viajero.IdentityServer.services;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import ec.viajero.IdentityServer.IntegrationTest;
import ec.viajero.IdentityServer.dto.RoleDTO;
import ec.viajero.IdentityServer.dto.UserDTO;
import ec.viajero.IdentityServer.exeptions.EmailAlreadyUsedException;
import ec.viajero.IdentityServer.exeptions.UsernameAlreadyUsedException;
import ec.viajero.IdentityServer.model.User;
import lombok.extern.slf4j.Slf4j;
import reactor.test.StepVerifier;

@Slf4j
@IntegrationTest
public class UserManagerServiceIntTest {

    @Autowired
    private UserManagerService userManager;

    @Test
    public void getUserWithRolesByUserName() {
        User user = userManager.findUserWithRolesByUserName("admin").block();

        log.info("Find user by username 'admin': {}", user.toString());
        assertThat(user).isNotNull();
    }

    @Test
    public void registerUserSuccesfull() {

        User user = userManager.findUserWithRolesByUserName("test123").block();

        if (!Objects.isNull(user)) // If user exist
            userManager.deleteUser(user.getUuid()).block();

        String jsonString = "{\"k1\":\"v1\",\"k2\":\"v2\"}";
        ObjectMapper mapper = new ObjectMapper();
        JsonNode metadata = null;
        try {
            metadata = mapper.readTree(jsonString);
        } catch (Exception e) {
            e.printStackTrace();
        }

        UserDTO dto = UserDTO.builder()
                .userName("test123")
                .company("Viajero")
                .email("test123@gmail.com")
                .phone("12345678")
                .password("Mypassword123*")
                .dni("kkk12344")
                .langKey("es")
                .firstName("Frank")
                .lastName("Nicolau Gonzalez")
                .metadata(metadata)
                .build();

        StepVerifier
                .create(userManager.registerUser(dto))
                .expectSubscription()
                .expectNextMatches(res -> res.getUuid() != null)
                .expectComplete()
                .verify();

    }

    @Test
    public void registerUserFaildByUserNameExistAndUserIsActivated() {

        User user = userManager.findUserWithRolesByUserName("test123").block();

        if (!Objects.isNull(user) && user.getActivated() == false) { // If User exist try register user with the same
                                                                     // userName
            userManager.activateUserByUserName("test123").block(); // activate user

            String jsonString = "{\"k1\":\"v1\",\"k2\":\"v2\"}";
            ObjectMapper mapper = new ObjectMapper();
            JsonNode metadata = null;
            try {
              metadata = mapper.readTree(jsonString);
            } catch (Exception e) {
              e.printStackTrace();
            }
            UserDTO dto = UserDTO.builder()
                    .userName("test123")
                    .company("Viajero")
                    .email("test123@gmail.com")
                    .phone("12345678")
                    .password("Mypassword123*")
                    .dni("kkk12344")
                    .langKey("es")
                    .firstName("Frank")
                    .lastName("Nicolau Gonzalez")
                    .metadata(metadata)
                    .build();

            StepVerifier
                    .create(userManager.registerUser(dto))
                    .expectSubscription()
                    .expectErrorMatches(throwable -> throwable instanceof UsernameAlreadyUsedException &&
                            throwable.getMessage().contains("is already in use"))
                    .verify();
        }

    }

    @Test
    public void registerUserFaildByEmailExistAndUserIsActivated() {
        User user = userManager.findUserWithRolesByUserName("test123").block();

        if (!Objects.isNull(user) && user.getActivated() == false) { // If User exist try register user with the same
                                                                     // email and diferent email
            userManager.activateUserByUserName("test123").block(); // activate user
            String jsonString = "{}";
            ObjectMapper mapper = new ObjectMapper();
            JsonNode metadata = null;
            try {
                metadata = mapper.readTree(jsonString);
            } catch (Exception e) {
                e.printStackTrace();
            }
            UserDTO dto = UserDTO.builder()
                    .userName("TestEEEEEE")
                    .company("Viajero")
                    .email("test123@gmail.com")
                    .phone("12345678")
                    .password("Mypassword123*")
                    .dni("kkk12344")
                    .langKey("es")
                    .firstName("Frank")
                    .lastName("Nicolau Gonzalez")
                    .metadata(metadata)
                    .build();

            StepVerifier
                    .create(userManager.registerUser(dto))
                    .expectSubscription()
                    .expectErrorMatches(throwable -> throwable instanceof EmailAlreadyUsedException &&
                            throwable.getMessage().contains("is already in use"))
                    .verify();
        }

    }

    @Test
    public void createUserSuccesfull() {

        UserDTO user = userManager.findUserByUserName("test123").block();

        if (!Objects.isNull(user)) // If user exist
        {
            System.out.print("Eliminando el usuario");
            userManager.deleteUser(user.getUuid()).block();

        }

        List<RoleDTO> roles = new ArrayList<>();
        roles.add(RoleDTO.builder().name("user").build());
        roles.add(RoleDTO.builder().name("admin").build());

        UserDTO dto = UserDTO.builder()
                .userName("test123")
                .company("Viajero")
                .email("test123@gmail.com")
                .phone("12345678")
                .password("Mypassword123*")
                .dni("kkk12344")
                .langKey("es")
                .firstName("Dylan")
                .roles(roles)
                .lastName("Nicolau Moya")
                .metadata(null)
                .build();

        StepVerifier
                .create(userManager.createUser(dto, "admin"))
                .expectSubscription()
                .expectNextMatches(res -> res.getUuid() != null)
                .expectComplete()
                .verify();
    }

    @Test
    public void updateUserSuccesfull() {

        UserDTO user = userManager.findUserByUserName("test123").block();

        if (Objects.isNull(user)) // If user not exist create user
        {
            List<RoleDTO> roles = new ArrayList<>();
            roles.add(RoleDTO.builder().name("user").build());
            roles.add(RoleDTO.builder().name("admin").build());

            user = UserDTO.builder()
                    .userName("test123")
                    .company("Viajero")
                    .email("test123@gmail.com")
                    .phone("12345678")
                    .password("Mypassword123*")
                    .dni("kkk12344")
                    .langKey("es")
                    .firstName("Dylan")
                    .roles(roles)
                    .lastName("Nicolau Moya")
                    .metadata(null)
                    .build();
            // Create user and store in user variable
            user = userManager.createUser(user, "admin").block();
        }

        List<RoleDTO> roles = new ArrayList<>();
        roles.add(RoleDTO.builder().name("user").build());

         String jsonString = "{\"a\": \"aaaaa\", \"c\": \"ccccc\", \"e\": {\"list\": [{\"a\": \"aaaaa\", \"c\": \"ccccc\"}, {\"a\": \"aaaaa\", \"c\": \"ccccc\"}]}, \"list\": [1, 23.4, 54]}";
            ObjectMapper mapper = new ObjectMapper();
            JsonNode metadata = null;
            try {
                metadata = mapper.readTree(jsonString);
            } catch (Exception e) {
                e.printStackTrace();
            }
        // update user
        user.setUserName("test123");
        user.setCompany("ViajeroEC");
        user.setEmail("test123@gmail.com");
        user.setPhone("3333333333");
        user.setPassword("Mypasswordwww123*");
        user.setDni("kikikiki");
        user.setLangKey("es");
        user.setFirstName("Enrique");
        user.setLastName("Nicolau Gonzalez");
        user.setRoles(roles);
        user.setMetadata(metadata);

        user = userManager.updateUser(user, "admin").block();

        log.info(user.toString());
        assertThat(user).isNotNull();
    }

    @Test
    public void getAllUsers() {
        int page = 1;
        int size = 3;
        Page<UserDTO> users = userManager.getAllUsers(PageRequest.of(page, size)).block();

        log.info(users.getContent().toString());

        assertThat(users.getTotalElements()).isGreaterThan(0);
    }

   

}