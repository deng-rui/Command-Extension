package extension.util.log;

public class VariableException extends Exception {
    public VariableException(String type) {
        super(extension.util.log.ErrorCode.valueOf(type).getError());
    }
}