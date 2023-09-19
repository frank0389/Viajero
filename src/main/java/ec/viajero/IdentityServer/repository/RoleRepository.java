package ec.viajero.IdentityServer.repository;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;

import ec.viajero.IdentityServer.model.Role;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@Repository
public interface RoleRepository extends R2dbcRepository<Role,Long> {
    public Mono<Role> findRoleByName(String name);
    @Query("SELECT r.id, r.name FROM um_role r INNER JOIN um_user_role ur ON r.id = ur.role_id WHERE ur.user_id = :userId")
    public Flux<Role> getRolesByUser(Long userId);
    @Query("INSERT INTO um_user_role(role_id, user_id) VALUES(:roleId, :userId)")
    public Mono<Void> saveUserRole(Long roleId, Long userId);

    @Query("DELETE FROM um_user_role WHERE user_id = :userId")
    public Mono<Void> deleteUserRoles(Long userId);
}