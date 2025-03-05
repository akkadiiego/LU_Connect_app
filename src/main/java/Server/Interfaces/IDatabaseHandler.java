package Server.Interfaces;
import Common.Models.User;
import Server.DataAccess.DatabaseHandler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;


public interface IDatabaseHandler {

    HashSet<User> getUsersData() throws SQLException ;
    void addUserData(User user) throws SQLException;

    Object getNextPendMsg(User user) throws SQLException;
    void appendPendMessage(Object msg) throws SQLException;

    boolean checkUserState(User user) throws SQLException;
    void changeUserState(User user, boolean state) throws SQLException;
}
