package ec.viajero.IdentityServer.services;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import ec.viajero.IdentityServer.dto.RoleDTO;
import ec.viajero.IdentityServer.dto.UserDTO;
import ec.viajero.IdentityServer.exeptions.BadRequestAlertException;
import ec.viajero.IdentityServer.exeptions.CustomError;
import ec.viajero.IdentityServer.exeptions.EmailAlreadyUsedException;
import ec.viajero.IdentityServer.exeptions.ErrorCodes;
import ec.viajero.IdentityServer.exeptions.ErrorDetails;
import ec.viajero.IdentityServer.exeptions.UsernameAlreadyUsedException;
import ec.viajero.IdentityServer.model.Role;
import ec.viajero.IdentityServer.model.User;
import ec.viajero.IdentityServer.repository.RoleRepository;
import ec.viajero.IdentityServer.repository.UserRepository;
import ec.viajero.IdentityServer.util.Constants;
import ec.viajero.IdentityServer.util.RandomUtil;
import io.r2dbc.postgresql.codec.Json;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Slf4j
@Service
@AllArgsConstructor
public class UserManagerService {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;


    public Mono<User> findUserWithRolesByUserName(String userName) {
        log.debug("Find user by username: {}", userName);

        return userRepository.findOneByUserName(userName).flatMap(user -> {
            return roleRepository.getRolesByUser(user.getId()).collectList().map(roles -> {
                user.setRoles(roles);
                return user;
            });
        });
    }

    @Transactional
    private Mono<User> saveUser(User user) {
        log.debug("Saving user:  {}", user.toString());

        if (Objects.isNull(user.getRoles()))
            user.setRoles(new ArrayList<Role>());

        return userRepository.save(user)
                .flatMap(savedUser -> Flux.fromIterable(user.getRoles())
                        .flatMap(role -> roleRepository.saveUserRole(role.getId(), savedUser.getId()))
                        .then(Mono.just(savedUser)))
                .map(u -> {
                    u.setRoles(user.getRoles());
                    return u;
                });
    }

    public Mono<UserDTO> findUserByUserName(String userName) {
        log.debug("Find user by username: {}", userName);

        return userRepository.findOneByUserName(userName).flatMap(user -> {
            return roleRepository.getRolesByUser(user.getId()).collectList().map(roles -> {
                user.setRoles(roles);
                return user;
            });
        }).map(u -> {
            List<RoleDTO> roles = new ArrayList<>();
            int size = u.getRoles().size();
            for (int i = 0; i < size; i++)
                roles.add(RoleDTO.builder()
                        .name(u.getRoles().get(i).getName())
                        .build());

            String jsonString = u.getMetadata().asString();
            ObjectMapper mapper = new ObjectMapper();
            JsonNode metadata = null;
             try {
                metadata = mapper.readTree(jsonString);
            } catch (Exception e) {
                 e.printStackTrace();
            } 

            return UserDTO.builder()
                    .uuid(u.getUuid())
                    .userName(u.getUserName())
                    .company(u.getCompany())
                    .dni(u.getDni())
                    .email(u.getEmail())
                    .roles(roles)
                    .firstName(u.getFirstName())
                    .lastName(u.getLastName())
                    .langKey(u.getLangKey())
                    .metadata(metadata)
                    .phone(u.getPhone())
                    .activated(u.getActivated())
                    .block(u.getBlock())
                    .build();
        });
    }

    @Transactional
    public Mono<UserDTO> findUserByUUID(String uuid) {
        log.debug("Find user by uuid: {}", uuid);

        return userRepository.findOneByUuid(uuid).flatMap(user -> {
            return roleRepository.getRolesByUser(user.getId()).collectList().map(roles -> {
                user.setRoles(roles);
                return user;
            });
        }).map(u -> {
            List<RoleDTO> roles = new ArrayList<>();
            int size = u.getRoles().size();
            for (int i = 0; i < size; i++)
                roles.add(RoleDTO.builder()
                        .name(u.getRoles().get(i).getName())
                        .build());

            String jsonString = u.getMetadata().asString();
            ObjectMapper mapper = new ObjectMapper();
            JsonNode metadata = null;
             try {
                metadata = mapper.readTree(jsonString);
            } catch (Exception e) {
                 e.printStackTrace();
            } 

            return UserDTO.builder()
                    .uuid(u.getUuid())
                    .userName(u.getUserName())
                    .company(u.getCompany())
                    .dni(u.getDni())
                    .email(u.getEmail())
                    .roles(roles)
                    .firstName(u.getFirstName())
                    .lastName(u.getLastName())
                    .langKey(u.getLangKey())
                    .metadata(metadata)
                    .phone(u.getPhone())
                    .activated(u.getActivated())
                    .block(u.getBlock())
                    .build();
        });
    }

    @Transactional
    public Mono<UserDTO> registerUser(UserDTO userDTO) {

        log.info("Register user:  {}", userDTO.toString());

        if (userDTO.getUuid() != null) {
            throw new BadRequestAlertException("Invalid request",
                                 CustomError.builder().traceId(UUID.randomUUID().toString())
                                                      .status(HttpStatus.BAD_REQUEST)
                                                      .timestamp(new Date().getTime())
                                                      .errors(List.of(new ErrorDetails(ErrorCodes.BAD_REQUEST_EC,"Email is already in use","https//error/details#123")))
                                                      .build());
        }

        return userRepository.findOneByUserName(userDTO.getUserName().toLowerCase()).flatMap(existingUser -> {
            if (!existingUser.getActivated()) {
                return userRepository.delete(existingUser);
            } else {
                return Mono.error(
                        new UsernameAlreadyUsedException("UserName  " + userDTO.getUserName() + " is already in use",
                                 CustomError.builder().traceId(UUID.randomUUID().toString())
                                                      .status(HttpStatus.BAD_REQUEST)
                                                      .timestamp(new Date().getTime())
                                                      .errors(List.of(new ErrorDetails(ErrorCodes.USERNAME_EXIST_EC,"Email is already in use","https//error/details#123")))
                                                      .build()));
            }
        }).then(userRepository.findOneByEmailIgnoreCase(userDTO.getEmail())).flatMap(existingUser -> {
            if (!existingUser.getActivated()) {
                return userRepository.delete(existingUser);
            } else {
                return Mono.error(new EmailAlreadyUsedException("Email " + userDTO.getEmail() + " is already in use",
                                 CustomError.builder().traceId(UUID.randomUUID().toString())
                                                      .status(HttpStatus.BAD_REQUEST)
                                                      .timestamp(new Date().getTime())
                                                      .errors(List.of(new ErrorDetails(ErrorCodes.EMAIL_EXIST_EC,"Email is already in use","https//error/details#123")))
                                                      .build()));
            }
        }).then(Mono.fromCallable(() -> {

            ObjectMapper objectMapper = new ObjectMapper();
            Json metadata = Json.of("{}");
            
            if(userDTO.getMetadata() != null){
                try{
                 metadata = Json.of(objectMapper.writeValueAsString(userDTO.getMetadata()));
                } catch(Exception e){
                    e.printStackTrace();
                }
            }

            return User.builder()
                    .uuid(RandomUtil.generateUUID())
                    .userName(userDTO.getUserName().toLowerCase())
                    .password(userDTO.getPassword())
                    .firstName(userDTO.getFirstName())
                    .lastName(userDTO.getLastName())
                    .email(userDTO.getEmail())
                    .langKey(userDTO.getLangKey())
                    .company(userDTO.getCompany())
                    .dni(userDTO.getDni())
                    .phone(userDTO.getPhone())
                    .activated(false)
                    .activationKey(RandomUtil.generateActivationKey())
                    .createdDate(Instant.now())
                    .createdBy(Constants.SYSTEM_USERNAME)
                    .roles(new ArrayList<Role>())
                    .metadata(metadata)
                    .build();
        })).flatMap(newUser -> {
            List<Role> roles = new ArrayList<>();
            return roleRepository.findRoleByName(Constants.USER_ROLE).map(roles::add).thenReturn(newUser)
                    .doOnNext(user -> {
                        roles.stream().forEach(role -> user.getRoles().add(role));
                    }).flatMap(this::saveUser);
        })
                .map(savedUser -> {
                    userDTO.setUuid(savedUser.getUuid());
                    List<RoleDTO> roles = new ArrayList<>();
                    int size = savedUser.getRoles().size();
                    for (int i = 0; i < size; i++)
                        roles.add(RoleDTO.builder()
                                .name(savedUser.getRoles().get(i).getName())
                                .build());
                    String jsonString = savedUser.getMetadata().asString();
                    ObjectMapper mapper = new ObjectMapper();
                    JsonNode metadata = null;

                    try {
                         metadata = mapper.readTree(jsonString);
                    } catch (Exception e) {
                         e.printStackTrace();
                     } 
                    return UserDTO.builder()
                            .uuid(savedUser.getUuid())
                            .userName(savedUser.getUserName())
                            .firstName(savedUser.getFirstName())
                            .lastName(savedUser.getLastName())
                            .email(savedUser.getEmail())
                            .langKey(savedUser.getLangKey())
                            .company(savedUser.getCompany())
                            .dni(savedUser.getDni())
                            .phone(savedUser.getPhone())
                            .activated(false)
                            .activationKey(savedUser.getActivationKey())
                            .block(false)
                            .roles(roles)
                            .metadata(metadata)
                            .build();
                });
    }

    @Transactional
    public Mono<UserDTO> createUser(UserDTO userDTO, String currentUser) {
        log.info("Create user {} by {}", userDTO.toString(), currentUser);

        return userRepository.findOneByUserName(userDTO.getUserName().toLowerCase()).flatMap(existingUser -> {
            if (!existingUser.getActivated()) {
                return userRepository.delete(existingUser);
            } else {
                return Mono.error(
                        new UsernameAlreadyUsedException("UserName  " + userDTO.getUserName() + " is already in use",
                                 CustomError.builder().traceId(UUID.randomUUID().toString())
                                                      .status(HttpStatus.BAD_REQUEST)
                                                      .timestamp(new Date().getTime())
                                                      .errors(List.of(new ErrorDetails(ErrorCodes.USERNAME_EXIST_EC,"Email is already in use","https//error/details#123")))
                                                      .build()));
            }
        }).then(userRepository.findOneByEmailIgnoreCase(userDTO.getEmail())).flatMap(existingUser -> {
            if (!existingUser.getActivated()) {
                return userRepository.delete(existingUser);
            } else {
                return Mono.error(new EmailAlreadyUsedException("Email " + userDTO.getEmail() + " is already in use",
                                 CustomError.builder().traceId(UUID.randomUUID().toString())
                                                      .status(HttpStatus.BAD_REQUEST)
                                                      .timestamp(new Date().getTime())
                                                      .errors(List.of(new ErrorDetails(ErrorCodes.EMAIL_EXIST_EC,"Email is already in use","https//error/details#123")))
                                                      .build()));
            }

        }).then(Mono.fromCallable(() -> {
            
            ObjectMapper objectMapper = new ObjectMapper();
            Json metadata = Json.of("{}");
            
            if(userDTO.getMetadata() != null){
                try{
                 metadata = Json.of(objectMapper.writeValueAsString(userDTO.getMetadata()));
                } catch(Exception e){
                    e.printStackTrace();
                }
            }

            
            return User.builder()
                    .uuid(RandomUtil.generateUUID())
                    .userName(userDTO.getUserName().toLowerCase())
                    .password(userDTO.getPassword())
                    .firstName(userDTO.getFirstName())
                    .lastName(userDTO.getLastName())
                    .email(userDTO.getEmail())
                    .langKey(userDTO.getLangKey())
                    .company(userDTO.getCompany())
                    .dni(userDTO.getDni())
                    .phone(userDTO.getPhone())
                    .createdBy(currentUser)
                    .activated(true)
                    .createdDate(Instant.now())
                    .roles(new ArrayList<Role>())
                    .metadata(metadata)
                    .build();
        })).flatMap(newUser -> {
            return Flux.fromIterable(userDTO.getRoles())
                    .flatMap(role -> roleRepository.findRoleByName(role.getName()))
                    .doOnNext(role -> newUser.getRoles().add(role))
                    .then(Mono.just(newUser)).flatMap(this::saveUser);
        }).map(savedUser -> {
            userDTO.setUuid(savedUser.getUuid());
            List<RoleDTO> roles = new ArrayList<>();
            int size = savedUser.getRoles().size();
            for (int i = 0; i < size; i++)
                roles.add(RoleDTO.builder()
                        .name(savedUser.getRoles().get(i).getName())
                        .build());
            String jsonString = savedUser.getMetadata().asString();
            ObjectMapper mapper = new ObjectMapper();
            JsonNode metadata = null;
            try {
                metadata = mapper.readTree(jsonString);
            } catch (Exception e) {
                 e.printStackTrace();
            } 

            return UserDTO.builder()
                    .uuid(savedUser.getUuid())
                    .userName(savedUser.getUserName())
                    .firstName(savedUser.getFirstName())
                    .lastName(savedUser.getLastName())
                    .email(savedUser.getEmail())
                    .langKey(savedUser.getLangKey())
                    .company(savedUser.getCompany())
                    .dni(savedUser.getDni())
                    .phone(savedUser.getPhone())
                    .activated(false)
                    .block(false)
                    .roles(roles)
                    .metadata(metadata)
                    .build();
        });

    }

    @Transactional
    public Mono<UserDTO> updateUser(UserDTO userDTO, String currentUser) {

        log.info("Udpdate user {} by {}", userDTO.toString(), currentUser);

        if (userDTO.getUuid() == null) {
            throw new BadRequestAlertException("Invalid request",
                                 CustomError.builder().traceId(UUID.randomUUID().toString())
                                                      .status(HttpStatus.BAD_REQUEST)
                                                      .timestamp(new Date().getTime())
                                                      .errors(List.of(new ErrorDetails(ErrorCodes.BAD_REQUEST_EC,"Invalid Request","https//error/details#123")))
                                                      .build());
        }

        return userRepository.findOneByUuid(userDTO.getUuid()).flatMap(user -> {

              ObjectMapper objectMapper = new ObjectMapper();
            Json metadata = Json.of("{}");
            
            if(userDTO.getMetadata() != null){
                try{
                 metadata = Json.of(objectMapper.writeValueAsString(userDTO.getMetadata()));
                } catch(Exception e){
                    e.printStackTrace();
                }
            }

            if (userDTO.getPassword() != null && !userDTO.getPassword().isEmpty())
                user.setPassword(userDTO.getPassword());
        
            user.setFirstName(userDTO.getFirstName());
            user.setLastName(userDTO.getLastName());
            user.setDni(userDTO.getDni());

            user.setPhone(userDTO.getPhone());
            user.setCompany(userDTO.getCompany());
            user.setMetadata(metadata);
            user.setRoles(new ArrayList<>());
            user.setLastModifiedBy(currentUser);
            user.setLastModifiedDate(Instant.now());
            user.setEmail(userDTO.getEmail());

            return userRepository.findOneByEmailIgnoreCase(userDTO.getEmail()).flatMap(existingUser -> {
                if (!existingUser.getUuid().equals(userDTO.getUuid())) {
                      return Mono.error(new EmailAlreadyUsedException("Email " + userDTO.getEmail() + " is already in use",
                                 CustomError.builder().traceId(UUID.randomUUID().toString())
                                                      .status(HttpStatus.BAD_REQUEST)
                                                      .timestamp(new Date().getTime())
                                                      .errors(List.of(new ErrorDetails(ErrorCodes.EMAIL_EXIST_EC,"Email is already in use","https//error/details#123")))
                                                      .build()));
                } else {
                    return roleRepository.deleteUserRoles(user.getId()).then(Mono.just(user));
                }
            });    
        }).flatMap(newUser -> {
            return Flux.fromIterable(userDTO.getRoles())
                    .flatMap(role -> roleRepository.findRoleByName(role.getName()))
                    .doOnNext(role -> newUser.getRoles().add(role))
                    .then(Mono.just(newUser)).flatMap(this::saveUser);
        }).map(savedUser -> {
            List<RoleDTO> roles = new ArrayList<>();
            int size = savedUser.getRoles().size();
            for (int i = 0; i < size; i++)
                roles.add(RoleDTO.builder()
                        .name(savedUser.getRoles().get(i).getName())
                        .build());
            String jsonString = savedUser.getMetadata().asString();
            ObjectMapper mapper = new ObjectMapper();
            JsonNode metadata = null;
            
            try {
                metadata = mapper.readTree(jsonString);
            } catch (Exception e) {
                 e.printStackTrace();
            } 

            return UserDTO.builder()
                    .uuid(savedUser.getUuid())
                    .userName(savedUser.getUserName())
                    .firstName(savedUser.getFirstName())
                    .lastName(savedUser.getLastName())
                    .email(savedUser.getEmail())
                    .langKey(savedUser.getLangKey())
                    .company(savedUser.getCompany())
                    .dni(savedUser.getDni())
                    .phone(savedUser.getPhone())
                    .activated(false)
                    .block(false)
                    .roles(roles)
                    .metadata(metadata)
                    .build();
        });
    }

    @Transactional
    public Mono<Page<UserDTO>> getAllUsers(PageRequest pageRequest) {
        log.debug("Get all users by {}", pageRequest);

        return userRepository.findAllBy(pageRequest).flatMap(user -> {
            return roleRepository.getRolesByUser(user.getId()).collectList().map(roles -> {
                user.setRoles(roles);
                return user;
            });
        }).map(user -> {

            List<RoleDTO> roles = new ArrayList<>();
            int size = user.getRoles().size();
            for (int i = 0; i < size; i++)
                roles.add(RoleDTO.builder()
                        .name(user.getRoles().get(i).getName())
                        .build());
            String jsonString = user.getMetadata().asString();
            ObjectMapper mapper = new ObjectMapper();
            JsonNode metadata = null;
             try {
                metadata = mapper.readTree(jsonString);
            } catch (Exception e) {
                 e.printStackTrace();
            } 
            return UserDTO.builder()
                    .uuid(user.getUuid())
                    .userName(user.getUserName())
                    .firstName(user.getFirstName())
                    .lastName(user.getLastName())
                    .email(user.getEmail())
                    .langKey(user.getLangKey())
                    .company(user.getCompany())
                    .dni(user.getDni())
                    .phone(user.getPhone())
                    .activated(user.getActivated())
                    .block(user.getBlock())
                    .roles(roles)
                    .metadata(metadata)
                    .build();
        })
                .collectList()
                .zipWith(userRepository.count())
                .map(t -> new PageImpl<>(t.getT1(), pageRequest, t.getT2()));
    } 

    @Transactional
    public Mono<Void> deleteUser(String uuid) {
        log.debug("Delete user by {}", uuid);

        return userRepository.findOneByUuid(uuid)
                .flatMap(user -> roleRepository.deleteUserRoles(user.getId()).thenReturn(user))
                .flatMap(user -> userRepository.delete(user));
    }

    @Transactional(readOnly = true)
    public Mono<Long> countUsers() {
        log.debug("Count all users");
        return userRepository.count();
    }

    @Transactional
    public Mono<Void> blockUserByUUID(String uuid, Boolean block) {
        log.debug("Activating user by uuid {}", uuid);

        return userRepository.findOneByUuid(uuid).flatMap(user -> {
            user.setBlock(block);
            return saveUser(user);
        }).doOnNext(user -> log.debug("Activated user: {}", user)).then();
    }

    @Transactional
    public Mono<UserDTO> activateUserBykey(String key) {
        log.debug("Activating user for activation key {}", key);

        return userRepository.findOneByActivationKey(key).flatMap(user -> {
            user.setActivated(true);
            user.setActivationKey(null);
            return saveUser(user);
        }).doOnNext(user -> log.debug("Activated user: {}", user)).map(u -> {
            return UserDTO.builder()
                    .uuid(u.getUuid())
                    .company(u.getCompany())
                    .userName(u.getUserName())
                    .dni(u.getDni())
                    .email(u.getEmail())
                    .firstName(u.getFirstName())
                    .lastName(u.getLastName())
                    .langKey(u.getLangKey())
                    .activationKey(u.getActivationKey())
                    .phone(u.getPhone())
                    .build();
        });
    }

    @Transactional
    public Mono<Void> activateUserByUUID(String uuid) {
        log.debug("Activating user by uuid {}", uuid);

        return userRepository.findOneByUuid(uuid).flatMap(user -> {
            user.setActivated(true);
            user.setActivationKey(null);
            return saveUser(user);
        }).doOnNext(user -> log.debug("Activated user: {}", user)).then();
    }

    @Transactional
    public Mono<Void> activateUserByUserName(String userName) {
        log.debug("Activating user for userName {}", userName);

        return userRepository.findOneByUserName(userName.toLowerCase()).flatMap(user -> {
            user.setActivated(true);
            user.setActivationKey(null);
            return saveUser(user);
        }).doOnNext(user -> log.debug("Activated user: {}", user)).then();
    }

    @Transactional
    public Mono<UserDTO> requestPasswordReset(String userName) {
        log.debug("Request password reset for user {}.", userName);

        return userRepository.findOneByUserName(userName.toLowerCase()).filter(User::getActivated)
                .publishOn(Schedulers.boundedElastic())
                .map((user) -> {
                    user.setResetKey(RandomUtil.generateResetKey());
                    user.setResetDate(Instant.now());
                    return user;
                }).flatMap(this::saveUser).map(u -> {
                    return UserDTO.builder()
                    .uuid(u.getUuid())
                    .company(u.getCompany())
                    .userName(u.getUserName())
                    .dni(u.getDni())
                    .email(u.getEmail())
                    .firstName(u.getFirstName())
                    .lastName(u.getLastName())
                    .langKey(u.getLangKey())
                    .resetKey(u.getResetKey())
                    .phone(u.getPhone())
                    .build();
                });
    }

    public Mono<UserDTO> completePasswordReset(String encriptedPassword, String key) {
        log.debug("Reset user password for reset key {}", key);

        return userRepository.findOneByResetKey(key)
                .filter(user -> user.getResetDate().isAfter(Instant.now().minus(1, ChronoUnit.DAYS)))
                .publishOn(Schedulers.boundedElastic()).map(user -> {
                    user.setPassword(encriptedPassword);
                    user.setResetKey(null);
                    user.setResetDate(null);
                    return user;
                }).flatMap(this::saveUser).map(u -> {
                    return UserDTO.builder()
                    .uuid(u.getUuid())
                    .company(u.getCompany())
                    .userName(u.getUserName())
                    .dni(u.getDni())
                    .email(u.getEmail())
                    .firstName(u.getFirstName())
                    .lastName(u.getLastName())
                    .langKey(u.getLangKey())
                    .activationKey(u.getActivationKey())
                    .phone(u.getPhone())
                    .build();
                });
    }

    @Transactional
    public Flux<RoleDTO> getRoles() {
        return roleRepository.findAll().map(role -> {
            return RoleDTO.builder().name(role.getName()).build();
        });
    }

}