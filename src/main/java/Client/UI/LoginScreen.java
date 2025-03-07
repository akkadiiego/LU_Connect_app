package Client.UI;

import Client.API.AuthenticationManager;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class LoginScreen extends JPanel {

    public LoginScreen(LU_Connect_App luConnectApp){
        setLayout(new BorderLayout());

        JLabel title = new JLabel("LOGIN", SwingConstants.CENTER);
        JTextField usernameField = new JTextField();
        JPasswordField passwordField = new JPasswordField();
        JLabel userLabel = new JLabel("Username: ");
        JLabel passLabel = new JLabel("Password: ");

        usernameField.setPreferredSize(new Dimension(200, 30));
        userLabel.setPreferredSize(new Dimension(100, 30));
        passwordField.setPreferredSize(new Dimension(200, 30));
        passLabel.setPreferredSize(new Dimension(100, 30));


        usernameField.setFont(new Font("Arial", Font.PLAIN, 16));
        passwordField.setFont(new Font("Arial", Font.PLAIN, 16));


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

        JButton logOut = new JButton("Log Out");
        JButton send = new JButton("Send");

        logOut.addActionListener(e -> {
            try {
                luConnectApp.logOut();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        send.addActionListener(e -> {
            String username = usernameField.getText();
            char[] password = passwordField.getPassword();
            new AuthenticationManager(username, password);
            if (AuthenticationManager.isValidUsername() && AuthenticationManager.isValidPassword()){

                luConnectApp.getClient().sendLoginData(username, password);

            }
            else {
                JOptionPane.showMessageDialog(null, "Wrong Input");
            }
        });


        JPanel buttonsPanel = new JPanel(new BorderLayout());

        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        leftPanel.add(logOut);

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.add(send);

        buttonsPanel.add(leftPanel, BorderLayout.WEST);
        buttonsPanel.add(rightPanel, BorderLayout.EAST);

        add(title, BorderLayout.NORTH);
        add(fieldsPanel, BorderLayout.CENTER);
        add(buttonsPanel, BorderLayout.SOUTH);
    }
}
