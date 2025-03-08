package Server.BusinessLogic;

import Client.API.MessageAdapter;
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
    public void receiveMessage(TextMessage message) {
        String decipherString = securityModule.decipherString(message.getContent());
        message.setContent(decipherString);

    }
}
