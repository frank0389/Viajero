package ec.viajero.IdentityServer.exeptions;

public class UserNotFoundByUserNameException extends CustomErrorException {

    public UserNotFoundByUserNameException(String message, CustomError error){
       super(message,error);
   }
    
}