package ec.viajero.IdentityServer.model.converters;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

import ec.viajero.IdentityServer.model.EmailTemplate;
import io.r2dbc.spi.Row;

@ReadingConverter
public class EmailTemplateReadingConverter implements Converter<Row, EmailTemplate>{

    @Override
    public EmailTemplate convert(Row row) {
        return EmailTemplate.builder()
                  .id(row.get("id", Long.class))
                  .type(row.get("type", EmailTemplate.Type.class))
                  .subject(row.get("subject", String.class))
                  .body(row.get("body", String.class))
                  .enSubject(row.get("en_subject", String.class))
                  .enBody(row.get("en_body", String.class))
                  .build();
    }
    
}