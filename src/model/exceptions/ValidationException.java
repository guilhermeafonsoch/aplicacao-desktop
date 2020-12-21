package model.exceptions;

import java.util.HashMap;
import java.util.Map;

public class ValidationException extends RuntimeException {

    private Map<String, String> errors = new HashMap<>();

    public ValidationException(String msg){
        super(msg);
    }

    public Map<String, String> getErrors() {
        return errors;
    }

    public void addError(String fieldName, String errorMessege){
        errors.put(fieldName, errorMessege);
    }

}
