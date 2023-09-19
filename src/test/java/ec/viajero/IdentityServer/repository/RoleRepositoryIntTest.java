package ec.viajero.IdentityServer.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import ec.viajero.IdentityServer.IntegrationTest;
import ec.viajero.IdentityServer.model.Role;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@IntegrationTest
public class RoleRepositoryIntTest {

    @Autowired
    private RoleRepository roleRepository;

    @Test
    public void createRole() {
       Role exist = roleRepository.findRoleByName("sheduler").block();

        if(exist!= null)
            roleRepository.delete(exist).block();

        Role role = Role.builder().name("sheduler").build();
        Role saved = roleRepository.save(role).block();
        log.info(saved.toString());
        assertThat(saved).isNotNull();
    }

    @Test
    public void getRoleByName() {
        Role role = roleRepository.findRoleByName("admin").block();
        log.info(role.toString());
        assertThat(role).isNotNull();
    }

    @Test
    public void getAllRoles() {
        List<Role> roles = roleRepository.findAll().collectList().block();
        log.info(roles.toString());
        assertThat(roles.size()).isGreaterThan(0);
    }

 
}