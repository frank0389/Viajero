package ec.viajero.IdentityServer.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ec.viajero.IdentityServer.dto.EmailTemplateDTO;
import ec.viajero.IdentityServer.model.EmailTemplate;
import ec.viajero.IdentityServer.repository.EmailTemplateRepository;
import ec.viajero.IdentityServer.util.EmailConstants;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@AllArgsConstructor
public class EmailTemplateService {

    private final EmailTemplateRepository emailTemplateRepository;

    @Transactional
    public Mono<EmailTemplateDTO> template(EmailTemplate.Type type) {
        log.info("Get email template by type {}", type);
        return this.emailTemplateRepository.findOneByType(type).map(template -> {
            return EmailTemplateDTO.builder()
                                .subject(template.getSubject())
                                .type(EmailConstants.templateTypes.get(template.getType()))
                                .body(template.getBody())
                                .enSubject(template.getEnSubject())
                                .enBody(template.getEnBody())
                                .id(template.getId())
                                .build();
        });
    }

    @Transactional
    public Mono<EmailTemplateDTO> template(Long id) {
        log.info("Get email template by id: {} ", id);
        return this.emailTemplateRepository.findById(id).map(template -> {
            return EmailTemplateDTO.builder()
                                .subject(template.getSubject())
                                .type(EmailConstants.templateTypes.get(template.getType()))
                                .body(template.getBody())
                                .enSubject(template.getEnSubject())
                                .enBody(template.getEnBody())
                                .id(template.getId())
                                .build();
        });
    }

    @Transactional
    public Flux<String> templates() {
        log.info("Get all email template types");
        return Flux.fromIterable(EmailConstants.templateTypes.values());
    }

    @Transactional
    public Mono<EmailTemplateDTO> updateTemplate(EmailTemplateDTO templateDTO) {
        log.info("Update email template by type {} " , templateDTO.getType());

        return this.emailTemplateRepository.findById(templateDTO.getId())
                .flatMap(t -> {
                    t.setSubject(templateDTO.getSubject());
                    t.setBody(templateDTO.getBody());
                    t.setEnBody(templateDTO.getEnBody());
                    t.setEnSubject(templateDTO.getEnSubject());
                    return this.emailTemplateRepository.save(t);
                }).map(template -> {
                    return EmailTemplateDTO.builder()
                                .subject(template.getSubject())
                                .type(EmailConstants.templateTypes.get(template.getType()))
                                .body(template.getBody())
                                .enSubject(template.getEnSubject())
                                .enBody(template.getEnBody())
                                .id(template.getId())
                                .build();
                })
                .doOnNext(template -> log.debug("Changed Information for email template: {}", template.getType()));
    }

}