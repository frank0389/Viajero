package ec.viajero.IdentityServer.exeptions;

public class LoginException extends CustomErrorException {

    public LoginException(String message, CustomError error){
       super(message,error);
   }
    
}