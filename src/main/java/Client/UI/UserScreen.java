package Client.UI;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class UserScreen extends JPanel {
    LU_Connect_App luConnect; // reference to the main app
    private JPanel userPanel; // panel that will contain user buttons

    public UserScreen(LU_Connect_App luConnectApp){
        luConnect = luConnectApp;
        setLayout(new BorderLayout());
        setBackground(luConnectApp.BACKGROUND_COLOR);

        JPanel topPanel = new JPanel(new BorderLayout()); // top bar
        JLabel title = new JLabel("Users Online", SwingConstants.CENTER); // screen title
        title.setForeground(luConnectApp.GREY);
        title.setFont(new Font("Arial", Font.BOLD, 22));

        JButton notifications = new JButton("Notications: " + luConnectApp.currentState()); // toggle notifications
        styleButton(notifications);
        notifications.addActionListener(e -> {
            luConnect.setNotificationState();
            notifications.setText("Notications: " + luConnectApp.currentState());
        });

        topPanel.setBackground(luConnectApp.BACKGROUND_COLOR);
        topPanel.add(title, BorderLayout.CENTER);
        topPanel.add(notifications, BorderLayout.EAST);

        JButton logOut = new JButton("Log out"); // logout button
        styleButton(logOut);
        logOut.addActionListener(e -> {
            luConnect.logOut();
        });

        userPanel = new JPanel(new GridLayout(3, 1)); // panel that lists the users
        userPanel.setBackground(luConnectApp.SECOND_BACK_COLOR);

        add(topPanel, BorderLayout.NORTH);
        add(new JScrollPane(userPanel), BorderLayout.CENTER);
        add(logOut, BorderLayout.SOUTH);
    }

    private void styleButton(JButton button) { // common button styling
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(luConnect.RED);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private Icon createDotIcon(Color color) { // small colored dot icon
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
            userPanel.removeAll(); // clear previous list
            Icon dot = createDotIcon(luConnect.RED); // red dot icon for new messages
            for (String user : onlineUsers) {
                JButton btn = new JButton(user); // create button with username
                btn.setHorizontalTextPosition(SwingConstants.LEFT);
                btn.setIconTextGap(8);
                if (notifyUsers.contains(user) && luConnect.isNotificationState()) {
                    btn.setIcon(dot); // add dot if there's a new message
                }
                btn.addActionListener(e -> {
                    luConnect.setTargetClient(user);
                    luConnect.showScreen("ChatScreen");
                    luConnect.getClient().startChatWith(user);
                    luConnect.newMessageUsers.remove(user);
                });
                userPanel.add(btn);
            }
            userPanel.revalidate(); // refresh panel
            userPanel.repaint();
        });
    }

}
