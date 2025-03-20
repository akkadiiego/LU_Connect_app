package Client.UI;

import Client.Client;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class UserScreen extends JPanel {
    LU_Connect_App luConnect;
    private JPanel userPanel;
    private boolean notificationState;

    public UserScreen(LU_Connect_App luConnectApp){
        luConnect = luConnectApp;
        setLayout(new BorderLayout());
        setBackground(luConnectApp.BACKGROUND_COLOR);

        JPanel topPanel = new JPanel(new BorderLayout());
        JLabel title = new JLabel("Users Online", SwingConstants.CENTER);
        title.setForeground(luConnectApp.GREY);

        title.setFont(new Font("Arial", Font.BOLD, 22));
        JButton notifications = new JButton("Notications: " + currentState());
        styleButton(notifications);
        notifications.addActionListener(e -> {
            luConnect.setNotificationState();
            notifications.setText("Notications: " + currentState());
        });


        topPanel.setBackground(luConnectApp.BACKGROUND_COLOR);
        topPanel.add(title, BorderLayout.CENTER);
        topPanel.add(notifications, BorderLayout.EAST);

        JButton logOut = new JButton("Log out");
        styleButton(logOut);
        logOut.addActionListener(e -> {
            luConnect.logOut();
        });

        userPanel = new JPanel(new GridLayout(3, 1));
        userPanel.setBackground(luConnectApp.SECOND_BACK_COLOR);

        add(topPanel, BorderLayout.NORTH);
        add(new JScrollPane(userPanel), BorderLayout.CENTER);
        add(logOut, BorderLayout.SOUTH);
    }

    private void styleButton(JButton button) {
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(luConnect.RED);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    public void updateUserList(List<String> onlineUsers, ArrayList<String> notifyUsers) {
        SwingUtilities.invokeLater(() -> {
            userPanel.removeAll();
            if (onlineUsers != null) {
                for (String user : onlineUsers) {
                    JButton userButton = new JButton(user);
                    if (notifyUsers.contains(user)){
                        userButton.setText(user + "(NEW MESSAGE)");
                    }
                    userButton.addActionListener(e -> {
                        luConnect.setTargetClient(user);
                        luConnect.showScreen("ChatScreen");
                        luConnect.getClient().startChatWith(user);
                        luConnect.newMessageUsers.remove(user);
                    });
                    userPanel.add(userButton);
                }
            }

            userPanel.revalidate();
            userPanel.repaint();
        });
    }

    private String currentState(){
        if (luConnect.isNotificationState()) {
            return "On";
        }
        return "Off";
    }


}
