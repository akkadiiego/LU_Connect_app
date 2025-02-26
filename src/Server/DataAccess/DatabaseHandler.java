package Server.DataAccess;

import Common.Models.FileData;
import Common.Models.Message;
import Common.Models.User;
import Server.Interfaces.IDatabaseHandler;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.Objects;

import static Common.Utils.Config.DB_PATH;

public class DatabaseHandler implements IDatabaseHandler {
    private static DatabaseHandler instance;
    private static Connection conn;

    private DatabaseHandler() throws SQLException {
        this.conn = DriverManager.getConnection("jdbc_sqlite:" + DB_PATH);
    };

    public static synchronized DatabaseHandler getInstance() throws SQLException {
        if (Objects.isNull(instance)){
        return new DatabaseHandler();}
        return instance;
    }
    @Override
    public ResultSet getUsersData() throws SQLException {
        return conn.createStatement().executeQuery("SELECT * FROM users");

    }

    @Override
    public void addUserData(User user) throws SQLException{
        conn.createStatement().executeQuery(
                "INSERT OR IGNORE INTO users (user_id, username, password, is_online) " +
                        "VALUES ( " + user.getUsername() + ", " + user.getPasswordHash() + ", "+ user.isOnline() + ");");
    }

    @Override
    public Object getNextPendMsg(User user) throws SQLException {
        ResultSet result = conn.createStatement().executeQuery("SELECT * FROM pending_messages WHERE message_id = ( SELECT MIN(message_id) FROM pending_messages WHERE receiver_id = " + getUserid(user.getUsername()) + ");");
        String sender = conn.createStatement().executeQuery("SELECT username FROM users WHERE user_id = " + result.getInt("sender_id") + ";").getString("username");
        String receiver = conn.createStatement().executeQuery("SELECT username FROM users WHERE user_id = " + result.getInt("receiver_id")).getString("username");
        LocalDateTime timestamp = result.getTimestamp("timestamp").toLocalDateTime();


        if (result.getString("message_type").equals("TEXT")) {
            String content = result.getString("content");
            return new Message(sender, receiver, content, timestamp);
        } else if (result.getString("message_type").equals("FILE")) {
            String filename = result.getString("filename");

            /*
            Is it possible that this data here is modified, so it does not work, you know why (in order to be saved is converted to hex before, so when you pull it, it is maybe in hex format)
            */
            byte[] data = result.getBytes("file_data");
            //


            return new FileData(sender, receiver, timestamp, filename, data.length, data);
        }
        else {
            throw new SQLException("Invalid message type");
        }
    }

    @Override
    public void appendPendMessage(Object msg) throws SQLException {
        if (msg instanceof Message){
            conn.createStatement().executeQuery(
                    "INSERT INTO pending_messages (message_id, sender_id, receiver_id, message_type, file_data, filename, content, timestamp) " +
                            "VALUES ( " + getUserid(((Message) msg).getSender()) + ", " + getUserid(((Message) msg).getReceiver()) + ", " + "'TEXT'" + ", " + ((Message) msg).getContent() + ", " + "NULL, NULL" + ((Message) msg).getTimestamp() + ");");
        } else if (msg instanceof FileData) {
            conn.createStatement().executeQuery(
                    "INSERT INTO pending_messages (message_id, sender_id, receiver_id, message_type, file_data, filename, content, timestamp) " +
                            "VALUES ( " + getUserid(((FileData) msg).getSender()) + ", " + getUserid(((FileData) msg).getReceiver()) + ", " + "'FILE'" + ", NULL" + msg + ((FileData) msg).dataToHex() + ", " + ((FileData) msg).getFilename() + ", " + ((FileData) msg).getTimestamp() + ");");
        }
        else {
            throw new SQLException("Invalid message type");
        }
    }

    private int getUserid(String username) throws SQLException {
        return conn.createStatement().executeQuery("SELECT user_id FROM users WHERE username = " + username).getInt("user_id");
    }
}
