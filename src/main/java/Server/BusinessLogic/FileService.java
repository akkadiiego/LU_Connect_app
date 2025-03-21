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
    public FileData receiveMessage(FileData message) {
        byte[] decryptedData = securityModule.decipherString(Arrays.toString(message.getData())).getBytes();
        return new FileData(message.getSender(), message.getReceiver(), message.getTimestamp(), message.getFilename(), decryptedData.length, decryptedData);
    }
}
