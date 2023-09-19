package ec.viajero.IdentityServer.exeptions;

public class UsernameAlreadyUsedException extends CustomErrorException{

     public UsernameAlreadyUsedException(String message, CustomError error){
        super(message,error);
    }
}