package ec.viajero.IdentityServer.api.resources;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import ec.viajero.IdentityServer.dto.ActiveUserDTO;
import ec.viajero.IdentityServer.dto.BlockUserDTO;
import ec.viajero.IdentityServer.dto.TotalDTO;
import ec.viajero.IdentityServer.dto.UserDTO;
import ec.viajero.IdentityServer.exeptions.CustomError;
import ec.viajero.IdentityServer.exeptions.ErrorCodes;
import ec.viajero.IdentityServer.exeptions.ErrorDetails;
import ec.viajero.IdentityServer.exeptions.UserNotFoundByUserNameException;
import ec.viajero.IdentityServer.services.UserManagerService;
import ec.viajero.IdentityServer.util.Constants;
import ec.viajero.IdentityServer.util.SecurityUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/api/admin")
public class UserManagerResource {
    private final UserManagerService userManager;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/users")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority(\"" + Constants.ADMIN_ROLE + "\")")
    public Mono<UserDTO> createUser(@Valid @RequestBody UserDTO userDTO) {
        log.info("REST request to create new User : {}", userDTO);
        
        if(userDTO.getPassword()!= null && !userDTO.getPassword().isEmpty())
           userDTO.setPassword(passwordEncoder.encode(userDTO.getPassword()));

        return SecurityUtils.getCurrentUserName()
              .flatMap(username -> userManager.createUser(userDTO, username));
    }

    @PutMapping("/users")
    @PreAuthorize("hasAuthority(\"" + Constants.ADMIN_ROLE + "\")")
    public Mono<UserDTO> updateUser(@Valid @RequestBody UserDTO userDTO) {
        log.info("REST request to update User : {}", userDTO);
        
        if(userDTO.getPassword()!= null && !userDTO.getPassword().isEmpty())
           userDTO.setPassword(passwordEncoder.encode(userDTO.getPassword()));

        return SecurityUtils.getCurrentUserName()
                .flatMap(username-> userManager.updateUser(userDTO,username)); 
    }

    @GetMapping("/users/{uuid}")
    @PreAuthorize("hasAuthority(\"" + Constants.ADMIN_ROLE + "\")")
    public Mono<UserDTO> getUser(@PathVariable String uuid) {
        log.info("REST request to get User by uuid  {}", uuid);
        return userManager
                .findUserByUUID(uuid)
                .switchIfEmpty(Mono.error(new UserNotFoundByUserNameException(
                   "User not found",
                            CustomError.builder().traceId(UUID.randomUUID().toString())
                             .status(HttpStatus.BAD_REQUEST)
                             .timestamp(new Date().getTime())
                             .errors(List.of(new ErrorDetails(ErrorCodes.USER_NOT_FOUND_EC,"User not found","https//error/details#123")))
                             .build())));
    }


    @DeleteMapping("/users/{uuid}")
    @PreAuthorize("hasAuthority(\"" + Constants.ADMIN_ROLE + "\")")
    @ResponseStatus(code = HttpStatus.OK)
    public Mono<Void> deleteUser(
            @PathVariable  String uuid) {
        log.info("REST request to delete User by uuid: {}", uuid);
        return userManager
                .deleteUser(uuid);
    }

    @GetMapping("/users")
    @PreAuthorize("hasAuthority(\"" + Constants.ADMIN_ROLE + "\")")
    public Mono<Page<UserDTO>> getAllUsers( @RequestParam(defaultValue = "0") int page,
                                                            @RequestParam(defaultValue = "10") int size) {
            log.info("Rest request to get all users by page {} and size", page, size);
            return userManager.getAllUsers(PageRequest.of(page, size)); 
    }

   @GetMapping("/users/count")
   @PreAuthorize("hasAuthority(\"" + Constants.ADMIN_ROLE + "\")")
   public Mono<TotalDTO> countUsers(){
        log.info("Rest request to count all users");
        return userManager.countUsers().map(t-> TotalDTO.builder().total(t).build());
   }

   @PostMapping("/users/block")
   @PreAuthorize("hasAuthority(\"" + Constants.ADMIN_ROLE + "\")")
   public Mono<Void> blockUser(@Valid @RequestBody BlockUserDTO dto){
        log.info("Rest request to block users by uuid {}", dto.getUuid());
        return userManager.blockUserByUUID(dto.getUuid(),dto.getBlock());
   }

   @PostMapping("/users/activate")
   @PreAuthorize("hasAuthority(\"" + Constants.ADMIN_ROLE + "\")")
   public Mono<Void> activeUser(@Valid @RequestBody ActiveUserDTO dto){
        log.info("Rest request to activate users by uuid {}", dto.getUuid());
        return userManager.activateUserByUUID(dto.getUuid());
   }

}