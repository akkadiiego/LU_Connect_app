package Server.Interfaces;

import Common.Models.FileData;

public interface IFileService {

    void receiveMessage(FileData message);
}
