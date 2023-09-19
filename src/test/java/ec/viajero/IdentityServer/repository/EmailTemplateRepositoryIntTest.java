package ec.viajero.IdentityServer.repository;

import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import ec.viajero.IdentityServer.IntegrationTest;
import ec.viajero.IdentityServer.model.EmailTemplate;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@IntegrationTest
public class EmailTemplateRepositoryIntTest {
 
    @Autowired
    private EmailTemplateRepository emailTemplateRepository;

    @Test
    public void listEmailTemplates() {
      List<EmailTemplate> templates = emailTemplateRepository.findAll().collectList().block();
      log.info(templates.toString());
      assertThat(templates.size()).isGreaterThan(0);
    }

    @Test
    public void getEmailTemplateByType(){
        EmailTemplate template = emailTemplateRepository.findOneByType(EmailTemplate.Type.AccountActivation).block();
        log.info(template.toString());
        assertThat(template).isNotNull();
     }

    

}