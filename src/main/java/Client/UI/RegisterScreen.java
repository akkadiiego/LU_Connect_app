package Client.UI;

import Client.API.AuthenticationManager;
import javax.swing.*;
import java.awt.*;

public class RegisterScreen extends JPanel {
    private JTextField usernameField; // input for username
    private JPasswordField passwordField; // input for password
    private JLabel messageLabel; // label for error messages
    private LU_Connect_App luConnectApp; // reference to main app

    public RegisterScreen(LU_Connect_App luConnect){
        luConnectApp = luConnect;
        setLayout(new BorderLayout());
        setBackground(luConnectApp.BACKGROUND_COLOR);

        JLabel title = new JLabel("REGISTER", SwingConstants.CENTER); // screen title
        title.setFont(new Font("Arial", Font.BOLD, 22));
        title.setForeground(luConnectApp.RED);

        usernameField = new JTextField(15); // text field for username
        passwordField = new JPasswordField(15); // text field for password
        usernameField.setFont(new Font("Arial", Font.PLAIN, 16));
        passwordField.setFont(new Font("Arial", Font.PLAIN, 16));
        styleTextField(usernameField);
        styleTextField(passwordField);

        JButton send = new JButton("Register"); // register button
        JButton back = new JButton("Back"); // back button
        styleButton(send);
        styleButton(back);

        messageLabel = new JLabel(" "); // message for invalid input
        messageLabel.setForeground(luConnectApp.RED);

        back.addActionListener(e -> {
            luConnectApp.showScreen("HomeScreen"); // return to home
            clearFields(); // clear input fields
        });

        send.addActionListener(e -> { // validate and send data
            String username = usernameField.getText();
            char[] password = passwordField.getPassword();
            new AuthenticationManager(username, password);
            if (!AuthenticationManager.isValidUsername()) {
                messageLabel.setText("Invalid Username.");
            } else if (!AuthenticationManager.isValidPassword()) {
                messageLabel.setText("Invalid password");
            } else {
                luConnectApp.getClient().sendRegistrationData(username, password);
            }
        });

        JPanel fieldsPanel = new JPanel(new GridBagLayout()); // center panel
        fieldsPanel.setBackground(luConnectApp.SECOND_BACK_COLOR);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        gbc.gridx = 0; gbc.gridy = 0;
        JLabel username = new JLabel("Username: "); // username label
        username.setForeground(luConnectApp.GREY);
        fieldsPanel.add(username, gbc);
        gbc.gridx = 1;
        fieldsPanel.add(usernameField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        JLabel password = new JLabel("Password: "); // password label
        password.setForeground(luConnectApp.GREY);
        fieldsPanel.add(password, gbc);
        gbc.gridx = 1;
        fieldsPanel.add(passwordField, gbc);

        gbc.gridx = 1; gbc.gridy = 2;
        fieldsPanel.add(messageLabel, gbc);
        gbc.gridx = 1; gbc.gridy = 3;
        fieldsPanel.add(send, gbc);

        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT)); // bottom panel
        buttonsPanel.setBackground(luConnectApp.BACKGROUND_COLOR);
        buttonsPanel.add(back);

        add(title, BorderLayout.NORTH);
        add(fieldsPanel, BorderLayout.CENTER);
        add(buttonsPanel, BorderLayout.SOUTH);
    }

    public void clearFields() { // clear input values
        usernameField.setText("");
        passwordField.setText("");
        messageLabel.setText("");
    }

    private void styleButton(JButton button) { // style for buttons
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(luConnectApp.RED);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void styleTextField(JTextField field) { // style for input fields
        field.setBackground(luConnectApp.SECOND_BACK_COLOR);
        field.setForeground(luConnectApp.GREY);
        field.setCaretColor(luConnectApp.GREY);
        field.setBorder(BorderFactory.createLineBorder(luConnectApp.GREY, 2));
    }
}
