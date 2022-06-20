package eu.hexsz.werewolf.api;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;

/**
 * Represents a message which can be sent to the client.
 * It has the following fields:
 * <ul>
 * <li>{@link Message#path}: determines to which {@link RequestHandler} potential responses should be sent to.
 * <li>{@link Message#type}: the type of the message specified in each {@link RequestHandler}.
 * <li>{@link Message#data}: the data of the message. Might be null. Also is specified in each {@link RequestHandler}.
 * </ul>
 * @see Session
 * @since 1.0-SNAPSHOT
 * @author hexszeug
 * */
@Getter
@AllArgsConstructor
public class Message {
    private final String path;
    private final String type;
    private final Object data;

    /**
     * Returns an object which when serialized corresponds the werewolf protocol
     * but misses getter and setter methods.
     * @since 1.0-SNAPSHOT
     * */
    public Object getSerializable() {
        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(path);
        arrayList.add(type);
        arrayList.add(data);
        return arrayList;
    }
}
