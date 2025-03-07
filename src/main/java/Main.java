import Client.Client;
import Client.UI.LU_Connect_App;
import Server.Server;

import javax.swing.*;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        new Thread(() -> new Server()).start();
        Client client = new Client();

        // Iniciar la UI en el EDT y pasarle la referencia del cliente
        SwingUtilities.invokeLater(() -> {
            LU_Connect_App ui = new LU_Connect_App(client);
            client.setUI(ui); // Opción para que el cliente también tenga referencia de la UI
        });

        // Iniciar el cliente en un hilo separado
        new Thread(client).start();
    }
}