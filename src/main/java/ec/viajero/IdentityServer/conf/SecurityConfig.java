package ec.viajero.IdentityServer.conf;

import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
// import org.springframework.security.web.server.header.ReferrerPolicyServerHttpHeadersWriter;

import ec.viajero.IdentityServer.exeptions.handlers.CustomAccessDeniedHandler;
import ec.viajero.IdentityServer.exeptions.handlers.CustomAuthenticationFailureHandler;
import ec.viajero.IdentityServer.util.Constants;
import ec.viajero.IdentityServer.util.JwtFilter;
import ec.viajero.IdentityServer.util.TokenProvider;
import lombok.AllArgsConstructor;


/**
 * Plase if you have problem with this configuration please
 * see this https://docs.spring.io/spring-security/reference/reactive/configuration/webflux.html
 **/

@AllArgsConstructor
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
public class SecurityConfig {
    
    private static final String[] ENDPOINTS_WHITELIST = {
        "/api/authenticate",
        "/api/validate/token",
        "/api/account/register",
        "/api/account/activate",
        "/api/account/reset-password/init",
        "/api/account/reset-password/finish"
    };

    private final ReactiveUserDetailsService userDetailsService;
    private final TokenProvider tokenProvider;


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public ReactiveAuthenticationManager reactiveAuthenticationManager() {
        UserDetailsRepositoryReactiveAuthenticationManager authenticationManager = new UserDetailsRepositoryReactiveAuthenticationManager(
                userDetailsService
        );
        authenticationManager.setPasswordEncoder(passwordEncoder());
        return authenticationManager;
    }


    @Bean
    public SecurityWebFilterChain filterChain(ServerHttpSecurity http) {

         http.csrf()
                .disable()
                .addFilterAt(new JwtFilter(tokenProvider), SecurityWebFiltersOrder.HTTP_BASIC)
                .authenticationManager(reactiveAuthenticationManager())
                .exceptionHandling().accessDeniedHandler(new CustomAccessDeniedHandler())
                .authenticationEntryPoint(new CustomAuthenticationFailureHandler())
                .and()
                // .headers()
                // .contentSecurityPolicy("img-src 'self' data: http://localhost:8080/ ; ")
                // .and()
                // .referrerPolicy(ReferrerPolicyServerHttpHeadersWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN)
                // .and()
                // .frameOptions().disable()
                // .and()
                .authorizeExchange()
                .pathMatchers(ENDPOINTS_WHITELIST).permitAll()
                .pathMatchers("/api/admin/**").hasAuthority(Constants.ADMIN_ROLE)
                .pathMatchers("/api/**").authenticated();

            return http.build();
    }

}