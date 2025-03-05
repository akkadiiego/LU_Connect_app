package Server;

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

public class ClientManager extends Thread{
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

    public ClientManager(Socket clientSocket) throws IOException {
        this.clientSocket = clientSocket;
        this.user = null;
        this.in = new Scanner(clientSocket.getInputStream());
        this.out = new PrintWriter(clientSocket.getOutputStream(), true);
    }

    @Override
    public void run() {
        System.out.println("Conectado: " + Integer.parseInt(Thread.currentThread().getName().split("-")[3]));

        while (in.hasNextLine()) {
            switch (in.nextLine()) {
                case "END":
                    try {
                        endClientConnection();
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                    break;

                case "PING":
                    out.println("PONG");
                    break;
                // TODO: Implement server functions here

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

                case "ENTER CHAT":
                    out.println("estas aqui");
                    if (this.user == null) {
                        //out.println("You need to login first");
                        break;
                    }

                    targetClient = null;
                    if (in.hasNextLine()) {
                        targetClient = Server.getClient(in.nextLine());
                        if (targetClient == null) {
                            out.println("User not found or not online.");
                            break;
                        }
                        //out.println("Chat entered: " + targetClient.user.getUsername());

                        Thread messageReceiver = getThread();

                        while (true) {
                            if (in.hasNextLine()) {
                                String input = in.nextLine();
                                switch (input) {
                                    case "SEND TEXT":
                                        //out.println("send your text to " + targetClient.user.getUsername());
                                        if (in.hasNextLine()) {
                                            String messageContent = in.nextLine();
                                            TextMessage newMessage = new TextMessage(user, targetClient.user, messageContent, LocalDateTime.now());

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
                                        break;
                                    case "EXIT CHAT":
                                        messageReceiver.interrupt();
                                        return;
                                    default:
                                        out.println("Invalid command.");
                                        break;
                                }
                            }
                        }
                    }
                    break;
            }
        }
    }

    private Thread getThread() {
        MessageService messageService = new MessageService(this, targetClient);

        Thread messageReceiver = new Thread(() -> {
            while (true) {
                try {
                    lock.lock();
                    databaseHandler = DatabaseHandler.getInstance();

                    Message message = databaseHandler.getNextPendMsg(user, targetClient.user);
                    if (message instanceof TextMessage) {
                        messageService.receiveMessage((TextMessage) message);
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
                        Thread.sleep(500);
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

        out.println("Give me the username");
        if (in.hasNextLine()) {
            arguments.add(in.nextLine());
        }

        out.println("Give me the password");
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

            out.println("User registered successfully, now you can log in :)");

        }
        else {
            out.println("User already exists");
        }
    }

    public void Login() throws SQLException {
        ArrayList<String> arguments = new ArrayList<>();

        out.println("Give me your username");
        if (in.hasNextLine()) {
            arguments.add(in.nextLine());
        }

        out.println("Give me your password");
        if (in.hasNextLine()) {
            arguments.add(in.nextLine());
        }

        user = new User(arguments.get(0), arguments.get(1), false);
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

        if (users == null || !authenticationService.authenticateUser(user, users)) {
            out.println("This User does not exist or is already connected!");
            user = null;
        }
        else {
            try {
                lock.lock();
                databaseHandler = DatabaseHandler.getInstance();
                if (databaseHandler.checkUserState(user)){
                    user = null;
                }else{
                    databaseHandler.changeUserState( user, true);
                    user.setOnline(true);
                    Server.registerClient(user.getUsername(), this);
                }

            } catch (SQLException e) {
                throw new RuntimeException(e);
            } finally {
                databaseHandler.close();
                lock.unlock();
            }

            out.println("You just logged!");
        }
    }

    public User getUser() {
        return user;
    }

    static class MessageReceiver extends Thread{
        @Override
        public void run(){

        }
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
    }
}
