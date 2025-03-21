package Server.BusinessLogic;

import Common.Models.TextMessage;
import Server.ClientManager;
import Server.Interfaces.IMessageService;

public class MessageService implements IMessageService {
    private SecurityModule securityModule;
    private ClientManager myClient;

    public MessageService(ClientManager myClient){
        this.myClient = myClient;

        this.securityModule = new SecurityModule();
    }

    @Override
    public TextMessage receiveMessage(TextMessage message) {
        String decipherString = securityModule.decipherString(message.getContent());
        return new TextMessage(message.getSender(), message.getReceiver(), decipherString, message.getTimestamp());

    }
}
