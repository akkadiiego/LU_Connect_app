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

/*In order to create an Interface a follow a course of swing, since is very simple and is among the java standard libraries
    here is the free course that I used to learn the basics --> https://www.youtube.com/playlist?list=PLAzlSdU-KYwWtfcYGSWCKD9Hf1nuhBdrQ*/

public class LU_Connect_App  extends JFrame{
    public static final Color RED = new Color(153, 0, 0); // main red color used
    public static final Color BACKGROUND_COLOR = new Color(50, 50, 50); // background color
    public static final Color SECOND_BACK_COLOR = new Color(30, 30, 30); // darker background
    public static final Color GREY = new Color(204, 204, 204); // light grey text
    private CardLayout cardLayout; // layout to switch between screens
    private JPanel cardPanel; // panel that contains all screens
    private Client myClient; // client object handling connection
    private List<String> otherOnClients; // list of other online clients
    private static final int initialScreenWidth = 600;
    private static final int initialScreenHeight = 400;
    public String currentScreen; // name of the current screen
    private String targetClient; // the user we are currently chatting with
    private boolean notificationState; // true if notification sound is on
    public ArrayList<String> newMessageUsers; // list of users that sent new messages
    private int lastMessageReceived; // id of the last message received

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

    public void showScreen(String screen){ // switch to another screen
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

    public Client getClient() {return myClient;} // return the client object

    public void setTargetClient(String targetClient) {this.targetClient = targetClient;} // set current chat target

    public String getTargetClient() {return targetClient;} // get current chat target

    public void setNotificationState() {this.notificationState = !isNotificationState();} // toggle notifications

    public boolean isNotificationState() {return notificationState;} // get notification state

    public int getLastMessageReceived() {return lastMessageReceived;} // get last message id

    public void setLastMessageReceived(int lastMessageReceived) {this.lastMessageReceived = lastMessageReceived;} // set last message id

    public void loginMessage(String serverMessage) { // show login message
        if (serverMessage.equals("You logged successfully")){
            showScreen("UserScreen");
        }

        JOptionPane.showMessageDialog(null, serverMessage);
    }

    public void getOnlineClients(List<String> clients){ // update online clients list
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

    public void getMessage(String message){ // pass received message to ChatScreen
        SwingUtilities.invokeLater(() -> ((ChatScreen) cardPanel.getComponent(4)).receiveMessage(message));
    }

    public void getFile(String filename, byte[] data){ // pass received file to ChatScreen
        SwingUtilities.invokeLater(() -> ((ChatScreen) cardPanel.getComponent(4)).receiveFile(filename, data));
    }

    public void notifyUser(String user){ // add user to new message list
        if (!newMessageUsers.contains(user)){
            newMessageUsers.add(user);
        }
        else if (currentScreen.equals("UserScreen")) {
            SwingUtilities.invokeLater(() -> ((UserScreen) cardPanel.getComponent(3)).updateUserList(otherOnClients, newMessageUsers));
        }
    }

    public void playNotificationSound() { // play sound if enabled
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

    public String currentState(){ // return text for current notification state
        if (isNotificationState()) {
            return "On";
        }
        return "Off";
    }

    public void logOut() { // log out and close window
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
