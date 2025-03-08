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

    public UserScreen(LU_Connect_App luConnectApp){
        luConnect = luConnectApp;
        setLayout(new BorderLayout());

        JLabel title = new JLabel("Users", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));

        JButton logOut = new JButton("Log out");


        logOut.addActionListener(e-> {
            try {
                luConnect.logOut();
            } catch (IOException i) {
                throw new RuntimeException(i);
            }
        });

        userPanel = new JPanel();
        userPanel.setLayout(new GridLayout(3, 1));


        JPanel panelFloor = new JPanel(new BorderLayout());
        JPanel leftFloor = new JPanel(new FlowLayout(FlowLayout.LEFT));
        leftFloor.add(logOut);
        panelFloor.add(leftFloor, BorderLayout.CENTER);


        add(panelFloor, BorderLayout.SOUTH);
        add(new JScrollPane(userPanel), BorderLayout.CENTER);
        add(title, BorderLayout.NORTH);
    }

    public void updateUserList(List<String> onlineUsers) {
        SwingUtilities.invokeLater(() -> {
            userPanel.removeAll();

            for (String user : onlineUsers) {
                JButton userButton = new JButton(user);
                userButton.addActionListener(e -> {
                    luConnect.getClient().startChatWith(user);
                    luConnect.showScreen("ChatScreen");
                });
                userPanel.add(userButton);
            }

            userPanel.revalidate();
            userPanel.repaint();
        });
    }


}
