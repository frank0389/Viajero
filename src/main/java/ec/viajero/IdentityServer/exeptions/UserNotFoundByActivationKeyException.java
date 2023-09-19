package ec.viajero.IdentityServer.exeptions;

public class UserNotFoundByActivationKeyException extends CustomErrorException{

     public UserNotFoundByActivationKeyException(String message, CustomError error){
        super(message,error);
    }
}