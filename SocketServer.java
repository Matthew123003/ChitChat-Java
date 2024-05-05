import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class SocketServer {
    ServerSocket server;
    Socket sk;
    InetAddress addr;

    private final Logger logger = Logger.getLogger("Socket Server Logger");
    ArrayList<ServerThread> list = new ArrayList<ServerThread>();
    private ArrayList<String> connectedUsers = new ArrayList<>();

    // Add user to the list of connected users
    private void addUser(String username) {
        connectedUsers.add(username);
    }

    // Remove user from the list of connected users
    private void removeUser(String username) {
        connectedUsers.remove(username);
    }

    // Get the list of connected users
    public List<String> getConnectedUsers() {
        return connectedUsers;
    }


    public SocketServer() {
        try {
            FileHandler fileHandler = new FileHandler("server.log");
            fileHandler.setFormatter(new SimpleFormatter());
            logger.addHandler(fileHandler);
        	addr = InetAddress.getByName("127.0.0.1");
        	//addr = InetAddress.getByName("192.168.43.1");
            
        	server = new ServerSocket(1234,50,addr);
            logger.info("\n Waiting for Client connection");
            SocketClient.main(null);
            while(true) {
                sk = server.accept();
                logger.info(sk.getInetAddress() + " connect");

                //Thread connected clients to ArrayList
                ServerThread st = new ServerThread(this);
                addThread(st);
                st.start();
            }
        } catch(IOException e) {
            logger.log(Level.WARNING, "Server Socket Failed", e);
        }
    }

    public void addThread(ServerThread st) {
        list.add(st);
    }

    public void removeThread(ServerThread st){
        list.remove(st); //remove
    }

    public void broadCast(String message){
        for(ServerThread st : list){
            st.pw.println(message);
        }
    }

    public static void main(String[] args) {
        new SocketServer();
    }
}

class ServerThread extends Thread {
    SocketServer server;
    PrintWriter pw;
    String name;
    private final Logger logger = Logger.getLogger("Server Thread Logger");

    public ServerThread(SocketServer server) {
        this.server = server;
    }

    @Override
    public void run() {
        try {
            // read
            BufferedReader br = new BufferedReader(new InputStreamReader(server.sk.getInputStream()));

            // writing
            pw = new PrintWriter(server.sk.getOutputStream(), true);
            name = br.readLine();
            server.broadCast("**["+name+"] Entered**");

            String data;
            while((data = br.readLine()) != null ){
                if(data.equals("/list")){
                    pw.println("a");
                }
                server.broadCast("["+name+"] "+ data);
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Exception occurred in ServerThread", e);
            //Remove the current thread from the ArrayList.
            server.removeThread(this);
            server.broadCast("**["+name+"] Left**");
            logger.info(server.sk.getInetAddress()+" - ["+name+"] Exit");
            logger.info(e + "---->");
        }
    }
}