package Server.BusinessLogic;

import Common.Models.TextMessage;
import Server.ClientManager;
import Server.Interfaces.IMessageService;

public class MessageService implements IMessageService {
    private SecurityModule securityModule;
    private ClientManager myClient;
    private ClientManager targetClient;

    public MessageService(ClientManager myClient, ClientManager targetClient){
        this.myClient = myClient;
        this.targetClient = targetClient;
        this.securityModule = new SecurityModule();
    }

    @Override
    public void receiveMessage(TextMessage message) {
        message.setContent(securityModule.decipherString(message.getContent()));
        myClient.out.println(message);

    }
}
