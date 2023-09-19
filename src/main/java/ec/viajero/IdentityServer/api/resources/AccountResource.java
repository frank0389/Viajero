package ec.viajero.IdentityServer.api.resources;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import ec.viajero.IdentityServer.dto.KeyAndPasswordDTO;
import ec.viajero.IdentityServer.dto.ResetPasswordDTO;
import ec.viajero.IdentityServer.dto.UserDTO;
import ec.viajero.IdentityServer.exeptions.CustomError;
import ec.viajero.IdentityServer.exeptions.ErrorCodes;
import ec.viajero.IdentityServer.exeptions.ErrorDetails;
import ec.viajero.IdentityServer.exeptions.InvalidPasswordException;
import ec.viajero.IdentityServer.exeptions.UserNotFoundByActivationKeyException;
import ec.viajero.IdentityServer.exeptions.UserNotFoundByResetKeyException;
import ec.viajero.IdentityServer.exeptions.UserNotFoundByUserNameException;
import ec.viajero.IdentityServer.services.NotificationService;
import ec.viajero.IdentityServer.services.UserManagerService;
import ec.viajero.IdentityServer.util.SecurityUtils;
import lombok.AllArgsConstructor;
import reactor.core.publisher.Mono;

 import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/api")
public class AccountResource {
    
    private final UserManagerService userManager;
    private final NotificationService mailService;
    private final PasswordEncoder passwordEncoder;


    @PostMapping("/account/register")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Void> registerAccount(@Valid @RequestBody UserDTO dto) {
        log.info("REST request to register an account {}", dto.toString());

        if (!isPasswordValid(dto.getPassword())) {
            throw new InvalidPasswordException("Invalid password",
                                 CustomError.builder().traceId(UUID.randomUUID().toString())
                                                      .status(HttpStatus.BAD_REQUEST)
                                                      .timestamp(new Date().getTime())
                                                      .errors(List.of(new ErrorDetails(ErrorCodes.INVALID_PASSWORD_EC,"Invalid password","https//error/details#123")))
                                                      .build());
        }

        if(dto.getPassword()!= null && !dto.getPassword().isEmpty())
           dto.setPassword(passwordEncoder.encode(dto.getPassword()));

        return userManager.registerUser(dto)
                .doOnSuccess(mailService::sendActivationEmail).then();
    }

    @PutMapping("/account")
    public Mono<UserDTO> updateAccount(@Valid @RequestBody UserDTO userDTO) {
        log.info("REST request to update account {}", userDTO.toString());

        if(userDTO.getPassword()!= null && !userDTO.getPassword().isEmpty())
           userDTO.setPassword(passwordEncoder.encode(userDTO.getPassword()));

        return SecurityUtils.getCurrentUserName()
                .switchIfEmpty(Mono.error(new UserNotFoundByUserNameException(
                   "User not found",
                            CustomError.builder().traceId(UUID.randomUUID().toString())
                             .status(HttpStatus.BAD_REQUEST)
                             .timestamp(new Date().getTime())
                             .errors(List.of(new ErrorDetails(ErrorCodes.USER_NOT_FOUND_EC,"User not found","https//error/details#123")))
                             .build())))
                .flatMap(username-> userManager.updateUser(userDTO,username)); 
                
    }

    @GetMapping("/account")
    public Mono<UserDTO> getAccount() {
        log.info("REST request to get account");

        return SecurityUtils.getCurrentUserName().flatMap(userManager::findUserByUserName).switchIfEmpty(
                Mono.error(new UserNotFoundByUserNameException(
                   "User not found",
                            CustomError.builder().traceId(UUID.randomUUID().toString())
                             .status(HttpStatus.BAD_REQUEST)
                             .timestamp(new Date().getTime())
                             .errors(List.of(new ErrorDetails(ErrorCodes.USER_NOT_FOUND_EC,"User not found","https//error/details#12")))
                             .build())));
    }

    @GetMapping("/account/activate")
    @ResponseStatus(HttpStatus.OK)
    public Mono<Void> activateAccount(@RequestParam(value = "key") String key) {
        log.info("REST request to active user by key {}", key);

        return userManager.activateUserBykey(key).switchIfEmpty(Mono.error(new UserNotFoundByActivationKeyException("User not found by activation key "+key,
        CustomError.builder().traceId(UUID.randomUUID().toString())
                             .status(HttpStatus.BAD_REQUEST)
                             .timestamp(new Date().getTime())
                             .errors(List.of(new ErrorDetails(ErrorCodes.USER_NOT_FOUND_EC,"User not found by activation key","https//error/details#12")))
                             .build())))
       .doOnSuccess(mailService::sendCreationEmail).then();
    }


    @PostMapping(path = "/account/reset-password/init")
    @ResponseStatus(HttpStatus.OK)
    public Mono<Void> requestPasswordReset(@RequestBody ResetPasswordDTO resetPasswordDTO) {
        log.info("REST request to reset password for user {}", resetPasswordDTO.getUsername());

        if (resetPasswordDTO.getUsername().isEmpty() ) {
                throw new UserNotFoundByUserNameException(
                   "User not found",
                            CustomError.builder().traceId(UUID.randomUUID().toString())
                             .status(HttpStatus.BAD_REQUEST)
                             .timestamp(new Date().getTime())
                             .errors(List.of(new ErrorDetails(ErrorCodes.USER_NOT_FOUND_EC,"User not found","https//error/details#12")))
                             .build()); 
        }

        return userManager.requestPasswordReset(resetPasswordDTO.getUsername()).doOnSuccess(user -> {
            if (user !=null) {
                mailService.sendPasswordResetMail(user);
            } else {
                // Pretend the request has been successful to prevent checking userName
                // really exist
                // but log that an invalid attempt has been made
                log.warn("Password reset requested for non existing mail");
            }
        }).then();
    }

    @PostMapping(path = "/account/reset-password/finish")
    public Mono<Void> finishPasswordReset(@RequestBody KeyAndPasswordDTO keyAndPassword) {
        log.info("REST request to reset password for key {}", keyAndPassword.getKey());

        if (isPasswordValid(keyAndPassword.getNewPassword()) == false) {
            throw new InvalidPasswordException("Invalid password",
                            CustomError.builder().traceId(UUID.randomUUID().toString())
                             .status(HttpStatus.BAD_REQUEST)
                             .timestamp(new Date().getTime())
                             .errors(List.of(new ErrorDetails(ErrorCodes.INVALID_PASSWORD_EC,"Invalid password","https//error/details#12")))
                             .build());
        }
        
        String newPassword = passwordEncoder.encode(keyAndPassword.getNewPassword());

        return userManager.completePasswordReset(newPassword, keyAndPassword.getKey())
                .switchIfEmpty(Mono.error(new UserNotFoundByResetKeyException(
                         "User not found by reset key "+keyAndPassword.getKey(),
        CustomError.builder().traceId(UUID.randomUUID().toString())
                             .status(HttpStatus.BAD_REQUEST)
                             .timestamp(new Date().getTime())
                             .errors(List.of(new ErrorDetails(ErrorCodes.USER_NOT_FOUND_EC,"User not found by reset key","https//error/details#12")))
                             .build())))
                .then();
    }



    private static boolean isPasswordValid(String password) {
        String pPattern = "(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#&()–[{}]:;',?/*~$^+=<>]).{8,20}";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(pPattern);
        java.util.regex.Matcher m = p.matcher(password);
        boolean value = m.matches();
        return value;
    }
}