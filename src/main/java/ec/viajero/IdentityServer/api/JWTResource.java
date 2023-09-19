package ec.viajero.IdentityServer.api;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ec.viajero.IdentityServer.dto.JWTTokenDTO;
import ec.viajero.IdentityServer.dto.LoginDTO;
import ec.viajero.IdentityServer.dto.ValidateJWTTokenDTO;
import ec.viajero.IdentityServer.model.security.CustomUserDetail;
import ec.viajero.IdentityServer.services.NotificationService;
import ec.viajero.IdentityServer.util.JwtFilter;
import ec.viajero.IdentityServer.util.TokenProvider;
import lombok.AllArgsConstructor;
import reactor.core.publisher.Mono;

@RestController
@AllArgsConstructor
@RequestMapping("/api")
public class JWTResource {
    private final TokenProvider tokenProvider;
    private final ReactiveAuthenticationManager authenticationManager;
    private final NotificationService mailService;

   
    @PostMapping("/authenticate")
    public Mono<ResponseEntity<JWTTokenDTO>> authenticate(@Valid @RequestBody Mono<LoginDTO> loginVM) {
        return loginVM
                .flatMap(
                        login -> authenticationManager
                                .authenticate(new UsernamePasswordAuthenticationToken(login.getUsername(),
                                        login.getPassword()))
                                 .doOnSuccess(auth -> { 
                                        CustomUserDetail principal = (CustomUserDetail)auth.getPrincipal();
                                        String fullName = principal.getFirstName()+" "+principal.getLastName();
                                        mailService.sendLoginMail(fullName, auth.getName(), principal.getEmail(),principal.getLangKey());
                                    })
                                .flatMap(auth -> Mono
                                        .fromCallable(() -> tokenProvider.createToken(auth))))
                                       
                .map(
                        jwt -> {
                            HttpHeaders httpHeaders = new HttpHeaders();
                            httpHeaders.add(JwtFilter.AUTHORIZATION_HEADER, "Bearer " + jwt);
                            return new ResponseEntity<>(new JWTTokenDTO(jwt), httpHeaders, HttpStatus.OK);
                        });
    }

   
    @PostMapping("/validate/token")
    public Mono<ResponseEntity<ValidateJWTTokenDTO>> validate(@Valid @RequestBody JWTTokenDTO tokenDTO) {
     return Mono.fromCallable(()-> tokenProvider.isValidToken(tokenDTO.getIdToken())).map(r->{
                            return new ResponseEntity<>(ValidateJWTTokenDTO.builder()
                                                        .valid(r.equals("Token is Valid"))
                                                        .message(r).build(),
                                                         new HttpHeaders(), HttpStatus.OK);
     });
    }
}