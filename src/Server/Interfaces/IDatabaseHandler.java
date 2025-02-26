package Server.Interfaces;
import Common.Models.User;
import Server.DataAccess.DatabaseHandler;

import java.sql.ResultSet;
import java.sql.SQLException;


public interface IDatabaseHandler {

    ResultSet getUsersData() throws SQLException ;
    void addUserData(User user) throws SQLException;

    Object getNextPendMsg(User user) throws SQLException;
    void appendPendMessage(Object msg) throws SQLException;
}
