package name.ncg777.maths.exceptions;

/**
 * Base exception class for interval-related errors.
 * 
 * @author Nicolas Couture-Grenier
 */
public class IntervalException extends RuntimeException {
    
    public IntervalException(String message) {
        super(message);
    }
    
    public IntervalException(String message, Throwable cause) {
        super(message, cause);
    }
}