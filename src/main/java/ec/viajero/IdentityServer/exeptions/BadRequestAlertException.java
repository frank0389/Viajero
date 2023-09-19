package ec.viajero.IdentityServer.exeptions;

public class BadRequestAlertException extends CustomErrorException {

     public BadRequestAlertException(String message, CustomError error){
        super(message,error);
    }
}