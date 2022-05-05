package eu.hexsz.werewolf.test;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import eu.hexsz.werewolf.api.IllegalRequestException;
import eu.hexsz.werewolf.api.Request;
import eu.hexsz.werewolf.api.Session;
import eu.hexsz.werewolf.api.SessionRegistry;
import lombok.AllArgsConstructor;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@AllArgsConstructor
public class IOSocketService extends SessionRegistry {
    private InputStream inputStream;
    private OutputStream outputStream;

    public void send(IOSession session, Object message) {
        ArrayList<?> messageList = (ArrayList) message;
        print("-> %s: %s %s %s",
                session.getSessionID(),
                messageList.get(0),
                messageList.get(1),
                new Gson().toJson(messageList.get(2))
        );
    }

    public void startReceiving() {
        new Thread(() -> {
            Scanner scanner = new Scanner(inputStream);
            while (true) {
                receive(scanner.nextLine());
            }
        }).start();
    }

    private void receive(String messageStr) {
        Pattern pattern = Pattern.compile("(\\S+)\\s*:\\s+(\\S+)\\s+(\\S+)\\s+(.+)");
        Matcher matcher = pattern.matcher(messageStr);
        if (!matcher.find()) {
            print("Wrong syntax! Use: <sessionID>: <path> <type> <data>");
            return;
        }
        Session receiver;
        try {
            receiver = IOGameStart.IO_SOCKET_SERVICE.getSession(matcher.group(1));
        } catch (NullPointerException e) {
            print("Session does not exist!");
            return;
        }
        Object data;
        try {
            data = new Gson().fromJson(matcher.group(4), Object.class);
        } catch (JsonSyntaxException e) {
            print("Wrong data syntax! " +  e.getLocalizedMessage());
            return;
        }
        try {
            receiver.receive(
                    new Request(
                            new ArrayList<>(Arrays.asList(
                                    matcher.group(2),
                                    matcher.group(3),
                                    data
                            ))
                    )
            );
        } catch (IllegalRequestException e) {
            print(e.getLocalizedMessage());
            return;
        }
    }

    private void print(String x) {
        try {
            outputStream.write(
                    (
                            "\r"
                            + x.replace("\n", "\\n ")
                            + "\n\r"
                    ).getBytes()
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void print(String format, Object... args) {
        print(String.format(format, args));
    }
}
