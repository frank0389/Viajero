package ec.viajero.IdentityServer.exeptions;

public class UserNotActivatedException extends CustomErrorException {

     public UserNotActivatedException(String message, CustomError error){
        super(message,error);
    }
    
}