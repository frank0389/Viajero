package ec.viajero.IdentityServer.exeptions;

public class EmailTemplateNotFoundByIdException  extends CustomErrorException{

     public EmailTemplateNotFoundByIdException(String message, CustomError error){
        super(message,error);
    } 
}