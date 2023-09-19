package ec.viajero.IdentityServer.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;

import ec.viajero.IdentityServer.IntegrationTest;
import ec.viajero.IdentityServer.model.Role;
import ec.viajero.IdentityServer.model.User;
import io.r2dbc.postgresql.codec.Json;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@IntegrationTest
public class UserRespositoryIntTest {

   @Autowired
   UserRepository userRepository;
   @Autowired
   RoleRepository roleRepository;

   @BeforeEach
   public void init() {

      User user = userRepository.findOneByUserName("test").block();

      if (Objects.isNull(user)) {

         String str = "{\"a\":\"aaaaa\",\"c\":\"ccccc\", \"list\":[1, 23.4, 54], \"e\":{\"list\":[{\"a\":\"aaaaa\",\"c\":\"ccccc\"},{\"a\":\"aaaaa\",\"c\":\"ccccc\"}]}}";
         Json metadata = Json.of(str);

         user = User.builder()
               .userName("test")
               .activated(false)
               .company("Viajero")
               .email("test@gmail.com")
               .createdBy("system")
               .createdDate(Instant.now())
               .phone("12345678")
               .password("Mypassword123*")
               .dni("kkk12344")
               .firstName("Frank")
               .lastName("Nicolau Gonzalez")
               .lastModifiedDate(Instant.now())
               .metadata(metadata)
               .uuid(UUID.randomUUID().toString())
               .build();

         User savedUser = userRepository.save(user).block();
         List<Role> roles=  roleRepository.findAll().collectList().block();
         int size = roles.size();

         for(int i=0;i < size; i++){
                 roleRepository.saveUserRole(roles.get(i).getId(), savedUser.getId()).block();
         }

         savedUser.setRoles(roles);
         log.info("Saved user:" + savedUser.toString());

      }
   }

   @Test
   public void getUserByUserName() {
      User user = userRepository.findOneByUserName("test").block();
      List<Role> roles = roleRepository.getRolesByUser(user.getId()).collectList().block();
      user.setRoles(roles);
      log.info(user.toString());
      assertThat(user).isNotNull();
   }

   @Test
   public void getUserByEmail() {
      User user = userRepository.findOneByEmailIgnoreCase("test@gmail.com").block();
      List<Role> roles = roleRepository.getRolesByUser(user.getId()).collectList().block();
      user.setRoles(roles);
      log.info(user.toString());
      assertThat(user).isNotNull();
   }

   @Test
   public void getAllUser(){

      List<User> users = userRepository.findAllBy(PageRequest.of(0, 2)).collectList().block();

      log.info(users.toString());

       assertThat(users.size()).isGreaterThan(0);

   }

}