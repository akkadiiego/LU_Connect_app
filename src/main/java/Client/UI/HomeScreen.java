package Client.UI;

import javax.swing.*;
import java.awt.*;

public class HomeScreen extends JPanel {
    public HomeScreen(LU_Connect_App luConnectApp){
        setLayout(new BorderLayout());

        JButton login = new JButton("Login");
        JButton register = new JButton("Register");

        login.addActionListener(e -> luConnectApp.showScreen("Login"));
        register.addActionListener(e -> luConnectApp.showScreen("Register"));

        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        buttonsPanel.add(login);
        buttonsPanel.add(register);

        add(buttonsPanel, BorderLayout.SOUTH);


    }
}
