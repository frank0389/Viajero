package ec.viajero.IdentityServer.repository;

import java.time.LocalDateTime;

import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;

import ec.viajero.IdentityServer.model.User;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface UserRepository extends R2dbcRepository<User, Long> {
    
    public Mono<User> findOneByActivationKey(String activationKey);

    public Flux<User> findAllByActivatedIsFalseAndActivationKeyIsNotNullAndCreatedDateBefore(LocalDateTime dateTime);

    Flux<User> findAllBy(Pageable pageable);

    public Mono<User> findOneByResetKey(String resetKey);

    public Mono<User> findOneByEmailIgnoreCase(String email);

    public Mono<User> findOneByUserName(String userName);

    public Mono<User> findOneByEmail(String email);

    public Mono<User> findOneByUuid(String uuid);

    public Mono<Long> count();   
}