package g61453.qwirkle.Model;
/**
 * A custom exception class for the Qwirkle game.
 * This exception is used to indicate errors or exceptional conditions specific to the Qwirkle game.
 */
public class QwirkleException extends RuntimeException{
    /**
     * Constructs a new QwirkleException with no detail message.
     */
    QwirkleException(){
        super();
    }
    /**
     * Constructs a new QwirkleException with the specified detail message.
     *
     * @param message The detail message, which provides information about the exception.
     */
    QwirkleException(String message){
        super(message);
    }
}
