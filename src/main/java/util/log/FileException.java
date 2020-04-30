package extension.util.log;

public class FileException extends Exception {
    public FileException(String type) {
        super(extension.util.log.ErrorCode.valueOf(type).getError());
    }
}