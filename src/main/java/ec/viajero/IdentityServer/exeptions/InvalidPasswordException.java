package ec.viajero.IdentityServer.exeptions;

public class InvalidPasswordException extends CustomErrorException {

     public InvalidPasswordException(String message, CustomError error){
        super(message,error);
    }
}