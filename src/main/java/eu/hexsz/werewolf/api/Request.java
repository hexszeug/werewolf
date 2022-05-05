package eu.hexsz.werewolf.api;

import lombok.Getter;

import java.util.ArrayList;

/**
 * Represents a request which was sent by a client.
 * It has the following fields:
 * <ul>
 * <li>{@link Request#path}: determines which {@link RequestHandler} is used.
 * <li>{@link Request#type}: the type of the message specified in each {@link RequestHandler}.
 * <li>{@link Request#data}: the data of the message. Might be null. Also is specified in each {@link RequestHandler}.
 * </ul>
 * @see Session
 * @since 1.0-SNAPSHOT
 * @author hexszeug
 * */
@Getter
public class Request {
    private final String path;
    private final String type;
    private final Object data;

    /**
     * Creates a {@code Request} instance from {@code deserializedRequest}
     * to support getter for the three fields of a werewolf protocol request.
     * @param deserializedRequest The object to create the {@code Request} from.
     * @throws IllegalRequestException Is thrown if the deserialized request does not
     * conform to the protocol specifications.
     * @since 1.0-SNAPSHOT
     * */
    public Request(Object deserializedRequest) throws IllegalRequestException {
        if (!(deserializedRequest instanceof ArrayList)) {
            throw new IllegalRequestException(String.format("Request %s is not an array", deserializedRequest), null);
        }
        ArrayList request = (ArrayList) deserializedRequest;
        if (request.size() != 3) {
            throw new IllegalRequestException(String.format("Request %s has not exactly three elements.", request), null);
        }
        if (!(request.get(0) instanceof String)) {
            throw new IllegalRequestException(String.format("The path of request %s is not a string.", request), null);
        }
        if (!(request.get(1) instanceof String)) {
            throw new IllegalRequestException(String.format("The type of request %s is not a string.", request), null);
        }
        this.path = (String) request.get(0);
        this.type = (String) request.get(1);
        this.data = request.get(2);
    }
}
