
import Client.Client;
import Client.UI.LU_Connect_App;
import Server.Server;

import javax.swing.*;
import java.io.IOException;
import java.net.ServerSocket;

import static Common.Utils.Config.SERVER_PORT;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        if (!isServerRunning()) {
            new Thread(() -> new Server()).start();

        }

        Client client = new Client();
        SwingUtilities.invokeLater(() -> {
            LU_Connect_App ui = new LU_Connect_App(client);
            client.setUI(ui);
        });


        new Thread(client).start();
    }

    private static boolean isServerRunning() {
        try (ServerSocket socket = new ServerSocket(SERVER_PORT)) {
            return false;
        } catch (IOException ignore) {
            return true;
        }
    }
}