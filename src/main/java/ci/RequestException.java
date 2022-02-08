package ci;

import jakarta.servlet.ServletException;

public class RequestException extends ServletException {
    public int status;

    public RequestException(int status, Throwable cause) {
        super(cause);
        this.status = status;
    }

    public RequestException(int status, String message) {
        super(message);
        this.status = status;
    }
}
