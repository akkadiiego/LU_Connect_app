package Server.Interfaces;

import Common.Models.User;

import java.util.HashSet;

public interface IAuthenticationService {

    boolean authenticateNewUser(User user, HashSet<User> users);
    boolean authenticateUser(User user, HashSet<User> users);

}
