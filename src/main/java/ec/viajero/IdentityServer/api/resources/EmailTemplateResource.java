package ec.viajero.IdentityServer.api.resources;

import java.util.Date;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ec.viajero.IdentityServer.dto.EmailTemplateDTO;
import ec.viajero.IdentityServer.exeptions.CustomError;
import ec.viajero.IdentityServer.exeptions.EmailTemplateNotFoundByIdException;
import ec.viajero.IdentityServer.exeptions.EmailTemplateNotFoundByTypeException;
import ec.viajero.IdentityServer.exeptions.ErrorCodes;
import ec.viajero.IdentityServer.exeptions.ErrorDetails;
import ec.viajero.IdentityServer.model.EmailTemplate;
import ec.viajero.IdentityServer.services.EmailTemplateService;
import ec.viajero.IdentityServer.util.Constants;
import ec.viajero.IdentityServer.util.EmailConstants;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;


@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/api/admin")
public class EmailTemplateResource {
   
   private final EmailTemplateService emailTemplateService;

    @GetMapping("/emailTemplates")
    @PreAuthorize("hasAuthority(\"" + Constants.ADMIN_ROLE + "\")")
    public Mono<EmailTemplateDTO> getEmailTemplate(@RequestParam(value = "type") String type) {
        log.info("REST request to get email template by type : {}", type);
        
        if(!EmailConstants.templateTypes.containsValue(type))
                  throw new EmailTemplateNotFoundByTypeException("Email template by type "+type+" was not found",
                  CustomError.builder().traceId(UUID.randomUUID().toString())
                   .status(HttpStatus.BAD_REQUEST)
                   .timestamp(new Date().getTime())
                   .errors(List.of(new ErrorDetails(ErrorCodes.EMAIL_TEMPLATE_NOT_FOUND_EC,"Email template not found","https//error/details#12")))
                   .build());
        
        EmailTemplate.Type emailtype = EmailTemplate.Type.AccountActivation;
         for(Entry<EmailTemplate.Type, String> entry: EmailConstants.templateTypes.entrySet()){
                if(entry.getValue().equals(type))
                    emailtype = entry.getKey();
         }

        return emailTemplateService.
                template(emailtype);
    }


    @GetMapping("/emailTemplates/types")
    @PreAuthorize("hasAuthority(\"" + Constants.ADMIN_ROLE + "\")")
    public Mono<List<String>> templates() {
        log.info("REST request to get all email templates");
        return emailTemplateService.
                templates().collectList();
    }

    @PutMapping("/emailTemplates")
    @PreAuthorize("hasAuthority(\"" + Constants.ADMIN_ROLE + "\")")
    public Mono<EmailTemplateDTO> updateTemplate( @Valid @RequestBody EmailTemplateDTO emailTemplateDTO) {
        log.info("REST request to update emailTemplate : {}", emailTemplateDTO.getId());

        if (emailTemplateDTO.getId() == null) {
            throw new EmailTemplateNotFoundByIdException("Email template by id "+String.valueOf(emailTemplateDTO.getId())+" was not found",
                            CustomError.builder().traceId(UUID.randomUUID().toString())
                             .status(HttpStatus.BAD_REQUEST)
                             .timestamp(new Date().getTime())
                             .errors(List.of(new ErrorDetails(ErrorCodes.EMAIL_TEMPLATE_NOT_FOUND_EC,"Email template not found","https//error/details#12")))
                             .build());
        }

        return emailTemplateService.
                updateTemplate(emailTemplateDTO);
    }



}