package eu.hexsz.werewolf.api;

import java.util.HashMap;

/**
 * Singleton
 * <br>Stores all {@link Session}s and provides access via sessionID to them.
 * @see Session
 * @since 1.0-SNAPSHOT
 * @author hexszeug
 * */
public class SessionRegistry {
    private HashMap<String, Session> sessions;

    /**
     * Only one {@code SessionRegistry} should exist over the whole application.
     * @since 1.0-SNAPSHOT
     * */
    public SessionRegistry() {
        sessions = new HashMap<>();
    }

    /**
     * Returns the {@link Session} with the passed sessionID.
     * @param sessionID The sessionID of the requested {@link Session}.
     * @throws NullPointerException When no session with the passed sessionID exists.
     * @return The requested {@link Session}.
     * @since 1.0-SNAPSHOT
     * */
    public Session getSession(String sessionID) throws NullPointerException {
        Session session = sessions.get(sessionID);
        if (session == null) {
            removeSession(sessionID);
            throw new NullPointerException();
        }
        return session;
    }

    /**
     * Adds the passed {@link Session} to the register.
     * Should only be called by {@link Session#Session(String, SessionRegistry)}
     * @param session The {@code Session} to add.
     * @since 1.0-SNAPSHOT
     * */
    public void addSession(Session session) {
        sessions.put(session.getSessionID(), session);
    }

    /**
     * Removes the {@link Session} with the passed sessionID. Does nothing if the {@code Session} does not exist.
     * @param sessionID The sessionID of the {@code Session} to remove.
     * @since 1.0-SNAPSHOT
     * */
    public void removeSession(String sessionID) {
        sessions.remove(sessionID);
    }
}
