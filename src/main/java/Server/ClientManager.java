package Server;

import Common.Models.FileData;
import Common.Models.Message;
import Common.Models.User;
import Common.Models.TextMessage;
import Server.BusinessLogic.*;
import Server.DataAccess.DatabaseHandler;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ClientManager extends Thread {
    private Socket clientSocket;
    private Scanner in;
    public PrintWriter out;
    private static DatabaseHandler databaseHandler;
    private AuthenticationService authenticationService;
    private MessageService messageService;
    private FileService fileService;
    private NotificationService notificationService;
    private SecurityModule securityModule;
    public static final Lock lock = new ReentrantLock();
    private User user;
    private ClientManager targetClient;
    private Thread messageReceiver;

    public ClientManager(Socket clientSocket) throws IOException {
        this.clientSocket = clientSocket;
        this.user = null;
        this.in = new Scanner(clientSocket.getInputStream());
        this.out = new PrintWriter(clientSocket.getOutputStream(), true);
    }

    @Override
    public void run() {
        System.out.println("Connected: " + Integer.parseInt(Thread.currentThread().getName().split("-")[3]));

        while (in.hasNextLine()) {
            switch (in.nextLine()) {
                case "END":
                    try {
                        endClientConnection();
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                    out.println("LOGGED OUT");
                    if (Server.getClients().isEmpty()){
                        System.exit(0);
                    }
                    break;

                case "REGISTER":
                    try {
                        registerUser();
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                    break;

                case "LOGIN":
                    if (user != null) {
                        break;
                    }
                    try {
                        Login();
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                    break;

                case "CLIENTS":
                    StringBuilder onClients = new StringBuilder();
                    for (String client : Server.getClients()) {
                        if (client != null) {
                            onClients.append(client).append(",");
                        }
                    }
                    if (onClients.length() > 2) {
                        onClients.delete(onClients.length() - 1, onClients.length());
                    }
                    out.println("ONLINE CLIENTS:" + onClients);
                    break;


                case "ENTER CHAT":

                    targetClient = null;
                    if (in.hasNextLine()) {

                        targetClient = Server.getClient(in.nextLine());
                        //out.println(targetClient.user);
                        //out.println("en el chat de:" + targetClient);
                        if (targetClient == null) {
                            out.println("USER NOT FOUND");
                            break;
                        }

                        messageReceiver = getThread();
                    }
                    break;

                case "SEND TEXT":
                    if (in.hasNextLine()) {
                        sendMessage(in.nextLine());
                    }
                    break;

                /*case "SEND FILE":
                    //out.println("send your text to " + targetClient.user.getUsername());
                    byte[] data = null;
                    String filename = null;
                    if (in.hasNext()) {
                        data = in.next().getBytes();
                    }
                    if (in.hasNextLine()) {
                        filename = in.nextLine();
                    }
                    if (!Objects.isNull(data) && !Objects.isNull(filename)) {
                        FileData newMessage = new FileData(user, targetClient.user, LocalDateTime.now(), filename, data.length, data);

                        try {
                            lock.lock();
                            databaseHandler = DatabaseHandler.getInstance();
                            databaseHandler.appendPendMessage(newMessage);
                        } catch (SQLException e) {
                            e.printStackTrace();
                        } finally {
                            try {
                                databaseHandler.close();
                            } catch (SQLException e) {
                                throw new RuntimeException(e);
                            }
                            lock.unlock();
                        }
                    }
                    break;*/

                case "POP":

                    try {
                        lock.lock();
                        databaseHandler = DatabaseHandler.getInstance();
                        databaseHandler.popPendMsg(databaseHandler.getNextMsgId(user, targetClient.user));
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    } finally {
                        try {
                            databaseHandler.close();
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                        lock.unlock();
                    }
                case "EXIT CHAT":
                    messageReceiver.interrupt();
                    break;
                default:
                    out.println("Invalid command.");
                    break;
            }
        }
    }

    private void sendMessage(String message) {
        TextMessage newMessage = null;
        if (!message.isEmpty()){
            newMessage = new TextMessage(user, targetClient.user, message, LocalDateTime.now());
        }
        try {
            lock.lock();
            databaseHandler = DatabaseHandler.getInstance();
            databaseHandler.appendPendMessage(newMessage);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                databaseHandler.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            lock.unlock();
        }
    }

    private Thread getThread() {
        messageService = new MessageService(this);
        fileService = new FileService(this);

        Thread messageReceiver = new Thread(() -> {
            while (true) {
                try {
                    lock.lock();
                    databaseHandler = DatabaseHandler.getInstance();

                    //out.println(targetClient.user);
                    Message message = databaseHandler.getNextPendMsg(user, targetClient.user);
                    if (message instanceof TextMessage) {
                        out.println("RECEIVED MESSAGE:" + (message));
                        messageService.receiveMessage((TextMessage) message);

                    }
                    else if (message instanceof FileData) {
                        fileService.receiveMessage((FileData) message);
                        databaseHandler.popPendMsg(databaseHandler.getNextMsgId(user, targetClient.user));
                    }

                } catch (SQLException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        databaseHandler.close();
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                    lock.unlock();
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ignored) {
                    }
                }
            }
        });

        messageReceiver.start();
        return messageReceiver;
    }

    private void registerUser() throws SQLException {
        ArrayList<String> arguments = new ArrayList<>();

        if (in.hasNextLine()) {
            arguments.add(in.nextLine());
        }
        if (in.hasNextLine()) {
            arguments.add(in.nextLine());
        }

        User newUser = new User(arguments.get(0), arguments.get(1), false);

        HashSet<User> users = null;

        try {
            lock.lock();

            databaseHandler = DatabaseHandler.getInstance();
            if (databaseHandler != null) {
                users = databaseHandler.getUsersData();
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            databaseHandler.close();
            lock.unlock();
        }

        authenticationService = new AuthenticationService();
        securityModule = new SecurityModule();

        if (users == null || !authenticationService.authenticateNewUser(newUser, users)) {
            newUser.setPassword(securityModule.cipherString(newUser.getPassword()));

            try {

                lock.lock();
                databaseHandler = DatabaseHandler.getInstance();
                if (databaseHandler != null) {
                    databaseHandler.addUserData(newUser);
                }

            } catch (SQLException e) {
                throw new RuntimeException(e);
            } finally {
                databaseHandler.close();
                lock.unlock();
            }

            out.println("REGISTERED");

        }
        else {
            out.println("NOT REGISTERED");
        }
    }

    public void Login() throws SQLException {
        ArrayList<String> arguments = new ArrayList<>();

        if (in.hasNextLine()) {
            arguments.add(in.nextLine());
        }
        if (in.hasNextLine()) {
            arguments.add(in.nextLine());
        }

        User newUser = new User(arguments.get(0), arguments.get(1), false);
        HashSet<User> users = null;

        try {
            lock.lock();

            databaseHandler = DatabaseHandler.getInstance();
            if (databaseHandler != null) {
                users = databaseHandler.getUsersData();
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            databaseHandler.close();
            lock.unlock();
        }

        authenticationService = new AuthenticationService();
        securityModule = new SecurityModule();

        if (users == null || !authenticationService.authenticateUser(newUser, users)) {
            out.println("NOT LOGGED");

        }
        else {
            try {
                lock.lock();
                databaseHandler = DatabaseHandler.getInstance();
                if (databaseHandler.checkUserState(newUser)){
                    out.println("NOT LOGGED");
                }else{
                    databaseHandler.changeUserState( newUser, true);
                    user = newUser;
                    user.setOnline(true);

                    Server.registerClient(user.getUsername(), this);

                }

            } catch (SQLException e) {
                throw new RuntimeException(e);
            } finally {
                databaseHandler.close();
                lock.unlock();
            }
            out.println("LOGGED");

        }
    }

    public User getUser() {
        return user;
    }


    private void endClientConnection() throws SQLException {
        if (user != null){
            user.setOnline(false);

            try {
                lock.lock();
                databaseHandler = DatabaseHandler.getInstance();
                if (databaseHandler.checkUserState(user)){
                    databaseHandler.changeUserState( user, false);
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            } finally {
                databaseHandler.close();
                lock.unlock();
            }
        }
        closeConnection(clientSocket, in, out);
    }
    private void closeConnection(Socket clientSocket, Scanner in, PrintWriter out){
        try {
            clientSocket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        in.close();
        out.close();
        Server.removeClient(user.getUsername());
    }
}
