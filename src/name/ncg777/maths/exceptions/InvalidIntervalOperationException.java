package name.ncg777.maths.exceptions;

/**
 * Exception thrown when an invalid operation is attempted on intervals,
 * such as trying to merge non-overlapping intervals.
 * 
 * @author Nicolas Couture-Grenier
 */
public class InvalidIntervalOperationException extends IntervalException {
    
    public InvalidIntervalOperationException(String message) {
        super(message);
    }
    
    public InvalidIntervalOperationException(String message, Throwable cause) {
        super(message, cause);
    }
}