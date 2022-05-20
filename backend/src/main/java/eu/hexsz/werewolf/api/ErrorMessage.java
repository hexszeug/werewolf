package eu.hexsz.werewolf.api;

import java.util.LinkedHashMap;

/**
 * Subclass of {@link Message} which should make it easier to create error messages and to specify their form.
 * @see Message
 * @since 1.0-SNAPSHOT
 * @author hexszeug
 * */
public class ErrorMessage extends Message {

    /**
     * Creates new {@code ErrorMessage} from a {@link Throwable}.
     * The error name is set to the simple class name of the {@code Throwable}
     * and the error message is set to the return value of {@link Throwable#getLocalizedMessage()}.
     * @since 1.0-SNAPSHOT
     * */
    public ErrorMessage(String path, Throwable throwable) {
        super(
                path,
                "error",
                throwable != null
                        ? generateErrorData(throwable)
                        : generateErrorData()
        );
    }

    private static Object generateErrorData(Throwable throwable) {
        LinkedHashMap<String, Object> data = new LinkedHashMap<>();
        data.put("name", throwable.getClass().getSimpleName());
        data.put("message", throwable.getLocalizedMessage());
        if (throwable instanceof IllegalRequestException) {
            data.put("request", ((IllegalRequestException) throwable).getRequest());
        }
        return data;
    }

    private static Object generateErrorData() {
        LinkedHashMap<String, Object> data = new LinkedHashMap<>();
        data.put("name", null);
        data.put("message", null);
        return data;
    }
}
