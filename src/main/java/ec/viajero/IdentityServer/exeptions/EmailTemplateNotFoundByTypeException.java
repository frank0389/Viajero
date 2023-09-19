package ec.viajero.IdentityServer.exeptions;

public class EmailTemplateNotFoundByTypeException extends CustomErrorException{

     public EmailTemplateNotFoundByTypeException(String message, CustomError error){
        super(message,error);
    }
    
}