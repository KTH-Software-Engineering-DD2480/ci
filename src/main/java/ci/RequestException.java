package ci;

import jakarta.servlet.ServletException;

/// Custom exception that includes a HTTP status code.
public class RequestException extends ServletException {
    public int status;

    /// Wrap some other exception with a status code
    public RequestException(int status, Throwable cause) {
        super(cause);
        this.status = status;
    }

    /// A message with a status code
    public RequestException(int status, String message) {
        super(message);
        this.status = status;
    }
}
