package Client.UI;

import Client.API.MessageAdapter;

import javax.swing.*;
import java.awt.*;

public class ChatScreen extends JPanel {
    private JTextArea chatArea;
    private JTextField messageField;
    private JButton sendButton;

    public ChatScreen(LU_Connect_App luConnectApp) {
        setLayout(new BorderLayout());


        JLabel title = new JLabel("Chat with: " + luConnectApp.getTargetClient(), SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        add(title, BorderLayout.NORTH);


        chatArea = new JTextArea();
        chatArea.setEditable(false);
        JScrollPane chatScrollPane = new JScrollPane(chatArea);
        add(chatScrollPane, BorderLayout.CENTER);


        JPanel floorPanel = new JPanel(new BorderLayout());
        messageField = new JTextField();
        sendButton = new JButton("Send");
        JButton back = new JButton("Back");


        sendButton.addActionListener(e -> sendMessage());
        messageField.addActionListener(e -> sendMessage());

        back.addActionListener(e -> luConnectApp.showScreen("UserScreen"));

        floorPanel.add(messageField, BorderLayout.CENTER);
        floorPanel.add(sendButton, BorderLayout.EAST);

        add(floorPanel, BorderLayout.SOUTH);
    }


    private void sendMessage() {
        String message = messageField.getText().trim();
        if (!message.isEmpty()) {
            chatArea.append("You: " + message + "\n");
            messageField.setText("");
        }
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

}
