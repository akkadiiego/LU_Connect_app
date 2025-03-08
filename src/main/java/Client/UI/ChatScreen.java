package Client.UI;

import Client.API.MessageAdapter;

import javax.swing.*;
import java.awt.*;

public class ChatScreen extends JPanel {
    private JPanel chatPanel;
    private JTextField messageField;
    private JButton sendButton;
    private JLabel title;
    private LU_Connect_App luConnect;
    private JScrollPane chatScrollPane;
    private Box chatBox;

    public ChatScreen(LU_Connect_App luConnectApp) {
        luConnect = luConnectApp;
        setLayout(new BorderLayout());
        setBackground(luConnectApp.BACKGROUND_COLOR);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(luConnectApp.BACKGROUND_COLOR);

        JPanel leftTop = new JPanel(new FlowLayout(FlowLayout.LEFT));
        leftTop.setBackground(luConnectApp.BACKGROUND_COLOR);

        JPanel center = new JPanel(new FlowLayout(FlowLayout.CENTER));
        center.setBackground(luConnectApp.BACKGROUND_COLOR);

        title = new JLabel("Chat with: ", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        title.setForeground(luConnectApp.GREY);
        center.add(title);

        JButton back = new JButton("Back");
        styleButton(back);
        leftTop.add(back);

        topPanel.add(center, BorderLayout.CENTER);
        topPanel.add(leftTop, BorderLayout.WEST);

        chatBox = Box.createVerticalBox();
        chatPanel = new JPanel();
        chatPanel.setLayout(new BorderLayout());
        chatPanel.setBackground(luConnectApp.SECOND_BACK_COLOR);
        chatPanel.add(chatBox, BorderLayout.NORTH);

        chatScrollPane = new JScrollPane(chatPanel);
        chatScrollPane.setBorder(null);
        chatScrollPane.getViewport().setBackground(luConnectApp.SECOND_BACK_COLOR);


        JPanel floorPanel = new JPanel(new BorderLayout());
        floorPanel.setBackground(luConnectApp.BACKGROUND_COLOR);

        messageField = new JTextField();
        messageField.setFont(new Font("Arial", Font.PLAIN, 14));
        messageField.setBackground(luConnectApp.SECOND_BACK_COLOR);
        messageField.setForeground(luConnectApp.GREY);
        messageField.setCaretColor(luConnectApp.GREY);
        messageField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(luConnectApp.GREY, 2),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));

        sendButton = new JButton("Send");
        styleButton(sendButton);

        sendButton.addActionListener(e -> sendChatMessage());
        messageField.addActionListener(e -> sendChatMessage());

        back.addActionListener(e -> {
            luConnectApp.showScreen("UserScreen");
            SwingUtilities.invokeLater(() -> luConnectApp.getClient().exitChat());
        });

        floorPanel.add(messageField, BorderLayout.CENTER);
        floorPanel.add(sendButton, BorderLayout.EAST);

        // 🔹 ADD COMPONENTS TO PANEL
        add(topPanel, BorderLayout.NORTH);
        add(chatScrollPane, BorderLayout.CENTER);
        add(floorPanel, BorderLayout.SOUTH);
    }

    public void updateChatTitle(String targetUser) {
        SwingUtilities.invokeLater(() -> title.setText("Chat with: " + targetUser));
    }

    public void receiveMessage(String unformattedMessage) {
        if (unformattedMessage == null || unformattedMessage.trim().isEmpty()) {
            System.out.println("Received an empty message!");
            return;
        }

        String formattedMessage = new MessageAdapter(unformattedMessage).formatMessage();
        if (formattedMessage == null || formattedMessage.trim().isEmpty()) {
            System.out.println("Message formatting failed!");
            return;
        }

        SwingUtilities.invokeLater(() -> addMessageBubble(formattedMessage, false));
    }

    public void showSentMessage(String message) {
        if (message == null || message.trim().isEmpty()) {
            System.out.println("Empty message not displayed!");
            return;
        }

        SwingUtilities.invokeLater(() -> addMessageBubble("You: " + message, true));
    }

    private void sendChatMessage() {
        String message = messageField.getText().trim();
        if (!message.isEmpty()) {
            showSentMessage(message);
            luConnect.getClient().sendMessage(message);
            messageField.setText("");
        }
    }

    private void addMessageBubble(String message, boolean isSent) {
        JPanel messagePanel = new JPanel(new BorderLayout());
        messagePanel.setBackground(luConnect.SECOND_BACK_COLOR);

        JLabel messageLabel = new JLabel("<html><p style='width: 200px;'>" + message + "</p></html>");
        messageLabel.setOpaque(true);
        messageLabel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        messageLabel.setFont(new Font("Arial", Font.PLAIN, 14));

        if (isSent) {
            messageLabel.setBackground(luConnect.RED);
            messageLabel.setForeground(Color.WHITE);
            messagePanel.add(messageLabel, BorderLayout.EAST);
        } else {
            messageLabel.setBackground(luConnect.GREY);
            messageLabel.setForeground(Color.BLACK);
            messagePanel.add(messageLabel, BorderLayout.WEST);
        }

        chatBox.add(messagePanel);
        chatBox.add(Box.createVerticalStrut(10));

        chatPanel.revalidate();
        chatPanel.repaint();

        SwingUtilities.invokeLater(() -> chatScrollPane.getVerticalScrollBar().setValue(chatScrollPane.getVerticalScrollBar().getMaximum()));
    }

    private void styleButton(JButton button) {
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(luConnect.RED);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
}
