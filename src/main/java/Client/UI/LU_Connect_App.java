package Client.UI;

import Client.Client;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LU_Connect_App  extends JFrame{
    private CardLayout cardLayout;
    private JPanel cardPanel;
    private Client myClient;
    private List<String> otherOnClients;
    private static final int initialScreenWidth = 600;
    private static final int initialScreenHeight = 400;
    private String currentScreen;
    private String targetClient;

    public LU_Connect_App(Client client){
        myClient = client;
        otherOnClients = null;
        targetClient = null;

        setTitle("LUConnect");
        setSize(initialScreenWidth, initialScreenHeight);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        currentScreen = "HomeScreen";
        cardPanel.add(new HomeScreen(this), "HomeScreen");
        cardPanel.add(new LoginScreen(this), "Login");
        cardPanel.add(new RegisterScreen(this), "Register");
        cardPanel.add(new UserScreen(this), "UserScreen");
        cardPanel.add(new UserScreen(this), "ChatScreen");

        add(cardPanel);
        setVisible(true);
    }

    public void showScreen(String screen){
        cardLayout.show(cardPanel, screen);
        currentScreen = screen;

        if (screen.equals("UserScreen")) {
            SwingUtilities.invokeLater(() -> {
                myClient.startUpdatingOnlineClients();
                ((UserScreen) cardPanel.getComponent(3)).updateUserList(otherOnClients);
            });
        }
    }


    public Client getClient() {return myClient;}


    public void loginMessage(String serverMessage) {
        if (serverMessage.equals("You logged successfully")){
            showScreen("UserScreen");
        }

        JOptionPane.showMessageDialog(null, serverMessage);


    }

    public void getOnlineClients(List<String> clients){
        List<String> otherClients = new ArrayList<>();

        for (String client : clients) {
            if (!client.equals(myClient.getUser().getUsername())) {
                otherClients.add(client);
            }
        }
        otherOnClients = otherClients;

        if (currentScreen.equals("UserScreen")) {
            SwingUtilities.invokeLater(() -> ((UserScreen) cardPanel.getComponent(3)).updateUserList(otherClients));
        }
    }


    public void logOut() throws IOException {
        JOptionPane.showMessageDialog(null, "You logged out.");
        myClient.logOut();
        SwingUtilities.invokeLater(() -> System.exit(0));
    }
}
