package Client.UI;

import Client.API.AuthenticationManager;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class RegisterScreen extends JPanel {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JLabel messageLabel;

    public RegisterScreen(LU_Connect_App luConnectApp){
        setLayout(new BorderLayout());

        JLabel title = new JLabel("REGISTER", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));

        usernameField = new JTextField(15);
        passwordField = new JPasswordField(15);

        JLabel usernameRules = new JLabel("<html><i>Username: 4-15 characters, only letters , numbers and _</i></html>");
        JLabel passwordRules = new JLabel("<html><i>Password: 8-15 characters, 1 Uppercase, 1 number, 1 symbol</i></html>");

        JButton send = new JButton("Send");

        JLabel userLabel = new JLabel("Username: ");
        JLabel passLabel = new JLabel("Password: ");

        usernameField.setPreferredSize(new Dimension(200, 30));
        userLabel.setPreferredSize(new Dimension(100, 30));
        passwordField.setPreferredSize(new Dimension(200, 30));
        passLabel.setPreferredSize(new Dimension(100, 30));


        usernameField.setFont(new Font("Arial", Font.PLAIN, 16));
        passwordField.setFont(new Font("Arial", Font.PLAIN, 16));




        JButton back = new JButton("Back");


        messageLabel = new JLabel(" ");
        messageLabel.setForeground(Color.RED);

        back.addActionListener(e -> {
            luConnectApp.showScreen("HomeScreen");
            clearFields();
        });

        send.addActionListener(e -> {
            String username = usernameField.getText();
            char[] password = passwordField.getPassword();

            new AuthenticationManager(username, password);
            if (!AuthenticationManager.isValidUsername()){
                messageLabel.setText("Invalid Username.");


            }
            else if (!AuthenticationManager.isValidPassword()){
                messageLabel.setText("Invalid password");
            }
            else {
                luConnectApp.getClient().sendRegistrationData(username, password);
            }
        });


        JPanel buttonsPanel = new JPanel(new BorderLayout());

        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        leftPanel.add(back);

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        //rightPanel.add(send);

        JPanel fieldsPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.gridx = 0; gbc.gridy = 0;
        fieldsPanel.add(userLabel, gbc);

        gbc.gridx = 1; gbc.gridy = 0;
        fieldsPanel.add(usernameField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        fieldsPanel.add(passLabel, gbc);

        gbc.gridx = 1; gbc.gridy = 1;
        fieldsPanel.add(passwordField, gbc);

        gbc.gridx = 1; gbc.gridy = 2;
        fieldsPanel.add(usernameRules, gbc);

        gbc.gridx = 1; gbc.gridy = 3;
        fieldsPanel.add(passwordRules, gbc);

        gbc.gridx = 1; gbc.gridy = 4;
        fieldsPanel.add(messageLabel, gbc);

        gbc.gridx = 1; gbc.gridy = 5;
        fieldsPanel.add(send, gbc);

        buttonsPanel.add(leftPanel, BorderLayout.WEST);
        buttonsPanel.add(rightPanel, BorderLayout.EAST);

        add(title, BorderLayout.NORTH);
        add(fieldsPanel, BorderLayout.CENTER);
        add(buttonsPanel, BorderLayout.SOUTH);
    }

    public void clearFields() {
        usernameField.setText("");
        passwordField.setText("");
        messageLabel.setText("");
    }
}
