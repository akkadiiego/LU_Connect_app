package Common.Models;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Base64;
import java.util.Objects;

public class FileData extends Message implements Serializable{



    // Constructor
    public FileData(User sender, User receiver, LocalDateTime timestamp, String filename, int fileSize, byte[] data) {
        super(sender, receiver, timestamp);
        this.filename = filename;
        this.fileSize = fileSize;
        this.data = data;
    }

    // Getters and Setters

    public String getFilename() { return filename; }
    public void setFilename(String filename) { this.filename = filename; }

    public long getFileSize() { return fileSize; }
    public void setFileSize(int fileSize) { this.fileSize = fileSize; }

    public byte[] getData() { return data; }
    public void setData(byte[] data) { this.data = data; }


    @Override
    public String toString() {
        return getSender() + " -> " + filename  + " of " + fileSize + " bytes //// " + Base64.getEncoder().encodeToString(getData()).replaceAll("[^A-Za-z0-9+/=]", "");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FileData)) return false;
        FileData fileData = (FileData) o;
        return fileSize == fileData.fileSize &&
                Objects.equals(getSender(), fileData.getSender()) &&
                Objects.equals(getReceiver(), fileData.getReceiver()) &&
                Objects.equals(filename, fileData.filename);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getSender(), getReceiver(), filename, fileSize);
    }

}

