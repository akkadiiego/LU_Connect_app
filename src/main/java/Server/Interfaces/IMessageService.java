package Server.Interfaces;

import Common.Models.TextMessage;

public interface IMessageService {

    TextMessage receiveMessage(TextMessage message);
}
