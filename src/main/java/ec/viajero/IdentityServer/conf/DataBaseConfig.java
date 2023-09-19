package ec.viajero.IdentityServer.conf;

// import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration;
import org.springframework.data.r2dbc.config.EnableR2dbcAuditing;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import ec.viajero.IdentityServer.model.EmailTemplate;
import ec.viajero.IdentityServer.model.converters.EmailTemplateReadingConverter;
import ec.viajero.IdentityServer.model.converters.EmailTemplateWritingConverter;


import io.r2dbc.pool.ConnectionPool;
import io.r2dbc.pool.ConnectionPoolConfiguration;
import io.r2dbc.pool.SimplePoolMetricsRecorder;
import io.r2dbc.postgresql.PostgresqlConnectionConfiguration;
import io.r2dbc.postgresql.PostgresqlConnectionFactory;
import io.r2dbc.postgresql.codec.EnumCodec;
import io.r2dbc.spi.ConnectionFactory;


import java.util.List;

/**
 * Plase if you have problem with reactive connection pooling performance  see the next link 
 * see this https://github.com/PiotrDuz/pidu-r2dbc-pool-performance-test/tree/master
 * import io.r2dbc.postgresql.PostgresqlConnectionFactoryProvider;
 * import reactor.netty.resources.LoopResources
 **/

@Configuration
@EnableTransactionManagement
@EnableR2dbcRepositories(basePackages = "ec.viajero.IdentityServer.repository")
@EnableR2dbcAuditing
@ConfigurationProperties(prefix = "postgresql")
public class DataBaseConfig extends AbstractR2dbcConfiguration {

     private static String host= "localhost";
     private static int port= 5433;
     private static String db= "SECURITY";
     private static String username= "postgres";
     private static String password= "postgres";

    @Override
    @Bean
    public ConnectionFactory connectionFactory() {

    PostgresqlConnectionFactory postgresConnectionFactory = new PostgresqlConnectionFactory(
            PostgresqlConnectionConfiguration.builder()
                .host(host)
                .port(port)
                .database(db)
                .username(username)
                .password(password)
                .codecRegistrar(EnumCodec.builder().withEnum("email_type", EmailTemplate.Type.class).build())
                .build()
        );
       
        ConnectionPoolConfiguration poolConfiguration = ConnectionPoolConfiguration.builder(postgresConnectionFactory)
        .initialSize(20) 
        .maxSize(50)
        .metricsRecorder(new SimplePoolMetricsRecorder())
        .build();
      
        return new ConnectionPool(poolConfiguration);
    }

    
    @Override
    protected List<Object> getCustomConverters() {
        return List.of(
                new EmailTemplateReadingConverter(),
                new EmailTemplateWritingConverter()
        );
    }

   
   
    
}