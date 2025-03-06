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
    public void receiveMessage(TextMessage message) {
        System.out.println(message.getContent());
        String descifrado = securityModule.decipherString(message.getContent());
        System.out.println(descifrado);
        message.setContent(descifrado);
        myClient.out.println(message.message_formated());

    }
}
