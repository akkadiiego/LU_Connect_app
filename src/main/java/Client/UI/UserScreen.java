package Client.UI;

import Client.Client;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.List;

public class UserScreen extends JPanel {
    LU_Connect_App luConnect;
    private JPanel userPanel;

    public UserScreen(LU_Connect_App luConnectApp){
        luConnect = luConnectApp;
        setLayout(new BorderLayout());
        setBackground(luConnectApp.BACKGROUND_COLOR);

        JLabel title = new JLabel("Users Online", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 22));
        title.setForeground(luConnectApp.GREY);

        JButton logOut = new JButton("Log out");
        styleButton(logOut);
        logOut.addActionListener(e -> {
            try {
                luConnect.logOut();
            } catch (IOException i) {
                throw new RuntimeException(i);
            }
        });

        userPanel = new JPanel(new GridLayout(3, 1));
        userPanel.setBackground(luConnectApp.SECOND_BACK_COLOR);

        add(title, BorderLayout.NORTH);
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

    public void updateUserList(List<String> onlineUsers) {
        SwingUtilities.invokeLater(() -> {
            userPanel.removeAll();
            if (onlineUsers != null) {
                for (String user : onlineUsers) {
                    JButton userButton = new JButton(user);
                    userButton.addActionListener(e -> {
                        luConnect.setTargetClient(user);
                        luConnect.showScreen("ChatScreen");
                        luConnect.getClient().startChatWith(user);
                    });
                    userPanel.add(userButton);
                }
            }

            userPanel.revalidate();
            userPanel.repaint();
        });
    }


}
