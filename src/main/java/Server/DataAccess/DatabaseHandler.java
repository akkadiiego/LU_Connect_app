package Server.DataAccess;

import Common.Models.FileData;
import Common.Models.Message;
import Common.Models.textMessage;
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
        conn = DriverManager.getConnection("jdbc:sqlite:" + DB_PATH);
    }

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
        String sql = "INSERT OR IGNORE INTO users (username, password, is_online) " +
                "VALUES (?,?,?);";

        try{
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, user.getUsername());
            preparedStatement.setString(2, user.getPassword());
            preparedStatement.setBoolean(3, user.isOnline());
            preparedStatement.executeUpdate();
        }catch(SQLException e){
            e.printStackTrace();
        }
    }

    @Override
    public Message getNextPendMsg(User user) throws SQLException {

        String sql = "SELECT * FROM pending_messages WHERE message_id = ( SELECT MIN(message_id) FROM pending_messages WHERE receiver_id = ?) ;";
        try {
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setInt(1, getUserid(user.getUsername()));
            ResultSet result = preparedStatement.executeQuery();
            if (!result.next()) {
                return null;
            }

            String senderSql = "SELECT username FROM users WHERE user_id = ?";

            PreparedStatement senderPreparedStatement = conn.prepareStatement(senderSql);
            senderPreparedStatement.setInt(1, result.getInt("sender_id"));
            String sender = senderPreparedStatement.executeQuery().getString("username");

            String receiverSql = "SELECT username FROM users WHERE user_id = ?";

            PreparedStatement receiverPreparedStatement = conn.prepareStatement(receiverSql);
            preparedStatement.setInt(1, result.getInt("receiver_id"));
            String receiver = receiverPreparedStatement.executeQuery().getString("username");

            LocalDateTime timestamp = result.getTimestamp("timestamp").toLocalDateTime();

            if (result.getString("message_type").equals("TEXT")) {

                return new textMessage(sender, receiver, result.getString("content"), timestamp);
            }
            else if (result.getString("message_type").equals("FILE")) {
                String filename = result.getString("filename");
                long fileSize = result.getBytes("file_data").length;
                byte[] data = result.getBytes("file_data");

                return new FileData(sender, receiver, timestamp, filename, fileSize, data);
            }
            else {
                throw new SQLException("Invalid message type");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

    }

    @Override
    public void appendPendMessage(Object msg) throws SQLException {
        if (msg instanceof textMessage){
            String sql = "INSERT INTO pending_messages (sender_id, receiver_id, message_type, file_data, filename, content, timestamp) " +
                    "VALUES (?,?, 'TEXT', NULL, NULL, ?, ?);";
            try {
                PreparedStatement preparedStatement = conn.prepareStatement(sql);
                preparedStatement.setInt(1, getUserid(((textMessage) msg).getSender()));
                preparedStatement.setInt(2, getUserid(((textMessage) msg).getReceiver()));
                preparedStatement.setString(3, ((textMessage) msg).getContent());
                preparedStatement.setTimestamp(4, Timestamp.valueOf(((textMessage) msg).getTimestamp()));

                preparedStatement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }


        } else if (msg instanceof FileData) {
            String sql = "INSERT INTO pending_messages (sender_id, receiver_id, message_type, file_data, filename, content, timestamp) " +
                    "VALUES (?,?, 'FILE', ?, ?, NULL, ?);";
            try {
                PreparedStatement preparedStatement = conn.prepareStatement(sql);
                preparedStatement.setInt(1, getUserid(((FileData) msg).getSender()));
                preparedStatement.setInt(2, getUserid(((FileData) msg).getReceiver()));
                preparedStatement.setBytes(3, ((FileData) msg).getData());
                preparedStatement.setString(4, ((FileData) msg).getFilename());
                preparedStatement.setTimestamp(5, Timestamp.valueOf(((FileData) msg).getTimestamp()));
                preparedStatement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        else {
            throw new SQLException("Invalid message type");
        }
    }

    private int getUserid(String username) throws SQLException {
        String sql = "SELECT user_id FROM users WHERE username = ?";

        try {
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, username);
            ResultSet result = preparedStatement.executeQuery();
            if (!result.next()) {
                return -1;
            }
            return result.getInt("user_id");
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    // Use when message is delivered to de receiver
    public void removePendMsg(int msgId) throws SQLException {
        String sql = "DELETE FROM pending_messages WHERE message_id = ?";

        try {
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setInt(1, msgId);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // By the moment, only used for testing
    public void removeUser(String username) throws SQLException {
        String sql = "DELETE FROM users WHERE username = ?";

        try {
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, username);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void removeHighestMsg () throws SQLException {
        String sql = "DELETE FROM pending_messages WHERE message_id = (SELECT MAX(message_id) FROM pending_messages);";

        try {
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // to close connection
    public void close() throws SQLException {
        conn.close();
    }
}
