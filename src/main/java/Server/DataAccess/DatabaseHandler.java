package Server.DataAccess;

import Common.Models.FileData;
import Common.Models.Message;
import Common.Models.TextMessage;
import Common.Models.User;
import Server.BusinessLogic.SecurityModule;
import Server.Interfaces.IDatabaseHandler;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;

import static Common.Utils.Config.DB_PATH;

public class DatabaseHandler implements IDatabaseHandler {
    private static DatabaseHandler instance; // singleton instance
    private static Connection conn; // connection to SQLite database
    private static SecurityModule securityModule; // decrypts stored passwords

    private DatabaseHandler() throws SQLException {
        conn = DriverManager.getConnection("jdbc:sqlite:" + DB_PATH);
        securityModule = new SecurityModule();
    }

    public static synchronized DatabaseHandler getInstance() throws SQLException {
        if (Objects.isNull(instance)){
            return new DatabaseHandler();
        }
        return instance;
    }

    @Override
    public HashSet<User> getUsersData() throws SQLException {
        ResultSet result = conn.createStatement().executeQuery("SELECT * FROM users;");
        ResultSetMetaData metaData = result.getMetaData();
        int columnsNumber = metaData.getColumnCount();
        HashSet<User> users = new HashSet<>();
        boolean isOnline = false;

        while (result.next()) {
            int i = 1;
            ArrayList<Object> arguments = new ArrayList<>();
            while (i <= columnsNumber) {
                arguments.add(result.getObject(i));
                i++;
            }
            String password = securityModule.decipherString((String) arguments.get(2));
            if ((Integer) arguments.get(3) != 0) {
                isOnline = true;
            } else {
                isOnline = false;
            }
            users.add(new User((String) arguments.get(1), password, isOnline));
        }
        return users;
    }

    @Override
    public void addUserData(User user) throws SQLException{
        String sql = "INSERT OR IGNORE INTO users (username, password, is_online) VALUES (?,?,?);";

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
    public Message getNextPendMsg(User user, User sender) throws SQLException {
        int message_id = getNextMsgId(user, sender);

        String sql = "SELECT * FROM pending_messages WHERE message_id = ? ;";
        try {
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setInt(1, message_id);
            ResultSet result = preparedStatement.executeQuery();
            if (!result.next()) {
                return null;
            }

            String userSql = "SELECT * FROM users WHERE user_id = ?";
            PreparedStatement receiverPreparedStatement = conn.prepareStatement(userSql);
            preparedStatement.setInt(1, result.getInt("receiver_id"));
            ResultSet userInfo = receiverPreparedStatement.executeQuery();
            User receiver = new User(userInfo.getString("username"), securityModule.decipherString(userInfo.getString("password")), userInfo.getBoolean("is_online"));

            LocalDateTime timestamp = result.getTimestamp("timestamp").toLocalDateTime();

            if (result.getString("message_type").equals("TEXT")) {
                return new TextMessage(sender, receiver, result.getString("content"), timestamp);
            } else if (result.getString("message_type").equals("FILE")) {
                String filename = result.getString("filename");
                byte[] b = result.getBytes("file_data");

                InputStream is = new ByteArrayInputStream(b);
                int fileSize = is.available();
                byte[] data = new byte[fileSize];
                is.read(data, 0, fileSize);
                is.close();

                return new FileData(sender, receiver, timestamp, filename, fileSize, data);
            } else {
                throw new SQLException("Invalid message type");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void appendPendMessage(Object msg) throws SQLException {
        if (msg instanceof TextMessage){
            String sql = "INSERT INTO pending_messages (sender_id, receiver_id, message_type, file_data, filename, content, timestamp) VALUES (?,?, 'TEXT', NULL, NULL, ?, ?);";
            try {
                PreparedStatement preparedStatement = conn.prepareStatement(sql);
                preparedStatement.setInt(1, getUserid(((TextMessage) msg).getSender().getUsername()));
                preparedStatement.setInt(2, getUserid(((TextMessage) msg).getReceiver().getUsername()));
                preparedStatement.setString(3, ((TextMessage) msg).getContent());
                preparedStatement.setTimestamp(4, Timestamp.valueOf(((TextMessage) msg).getTimestamp()));

                preparedStatement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else if (msg instanceof FileData) {
            String sql = "INSERT INTO pending_messages (sender_id, receiver_id, message_type, content, file_data, filename, timestamp) VALUES (?,?, 'FILE', NULL, ?, ?, ?);";
            try {
                PreparedStatement preparedStatement = conn.prepareStatement(sql);
                preparedStatement.setInt(1, getUserid(((FileData) msg).getSender().getUsername()));
                preparedStatement.setInt(2, getUserid(((FileData) msg).getReceiver().getUsername()));
                preparedStatement.setBytes(3, ((FileData) msg).getData());
                preparedStatement.setString(4, ((FileData) msg).getFilename());
                preparedStatement.setTimestamp(5, Timestamp.valueOf(((FileData) msg).getTimestamp()));
                preparedStatement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            throw new SQLException("Invalid message type");
        }
    }

    @Override
    public int getNextMsgId(User user, User sender) throws SQLException {
        String sql = "SELECT * FROM pending_messages WHERE message_id = ( SELECT MIN(message_id) FROM pending_messages WHERE receiver_id = ? AND sender_id = ?) ;";
        try {
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setInt(1, getUserid(user.getUsername()));
            preparedStatement.setInt(2,getUserid(sender.getUsername()));
            ResultSet result = preparedStatement.executeQuery();
            if (!result.next()) {
                return -1;
            }
            return result.getInt("message_id");
        } catch (SQLException e){
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    public boolean checkUserState(User user) throws SQLException {
        HashSet<User> users = getUsersData();
        for (User userReg : users){
            if (userReg.getUsername().equals(user.getUsername())){
                return userReg.isOnline();
            }
        }
        return false;
    }

    @Override
    public void changeUserState(User user, boolean state) throws SQLException {
        try {
            String sql = "UPDATE users SET is_online = ? WHERE user_id = ?;";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setBoolean(1, state);
            preparedStatement.setInt(2, getUserid(user.getUsername()));
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
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

    public void popPendMsg(int msgId) throws SQLException { // delete message after delivery
        String sql = "DELETE FROM pending_messages WHERE message_id = ?";
        try {
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setInt(1, msgId);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void removeUser(String username) throws SQLException { // remove user by username (for testing)
        String sql = "DELETE FROM users WHERE username = ?";
        try {
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, username);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void removeAllTestMsg(){ // remove test messages from testName sender
        String sql = "DELETE FROM pending_messages WHERE sender_id = (SELECT user_id FROM users WHERE username = ?)";
        PreparedStatement preparedStatement;
        try {
            preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, "testName");
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void close() throws SQLException { // close database connection
        conn.close();
    }

    public static void main(String[] args) throws SQLException {
        DatabaseHandler databaseHandler = DatabaseHandler.getInstance();
        databaseHandler.getUsersData();
    }
}
