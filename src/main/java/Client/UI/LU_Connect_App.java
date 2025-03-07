package Client.UI;

import Client.Client;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class LU_Connect_App  extends JFrame{
    private CardLayout cardLayout;
    private JPanel cardPanel;
    private Client myClient;

    public LU_Connect_App(Client client){
        myClient = client;

        setTitle("LUConnect");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        cardPanel.add(new HomeScreen(this), "HomeScreen");
        cardPanel.add(new LoginScreen(this), "Login");
        cardPanel.add(new RegisterScreen(this), "Register");

        add(cardPanel);
        setVisible(true);
    }

    public void showScreen(String screen){
        cardLayout.show(cardPanel, screen);
    }

    public Client getClient() {return myClient;}

    public void updateText(String serverMessage) {

    }

    public void loginMessage(String serverMessage) {
        JOptionPane.showMessageDialog(null, serverMessage);
    }

    public void logOut() throws IOException {
        JOptionPane.showMessageDialog(null, "You logged out.");
        myClient.logOut();
        SwingUtilities.invokeLater(() -> System.exit(0));
    }
}
