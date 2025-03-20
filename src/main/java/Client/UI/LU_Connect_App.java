package Client.UI;

import Client.Client;

import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class LU_Connect_App  extends JFrame{
    public static final Color RED = new Color(153, 0, 0);
    public static final Color BACKGROUND_COLOR = new Color(50, 50, 50);
    public static final Color SECOND_BACK_COLOR = new Color(30, 30, 30);
    public static final Color GREY = new Color(204, 204, 204);
    private CardLayout cardLayout;
    private JPanel cardPanel;
    private Client myClient;
    private List<String> otherOnClients;
    private static final int initialScreenWidth = 600;
    private static final int initialScreenHeight = 400;
    public String currentScreen;
    private String targetClient;
    private boolean notificationState;
    public ArrayList<String> newMessageUsers;
    private int lastMessageReceived;

    public LU_Connect_App(Client client){
        myClient = client;
        otherOnClients = null;
        targetClient = null;
        notificationState = true;
        newMessageUsers = new ArrayList<>();
        lastMessageReceived = 0;

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
        cardPanel.add(new ChatScreen(this), "ChatScreen");

        add(cardPanel);
        setVisible(true);
    }

    public void showScreen(String screen){
        cardLayout.show(cardPanel, screen);
        currentScreen = screen;

        if (screen.equals("UserScreen")) {
            SwingUtilities.invokeLater(() -> {
                myClient.startUpdatingOnlineClients();
                ((UserScreen) cardPanel.getComponent(3)).updateUserList(otherOnClients, new ArrayList<String>());
            });
        } else if (screen.equals("ChatScreen")) {
            SwingUtilities.invokeLater(() -> ((ChatScreen) cardPanel.getComponent(4)).updateChatTitle(targetClient));
        }
    }



    public Client getClient() {return myClient;}

    public void setTargetClient(String targetClient) {this.targetClient = targetClient;}

    public String getTargetClient() {return targetClient;}

    public void setNotificationState() {this.notificationState = !isNotificationState();}

    public boolean isNotificationState() {return notificationState;}

    public int getLastMessageReceived() {return lastMessageReceived;}

    public void setLastMessageReceived(int lastMessageReceived) {this.lastMessageReceived = lastMessageReceived;}

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
            SwingUtilities.invokeLater(() -> ((UserScreen) cardPanel.getComponent(3)).updateUserList(otherClients, newMessageUsers));
        }
    }

    public void getMessage(String message){
        SwingUtilities.invokeLater(() -> ((ChatScreen) cardPanel.getComponent(4)).receiveMessage(message));
    }

    public void notifyUser(String user){
        if (!newMessageUsers.contains(user)){
            newMessageUsers.add(user);
        }
        else if (currentScreen.equals("UserScreen")) {
            SwingUtilities.invokeLater(() -> ((UserScreen) cardPanel.getComponent(3)).updateUserList(otherOnClients, newMessageUsers));
        }
    }

    public void playNotificationSound() {
        if (!notificationState){
            return;
        }
        try (AudioInputStream audio = AudioSystem.getAudioInputStream(getClass().getResource("/sounds/Notification.wav"))) {
            Clip clip = AudioSystem.getClip();
            clip.open(audio);
            clip.start();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            Toolkit.getDefaultToolkit().beep();
        }
    }

    public String currentState(){
        if (isNotificationState()) {
            return "On";
        }
        return "Off";
    }


    public void logOut() {
        try {
            myClient.logOut();
            SwingUtilities.invokeLater(() -> {
                this.dispose();
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
