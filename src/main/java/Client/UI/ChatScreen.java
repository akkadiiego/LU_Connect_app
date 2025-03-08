package Client.UI;

import Client.API.MessageAdapter;

import javax.swing.*;
import java.awt.*;

public class ChatScreen extends JPanel {
    private JTextArea chatArea;
    private JTextField messageField;
    private JButton sendButton;
    private JLabel title;
    private LU_Connect_App luConnect;

    public ChatScreen(LU_Connect_App luConnectApp) {
        luConnect = luConnectApp;
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new BorderLayout());
        JPanel leftTop = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPanel center = new JPanel(new FlowLayout(FlowLayout.CENTER));

        title = new JLabel("Chat with: ", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        center.add(title);

        JButton back = new JButton("Back");
        leftTop.add(back);
        topPanel.add(center, BorderLayout.CENTER);
        topPanel.add(leftTop, BorderLayout.WEST);


        chatArea = new JTextArea();
        chatArea.setEditable(false);
        JScrollPane chatScrollPane = new JScrollPane(chatArea);



        JPanel floorPanel = new JPanel(new BorderLayout());
        messageField = new JTextField();
        sendButton = new JButton("Send");




        sendButton.addActionListener(e -> sendChatMessage());
        messageField.addActionListener(e -> sendChatMessage());

        back.addActionListener(e -> {
            luConnectApp.showScreen("UserScreen");
            SwingUtilities.invokeLater(() -> luConnectApp.getClient().exitChat());
        });

        floorPanel.add(messageField, BorderLayout.CENTER);
        floorPanel.add(sendButton, BorderLayout.EAST);

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

        SwingUtilities.invokeLater(() -> chatArea.append(formattedMessage + "\n"));

    }

    public void showSentMessage(String message) {
        if (message == null || message.trim().isEmpty()) {
            System.out.println("Empty message not displayed!");
            return;
        }

        SwingUtilities.invokeLater(() -> chatArea.append("You: " + message + "\n"));


    }

    private void sendChatMessage() {
        String message = messageField.getText().trim();
        if (!message.isEmpty()) {
            showSentMessage(message);
            luConnect.getClient().sendMessage(message);
            messageField.setText("");
        }
    }


}
