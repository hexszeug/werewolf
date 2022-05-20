package eu.hexsz.werewolf.api;

import lombok.Getter;

/**
 * Can be thrown by {@link RequestHandler#receive(Request)} implementations
 * if any problem with the request occurs. Is caught by the {@link Session} and converted to an Error message
 * which is returned to the client.
 * @since 1.0-SNAPSHOT
 * @author hexszeug
 * */
public class IllegalRequestException extends Exception {
    private final @Getter Object request;

    public IllegalRequestException(String message, Request request) {
        super(message);
        this.request = (request == null) ? null : request.getSerializable();
    }
}
