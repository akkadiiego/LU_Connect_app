package Common.Models;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

public class FileData implements Serializable {
    private String sender;
    private String receiver;
    private LocalDateTime timestamp;
    private String filename;
    private long fileSize;
    private byte[] data;

    // Constructor
    public FileData(String sender, String receiver, LocalDateTime timestamp, String filename, long fileSize, byte[] data) {
        this.sender = sender;
        this.receiver = receiver;
        this.timestamp = timestamp;
        this.filename = filename;
        this.fileSize = fileSize;
        this.data = data;
    }

    // Getters and Setters
    public String getSender() { return sender; }
    public void setSender(String sender) { this.sender = sender; }

    public String getReceiver() { return receiver; }
    public void setReceiver(String receiver) { this.receiver = receiver; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public String getFilename() { return filename; }
    public void setFilename(String filename) { this.filename = filename; }

    public long getFileSize() { return fileSize; }
    public void setFileSize(long fileSize) { this.fileSize = fileSize; }

    public byte[] getData() { return data; }
    public void setData(byte[] data) { this.data = data; }


    @Override
    public String toString() {
        return "Archivo: " + filename + " (" + fileSize + " bytes) enviado por " + sender;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FileData)) return false;
        FileData fileData = (FileData) o;
        return fileSize == fileData.fileSize &&
                Objects.equals(sender, fileData.sender) &&
                Objects.equals(receiver, fileData.receiver) &&
                Objects.equals(filename, fileData.filename);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sender, receiver, filename, fileSize);
    }

    public String dataToHex() {
        StringBuilder hexString = new StringBuilder();
        for (byte b : this.data) {
            hexString.append(String.format("%02X", b)); //
        }
        return hexString.toString();
    }
}

