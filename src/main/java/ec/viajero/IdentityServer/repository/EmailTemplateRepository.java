package ec.viajero.IdentityServer.repository;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;

import ec.viajero.IdentityServer.model.EmailTemplate;
import reactor.core.publisher.Mono;

@Repository
public interface EmailTemplateRepository extends R2dbcRepository<EmailTemplate,Long> {

    public Mono<EmailTemplate> findOneByType(EmailTemplate.Type type);
}