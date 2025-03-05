package Server.Interfaces;

import Common.Models.TextMessage;
import Server.DataAccess.DatabaseHandler;

public interface IMessageService {

    void receiveMessage(TextMessage message);
}
