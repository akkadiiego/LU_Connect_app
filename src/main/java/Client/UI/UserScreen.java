package Client.UI;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.List;
import javax.sound.sampled.*;

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
        JButton notifications = new JButton("Notications: " + luConnectApp.currentState());
        styleButton(notifications);
        notifications.addActionListener(e -> {
            luConnect.setNotificationState();
            notifications.setText("Notications: " + luConnectApp.currentState());
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

    private Icon createDotIcon(Color color) {
        return new Icon() {
            private final int SIZE = 8;
            @Override public void paintIcon(Component c, Graphics g, int x, int y) {
                g.setColor(color);
                g.fillOval(x, y, SIZE, SIZE);
            }
            @Override public int getIconWidth() { return SIZE; }
            @Override public int getIconHeight() { return SIZE; }
        };
    }

    public void updateUserList(List<String> onlineUsers, List<String> notifyUsers) {
        SwingUtilities.invokeLater(() -> {
            userPanel.removeAll();
            Icon dot = createDotIcon(luConnect.RED);
            for (String user : onlineUsers) {
                JButton btn = new JButton(user);
                btn.setHorizontalTextPosition(SwingConstants.LEFT);
                btn.setIconTextGap(8);
                if (notifyUsers.contains(user) && luConnect.isNotificationState()) {
                    btn.setIcon(dot);
                }
                btn.addActionListener(e -> {
                    luConnect.setTargetClient(user);
                    luConnect.showScreen("ChatScreen");
                    luConnect.getClient().startChatWith(user);
                    luConnect.newMessageUsers.remove(user);
                });
                userPanel.add(btn);
            }
            userPanel.revalidate();
            userPanel.repaint();
        });
    }


}
