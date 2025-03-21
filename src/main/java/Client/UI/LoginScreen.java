package Client.UI;

import Client.API.AuthenticationManager;
import javax.swing.*;
import java.awt.*;

public class LoginScreen extends JPanel {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JLabel messageLabel;
    private LU_Connect_App luConnectApp;

    public LoginScreen(LU_Connect_App luConnect){
        luConnectApp = luConnect;
        setLayout(new BorderLayout());
        setBackground(luConnectApp.BACKGROUND_COLOR);

        JLabel title = new JLabel("LOGIN", SwingConstants.CENTER); // screen title
        title.setFont(new Font("Arial", Font.BOLD, 22));
        title.setForeground(luConnectApp.RED);

        usernameField = new JTextField(15); // input for username
        passwordField = new JPasswordField(15); // input for password
        usernameField.setFont(new Font("Arial", Font.PLAIN, 16));
        passwordField.setFont(new Font("Arial", Font.PLAIN, 16));
        styleTextField(usernameField);
        styleTextField(passwordField);

        JButton send = new JButton("Login"); // login button
        JButton back = new JButton("Back"); // back button
        styleButton(send);
        styleButton(back);

        messageLabel = new JLabel(" "); // label to show error messages
        messageLabel.setForeground(luConnectApp.RED);

        back.addActionListener(e -> {
            luConnectApp.showScreen("HomeScreen"); // return to home screen
            clearFields(); // reset fields
        });

        send.addActionListener(e -> {
            String username = usernameField.getText();
            char[] password = passwordField.getPassword();
            new AuthenticationManager(username, password);
            if (!AuthenticationManager.isValidUsername()) {
                messageLabel.setText("Invalid Username.");
            } else if (!AuthenticationManager.isValidPassword()) {
                messageLabel.setText("Invalid password");
            } else {
                luConnectApp.getClient().sendLoginData(username, password); // send login data
            }
        });

        usernameField.addActionListener(e -> {
            String username = usernameField.getText();
            char[] password = passwordField.getPassword();
            new AuthenticationManager(username, password);
            if (!AuthenticationManager.isValidUsername()) {
                messageLabel.setText("Invalid Username.");
            } else if (!AuthenticationManager.isValidPassword()) {
                messageLabel.setText("Invalid password");
            } else {
                luConnectApp.getClient().sendLoginData(username, password);
            }
        });

        passwordField.addActionListener(e -> {
            String username = usernameField.getText();
            char[] password = passwordField.getPassword();
            new AuthenticationManager(username, password);
            if (!AuthenticationManager.isValidUsername()) {
                messageLabel.setText("Invalid Username.");
            } else if (!AuthenticationManager.isValidPassword()) {
                messageLabel.setText("Invalid password");
            } else {
                luConnectApp.getClient().sendLoginData(username, password);
            }
        });

        JPanel fieldsPanel = new JPanel(new GridBagLayout()); // panel to hold form fields
        fieldsPanel.setBackground(luConnectApp.SECOND_BACK_COLOR);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel username = new JLabel("Username: "); // label for username
        username.setForeground(luConnectApp.GREY);
        fieldsPanel.add(username, gbc);
        gbc.gridx = 1;
        fieldsPanel.add(usernameField, gbc);
        gbc.gridx = 0; gbc.gridy = 1;
        JLabel password = new JLabel("Password: "); // label for password
        password.setForeground(luConnectApp.GREY);
        fieldsPanel.add(password, gbc);
        gbc.gridx = 1;
        fieldsPanel.add(passwordField, gbc);
        gbc.gridx = 1; gbc.gridy = 2;
        fieldsPanel.add(messageLabel, gbc);
        gbc.gridx = 1; gbc.gridy = 3;
        fieldsPanel.add(send, gbc);

        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT)); // bottom panel for back button
        buttonsPanel.setBackground(luConnectApp.BACKGROUND_COLOR);
        buttonsPanel.add(back);

        add(title, BorderLayout.NORTH);
        add(fieldsPanel, BorderLayout.CENTER);
        add(buttonsPanel, BorderLayout.SOUTH);
    }

    public void clearFields() {
        usernameField.setText("");
        passwordField.setText("");
        messageLabel.setText("");
    }

    private void styleButton(JButton button) {
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(luConnectApp.RED);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void styleTextField(JTextField field) {
        field.setBackground(luConnectApp.SECOND_BACK_COLOR);
        field.setForeground(luConnectApp.GREY);
        field.setCaretColor(luConnectApp.GREY);
        field.setBorder(BorderFactory.createLineBorder(luConnectApp.GREY, 2));
    }
}
