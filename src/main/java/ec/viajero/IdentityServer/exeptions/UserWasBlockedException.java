package ec.viajero.IdentityServer.exeptions;

public class UserWasBlockedException extends CustomErrorException {

     public UserWasBlockedException(String message, CustomError error){
        super(message,error);
    }
}