package eu.hexsz.werewolf.test;

import eu.hexsz.werewolf.api.Message;
import eu.hexsz.werewolf.api.Session;
import lombok.NonNull;

public class IOSession extends Session {
    private final String fakeID;

    //dependencies
    private final @NonNull IOSocketService ioSocketService;

    public IOSession(String fakeID, IOSocketService ioSocketService) {
        super(ioSocketService);
        this.fakeID = fakeID;
        this.ioSocketService = ioSocketService;
    }

    @Override
    public String getSessionID() {
        return fakeID;
    }

    @Override
    public void send(Message message) {
        if (message != null) {
            ioSocketService.send(this, message.getSerializable());
        }
    }
}
