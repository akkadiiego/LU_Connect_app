package Server.BusinessLogic;

import Common.Models.User;
import Server.Interfaces.IAuthenticationService;

import java.util.ArrayList;
import java.util.HashSet;

public class AuthenticationService implements IAuthenticationService {
    @Override
    public boolean authenticateNewUser(User user, HashSet<User> users) {
        if(users == null){
            return false;
        }

        for ( User userReg : users){
            if (userReg.getUsername().equals(user.getUsername())){
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean authenticateUser(User user, HashSet<User> users) {
        if(users == null){
            return false;
        }
        return users.contains(user);
    }

}
