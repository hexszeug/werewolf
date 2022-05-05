package eu.hexsz.werewolf.test;

import eu.hexsz.werewolf.api.Message;
import eu.hexsz.werewolf.api.Session;
import lombok.NonNull;

public class IOSession extends Session {
    private final String fakeID;

    //dependencies
    private final @NonNull IOSocketService IOSocketService;

    public IOSession(String fakeID, IOSocketService IOSocketService) {
        super(IOSocketService);
        this.fakeID = fakeID;
        this.IOSocketService = IOSocketService;
    }

    @Override
    public String getSessionID() {
        return fakeID;
    }

    @Override
    public void send(Message message) {
        IOSocketService.send(this, message.getSerializable());
    }
}
