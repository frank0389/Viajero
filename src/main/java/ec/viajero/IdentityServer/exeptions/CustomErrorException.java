package ec.viajero.IdentityServer.exeptions;

import lombok.Getter;

public class CustomErrorException extends RuntimeException {
    @Getter
    private CustomError error;

    public CustomErrorException(String message, CustomError error) {
        super(message);
        this.error = error;
    }
}