package Server.BusinessLogic;

import Common.Models.FileData;
import Common.Models.TextMessage;
import Server.ClientManager;
import Server.Interfaces.IFileService;
import Server.Interfaces.IMessageService;

import java.util.Arrays;

public class FileService implements IFileService {
    private SecurityModule securityModule;
    private ClientManager myClient;

    public FileService(ClientManager myClient){
        this.myClient = myClient;
        this.securityModule = new SecurityModule();
    }

    @Override
    public void receiveMessage(FileData message) {
        message.setData(securityModule.decipherString(Arrays.toString(message.getData())).getBytes());
        myClient.out.println(message);

    }
}
