package Client.API;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MessageAdapter {
    String myMessage;

    public MessageAdapter(String message){
        myMessage = message;
    }

    public String formatMessage() {
        StringBuilder finalMessage = new StringBuilder();
        ArrayList<String> message = new ArrayList<>(List.of(myMessage.split(": ")));

        String content = "";
        if (message.size() > 1) {
            content = message.get(1);
        }

        message = new ArrayList<>(Arrays.asList(message.get(0).split("/")));

        String timestamp = message.get(0);
        if (message.size() > 1) {
            message = new ArrayList<>(Arrays.asList(message.get(1).split(" -> ")));
        }

        String sender = message.getFirst();
        String receiver = message.getLast();

        finalMessage.append(sender).append(": ").append(content).append(" ");
        return finalMessage.toString();
    }

}
