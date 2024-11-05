package be.enkidu.vinyles.business.excpetion;

public class RessourceNotFoundException extends RuntimeException {

    public RessourceNotFoundException(String message) {
        super(message);
    }
}
