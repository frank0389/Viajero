package ec.viajero.IdentityServer.model.converters;

import org.springframework.data.convert.WritingConverter;
import org.springframework.data.r2dbc.convert.EnumWriteSupport;

import ec.viajero.IdentityServer.model.EmailTemplate;

@WritingConverter
public class EmailTemplateWritingConverter extends EnumWriteSupport<EmailTemplate.Type>{
    
}