package Client.UI;

import Client.API.MessageAdapter;
import Common.Models.FileData;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;

public class ChatScreen extends JPanel {
    // UI components for chat interface
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

        // Top bar with title and buttons
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(luConnectApp.BACKGROUND_COLOR);

        JPanel leftTop = new JPanel(new FlowLayout(FlowLayout.LEFT));
        leftTop.setBackground(luConnectApp.BACKGROUND_COLOR);

        JPanel rightTop = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightTop.setBackground(luConnectApp.BACKGROUND_COLOR);

        JPanel center = new JPanel(new FlowLayout(FlowLayout.CENTER));
        center.setBackground(luConnectApp.BACKGROUND_COLOR);

        title = new JLabel("Chat with: ", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        title.setForeground(luConnectApp.GREY);
        center.add(title);

        JButton back = new JButton("Back");
        styleButton(back);
        leftTop.add(back);

        JButton notifications = new JButton("Notications: " + luConnect.currentState());
        styleButton(notifications);
        notifications.addActionListener(e -> {
            luConnect.setNotificationState();
            notifications.setText("Notications: " + luConnect.currentState());
        });

        rightTop.add(notifications);

        topPanel.add(center, BorderLayout.CENTER);
        topPanel.add(leftTop, BorderLayout.WEST);
        topPanel.add(rightTop, BorderLayout.EAST);

        // Message display area
        chatBox = Box.createVerticalBox();
        chatPanel = new JPanel();
        chatPanel.setLayout(new BorderLayout());
        chatPanel.setBackground(luConnectApp.SECOND_BACK_COLOR);
        chatPanel.add(chatBox, BorderLayout.NORTH);

        chatScrollPane = new JScrollPane(chatPanel);
        chatScrollPane.setBorder(null);
        chatScrollPane.getViewport().setBackground(luConnectApp.SECOND_BACK_COLOR);

        // Bottom input area with message field and buttons
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

        // Send message on click or Enter
        sendButton.addActionListener(e -> sendChatMessage());
        messageField.addActionListener(e -> sendChatMessage());

        // Attach file button
        JButton attachButton = new JButton("SEND FILE");
        styleButton(attachButton);
        attachButton.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                File file = chooser.getSelectedFile();
                String[] parts = file.getName().split("\\.");
                String extension = parts[parts.length - 1];

                if (extension.equals("pdf") || extension.equals("jpeg") || extension.equals("jpg") || extension.equals("png") || extension.equals("docx")) {
                    luConnect.getClient().sendFile(file);
                }
                else {
                    JOptionPane.showMessageDialog(null, "Only pdf, docx, jpeg, jpg and png are allowed");
                }
            }
        });

        // Back button returns to user list and clears chat
        back.addActionListener(e -> {
            luConnectApp.showScreen("UserScreen");
            SwingUtilities.invokeLater(() -> luConnectApp.getClient().exitChat());
            clearChat();
        });

        floorPanel.add(messageField, BorderLayout.CENTER);
        floorPanel.add(sendButton, BorderLayout.EAST);
        floorPanel.add(attachButton, BorderLayout.WEST);

        add(topPanel, BorderLayout.NORTH);
        add(chatScrollPane, BorderLayout.CENTER);
        add(floorPanel, BorderLayout.SOUTH);
    }

    // Change chat title when switching users
    public void updateChatTitle(String targetUser) {
        SwingUtilities.invokeLater(() -> title.setText("Chat with: " + targetUser));
    }

    // Receive and show a message
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

    // Receive and show a file download button
    public void receiveFile(String filename, byte[] data){
        if (data == null || data.length == 0) {
            System.out.println("Received an empty file.");
            return;
        }
        addDownloadMessage(filename, data);
    }

    // Adds a button to download the received file
    private void addDownloadMessage(String filename, byte[] data){
        JPanel messagePanel = new JPanel(new BorderLayout());
        messagePanel.setBackground(luConnect.SECOND_BACK_COLOR);

        JButton downloadButton = new JButton(filename);
        styleButton(downloadButton);
        downloadButton.setBackground(luConnect.GREY);
        downloadButton.setForeground(Color.BLACK);

        downloadButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setSelectedFile(new File(filename));
            int choice = fileChooser.showSaveDialog(this);
            if (choice == JFileChooser.APPROVE_OPTION) {
                File selected = fileChooser.getSelectedFile();
                try (FileOutputStream fos = new FileOutputStream(selected)) {
                    fos.write(data);
                    JOptionPane.showMessageDialog(this, "File perfectly saved");
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(this, "Error: file not saved");
                    ex.printStackTrace();
                }
            }
        });

        messagePanel.add(downloadButton, BorderLayout.WEST);

        chatBox.add(messagePanel);
        chatBox.add(Box.createVerticalStrut(10));

        chatPanel.revalidate();
        chatPanel.repaint();

        SwingUtilities.invokeLater(() ->
                chatScrollPane.getVerticalScrollBar().setValue(chatScrollPane.getVerticalScrollBar().getMaximum())
        );
    }

    // Display the message that you sent
    public void showSentMessage(String message) {
        if (message == null || message.trim().isEmpty()) {
            System.out.println("Empty message not displayed!");
            return;
        }

        SwingUtilities.invokeLater(() -> addMessageBubble("You: " + message, true));
    }

    // Handles sending messages from input field
    private void sendChatMessage() {
        String message = messageField.getText().trim();
        if (!message.isEmpty()) {
            showSentMessage(message);
            luConnect.getClient().sendMessage(message);
            messageField.setText("");
        }
    }

    // Adds a chat bubble to the screen
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

    // Applies styling to buttons
    private void styleButton(JButton button) {
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(luConnect.RED);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    // Clears all messages from the chat
    public void clearChat() {
        chatBox.removeAll();
        chatPanel.revalidate();
        chatPanel.repaint();
    }

}
