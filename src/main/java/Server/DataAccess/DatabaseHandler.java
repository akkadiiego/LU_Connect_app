package Server.DataAccess;

import Common.Models.FileData;
import Common.Models.Message;
import Common.Models.textMessage;
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
    private static DatabaseHandler instance;
    private static Connection conn;
    private static SecurityModule securityModule;

    private DatabaseHandler() throws SQLException {
        conn = DriverManager.getConnection("jdbc:sqlite:" + DB_PATH);
        securityModule = new SecurityModule();
    }

    public static synchronized DatabaseHandler getInstance() throws SQLException {
        if (Objects.isNull(instance)){
        return new DatabaseHandler();}
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
                System.out.println(result.getObject(i));
                arguments.add(result.getObject(i));
                i++;
            }
            String password = securityModule.decipherString((String) arguments.get(2));
            if ((Integer) arguments.get(3) != 0) {
                isOnline = true;
            }
            users.add(new User((String) arguments.get(1), password, isOnline));
        }
        return users;
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
                byte[] b = result.getBytes("file_data");

                InputStream is = new ByteArrayInputStream(b);
                int fileSize = is.available();
                byte[] data = new byte[fileSize];
                is.read(data, 0, fileSize);
                is.close();

                return new FileData(sender, receiver, timestamp, filename, fileSize, data);
            }
            else {
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
            String sql = "INSERT INTO pending_messages (sender_id, receiver_id, message_type, content, file_data, filename, timestamp) " +
                    "VALUES (?,?, 'FILE', NULL, ?, ?, ?);";
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

    // Use when message is delivered to de receiver
    public void popPendMsg(int msgId) throws SQLException {
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


   /* public void removeHighestMsg () throws SQLException {
        String sql = "DELETE FROM pending_messages WHERE message_id = (SELECT MAX(message_id) FROM pending_messages);";

        try {
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    } */

    public void removeAllTestMsg(){
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
    /*public void removeAll() throws SQLException {
        String sql = "DELETE FROM pending_messages";
        try {
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        sql = "DELETE FROM users";
        try {
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.executeUpdate();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }*/

    // to close connection
    public void close() throws SQLException {
        conn.close();
    }

    public static void main(String[] args) throws SQLException {
        DatabaseHandler databaseHandler = DatabaseHandler.getInstance();
        databaseHandler.getUsersData();
    }
}
