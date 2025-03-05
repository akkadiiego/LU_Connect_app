package Server.Interfaces;

import Common.Models.User;

public interface INotificationService {

     void notifyUser(User user);
     void changeNotificationStatus(User user);

}
