package name.ncg777.maths.exceptions;

/**
 * Exception thrown when attempting to create an invalid interval,
 * such as with contradictory boundary specifications.
 * 
 * @author Nicolas Couture-Grenier
 */
public class InvalidIntervalException extends IntervalException {
    
    public InvalidIntervalException(String message) {
        super(message);
    }
    
    public InvalidIntervalException(String message, Throwable cause) {
        super(message, cause);
    }
}