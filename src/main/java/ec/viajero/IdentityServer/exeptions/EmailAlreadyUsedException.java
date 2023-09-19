package ec.viajero.IdentityServer.exeptions;

public class EmailAlreadyUsedException extends CustomErrorException {

     public EmailAlreadyUsedException(String message, CustomError error){
        super(message,error);
    }
    
}