package ec.viajero.IdentityServer.services;

import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import ec.viajero.IdentityServer.exeptions.LoginException;
import ec.viajero.IdentityServer.exeptions.CustomError;
import ec.viajero.IdentityServer.exeptions.ErrorCodes;
import ec.viajero.IdentityServer.exeptions.ErrorDetails;
import ec.viajero.IdentityServer.exeptions.UserNotActivatedException;
import ec.viajero.IdentityServer.exeptions.UserWasBlockedException;
import ec.viajero.IdentityServer.model.User;
import ec.viajero.IdentityServer.model.security.CustomUserDetail;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

/**
 * User authentication service.
 */

@Slf4j
@Component
@AllArgsConstructor
public class DomainUserDetailsService implements ReactiveUserDetailsService {

    private final UserManagerService userManager;
    
    @Override
    @Transactional
    public Mono<UserDetails> findByUsername(String username) {
      log.info("Authenticating {}", username);

        String lowercaseUserName = username.toLowerCase(Locale.ENGLISH);
        return userManager
                .findUserWithRolesByUserName(lowercaseUserName)
                .switchIfEmpty(Mono.error(new LoginException("User not found",
                                 CustomError.builder().traceId(UUID.randomUUID().toString())
                                                      .status(HttpStatus.UNAUTHORIZED)
                                                      .timestamp(new Date().getTime())
                                                      .errors(List.of(new ErrorDetails(ErrorCodes.USER_NOT_FOUND_EC,"User not found","https//error/details#123")))
                                                      .build())))
                .map(this::createSpringSecurityUser);

    }

    private CustomUserDetail createSpringSecurityUser(User user) {

         if (!user.getActivated() ) { // if user is not activated 
            throw new UserNotActivatedException("User "+user.getUserName()+" was not activated",
                                 CustomError.builder().traceId(UUID.randomUUID().toString())
                                                      .status(HttpStatus.UNAUTHORIZED)
                                                      .timestamp(new Date().getTime())
                                                      .errors(List.of(new ErrorDetails(ErrorCodes.USER_NOT_ACTIVATED_EC,"User was not activated","https//error/details#123")))
                                                      .build());
        }

        if(user.getBlock()) {
           throw new UserWasBlockedException("User "+user.getUserName()+" is blocked",
                                 CustomError.builder().traceId(UUID.randomUUID().toString())
                                                      .status(HttpStatus.UNAUTHORIZED)
                                                      .timestamp(new Date().getTime())
                                                      .errors(List.of(new ErrorDetails(ErrorCodes.USER_WAS_BLOQUED_EC,"User is blocked","https//error/details#123")))
                                                      .build());
        }
          
        
        List<GrantedAuthority> grantedAuthorities = user
                .getRoles()
                .stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toList());

        return new CustomUserDetail(user.getUserName(), user.getPassword(), user.getFirstName() ,
           user.getLastName(), user.getDni(), user.getEmail(), user.getPhone(), user.getLangKey(), grantedAuthorities);
    }

}