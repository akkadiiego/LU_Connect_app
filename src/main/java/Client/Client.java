package Client;

import Client.UI.LU_Connect_App;
import Common.Models.User;

import javax.swing.*;

import static Common.Utils.Config.SERVER_PORT;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Client implements Runnable{
    private Socket socket;
    private Scanner in;
    private PrintWriter writer;
    private User user;
    private LU_Connect_App luConnectUI;

    public Client(){
        try{
            socket = new Socket("localhost", SERVER_PORT);
            in = new Scanner(socket.getInputStream());
            user = null;
            writer = new PrintWriter(socket.getOutputStream(), true);

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
                String serverMessage = in.nextLine();
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
                    String message = serverMessage.substring(17).trim();
                    SwingUtilities.invokeLater(() -> luConnectUI.getMessage(message));
                }

                System.out.println("Server: " + serverMessage);
            }


            /*if (luConnectUI != null) {
                SwingUtilities.invokeLater(() -> luConnectUI.updateText(serverMessage));
            }*/
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
            return true;
        }
        return false;
    }

    private void onlineClients() {
        if (writer != null) {
            writer.println("CLIENTS");
            writer.flush();
        }
    }

    public void startUpdatingOnlineClients() {
        new Thread(() -> {
            while (true) {
                onlineClients();
                try {
                    Thread.sleep(5000);
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

    public boolean sendMessage(String message){
        if (writer != null) {
            writer.println("SEND TEXT");
            writer.println(message);
            writer.flush();
            return true;
        }
        return false;
    }


    /*public static void main(String[] args) {
        new Client();
    }/+*/
}
