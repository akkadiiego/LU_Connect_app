package Client;

import Client.UI.ChatScreen;
import Client.UI.LU_Connect_App;
import Common.Models.User;

import javax.swing.*;

import static Common.Utils.Config.SERVER_PORT;

import java.io.*;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Scanner;

public class Client implements Runnable{
    private Socket socket; // client socket
    private Scanner in; // to read data from server
    private PrintWriter writer; // to send data to server
    private User user; // current user
    private LU_Connect_App luConnectUI; // reference to UI

    public Client(){
        try{
            socket = new Socket("localhost", SERVER_PORT); // connect to server
            in = new Scanner(socket.getInputStream()); // read from server
            user = null;
            writer = new PrintWriter(socket.getOutputStream(), true); // write to server

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public User getUser(){ return this.user; }
    public void setUser(User user) { this.user = user; }

    public void setUI(LU_Connect_App luConnectUI) {
        this.luConnectUI = luConnectUI;
    }

    private void close() throws IOException {
        socket.close();
        in.close();
    }

    @Override
    public void run() {
        while (true){
            if (in.hasNextLine()) {
                String serverMessage = in.nextLine(); // get server response

                if (serverMessage.equals("LOGGED")) {
                    SwingUtilities.invokeLater(() -> luConnectUI.loginMessage("You logged successfully"));
                } else if (serverMessage.equals("NOT LOGGED")) {
                    user = null;
                    SwingUtilities.invokeLater(() -> luConnectUI.loginMessage("User does not exist or it is already online"));
                } else if (serverMessage.equals("LOGGED OUT")) {
                    user = null;
                } else if (serverMessage.equals("REGISTERED")) {
                    SwingUtilities.invokeLater(() -> luConnectUI.loginMessage("User Registered, now you can Log in"));
                } else if (serverMessage.equals("NOT REGISTERED")) {
                    SwingUtilities.invokeLater(() -> luConnectUI.loginMessage("User already exist"));
                } else if (serverMessage.contains("ONLINE CLIENTS:")) {
                    List<String> items = Arrays.asList(serverMessage.split(":"));
                    if (items.size() > 1) {
                        List<String> clients = List.of(items.get(1).split(","));
                        SwingUtilities.invokeLater(() -> luConnectUI.getOnlineClients(clients));
                    }
                } else if (serverMessage.startsWith("RECEIVED MESSAGE:")) {
                    String targetClient = luConnectUI.getTargetClient();
                    String message = serverMessage.substring(17).trim();
                    String sender = message.split("/")[1].split("->")[0].split(" ")[0];

                    // only show message if chat is open with that user
                    if (luConnectUI.currentScreen.equals("ChatScreen") && sender.equals(targetClient)) {
                        SwingUtilities.invokeLater(() -> luConnectUI.getMessage(message));
                        messageReceived();
                    }

                } else if (serverMessage.startsWith("RECEIVED FILE:")) {
                    String targetClient = luConnectUI.getTargetClient();
                    String message = serverMessage.substring(14).trim();
                    String sender = message.split(" -> ")[0].split(" ")[0];
                    String filename = message.split(" -> ")[1].split(" of ")[0];
                    byte[] data = Base64.getDecoder().decode(serverMessage.split(" bytes //// ")[1].replaceAll("[^A-Za-z0-9+/=]", ""));  https://stackoverflow.com/questions/8571501/how-to-check-whether-a-string-is-base64-encoded-or-not

                    if (luConnectUI.currentScreen.equals("ChatScreen") && sender.equals(targetClient)) {
                        SwingUtilities.invokeLater(() -> luConnectUI.getFile(filename, data));
                        messageReceived();
                    }
                } else if (serverMessage.startsWith("NOTIFY:")) {
                    String sender = serverMessage.substring(7).trim();
                    SwingUtilities.invokeLater(() -> luConnectUI.playNotificationSound());
                    if (luConnectUI.currentScreen.equals("UserScreen")) {
                        SwingUtilities.invokeLater(() -> luConnectUI.notifyUser(sender));
                    }
                }

                System.out.println("Server: " + serverMessage);
            }
        }
    }

    public boolean sendLoginData(String username, char[] password){
        if (writer != null) {
            writer.println("LOGIN");
            writer.println(username);
            writer.println(new String(password));
            writer.flush();

            user = new User(username, new String(password), false);
            return true;
        }
        return false;
    }

    public boolean sendRegistrationData(String username, char[] password){
        if (writer != null) {
            writer.println("REGISTER");
            writer.println(username);
            writer.println(new String(password));
            writer.flush();
            return true;
        }
        return false;
    }

    public boolean logOut() throws IOException {
        if (writer != null) {
            writer.println("END");
            writer.flush();

            socket.close();
            return true;
        }
        return false;
    }

    private void onlineClients() { // request list of online users
        if (writer != null) {
            writer.println("CLIENTS");
            writer.flush();
        }
    }

    public void startUpdatingOnlineClients() { // continuously update list
        new Thread(() -> {
            while (true) {
                onlineClients();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public boolean startChatWith(String user) {
        if (writer != null) {
            writer.println("ENTER CHAT");
            writer.println(user);
            writer.flush();
            return true;
        }
        return false;
    }

    public boolean exitChat(){
        if (writer != null) {
            writer.println("EXIT CHAT");
            writer.flush();
            return true;
        }
        return false;
    }

    public boolean sendMessage(String message){ // send a text message
        if (writer != null) {
            writer.println("SEND TEXT");
            writer.println(message);
            writer.flush();
            return true;
        }
        return false;
    }

    public boolean sendFile(File file) { // send a file to server
        try {
            writer.println("SEND FILE");

            FileInputStream fis = new FileInputStream(file);
            InputStream is = new ByteArrayInputStream(fis.readAllBytes());
            int fileSize = is.available();
            byte[] data = new byte[fileSize];
            is.read(data, 0, fileSize);
            String encodedFile = Base64.getEncoder().encodeToString(data);

            writer.println(file.getName());
            writer.println(encodedFile);
            writer.flush();
            is.close();
            fis.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean messageReceived(){ // notify server message was received
        if (writer != null) {
            writer.println("POP");
            writer.flush();
            return true;
        }
        return false;
    }
}