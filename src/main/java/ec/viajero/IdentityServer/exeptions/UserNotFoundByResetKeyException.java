package ec.viajero.IdentityServer.exeptions;

public class UserNotFoundByResetKeyException extends CustomErrorException{

     public UserNotFoundByResetKeyException(String message, CustomError error){
        super(message,error);
    }
    
}