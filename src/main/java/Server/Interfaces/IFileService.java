package Server.Interfaces;

import Common.Models.FileData;

public interface IFileService {

    FileData receiveMessage(FileData message);
}
