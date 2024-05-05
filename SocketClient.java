import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

@SuppressWarnings("serial")
public class SocketClient extends JFrame implements ActionListener, Runnable {
    JTextArea textArea = new JTextArea();
    JScrollPane jp = new JScrollPane(textArea);
    JTextField input_Text = new JTextField();
    JMenuBar menuBar = new JMenuBar();
    JButton sendImageBtn = new JButton("Send Image");

    Socket sk;
    BufferedReader br;
    PrintWriter pw;
    private final Logger logger = Logger.getLogger(SocketClient.class.getName());

    public SocketClient() {
        super("Chit Chat");
        setFont(new Font("Arial Black", Font.PLAIN, 12));
        setForeground(new Color(0, 0, 51));
        setBackground(new Color(51, 0, 0));
        textArea.setToolTipText("Chat History");
        textArea.setForeground(new Color(50, 205, 50));
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.BOLD, 13));

        textArea.setBackground(new Color(0, 0, 0));

/*
         JMenu helpMenu = new JMenu("Help");
        JMenuItem update = new JMenuItem("Update Information");
        JMenuItem connect_List = new JMenuItem("Visitor List");

        helpMenu.add(update);
        helpMenu.add(connect_List);

        menuBar.add(helpMenu);
        setJMenuBar(menuBar);
*/
        getContentPane().add(jp, "Center");
        input_Text.setText("Enter your Message:");
        input_Text.setToolTipText("Enter your Message");
        input_Text.setForeground(new Color(0, 0, 0));
        input_Text.setFont(new Font("Tahoma", Font.BOLD, 11));
        input_Text.setBackground(new Color(230, 230, 250));
        
        getContentPane().add(input_Text, "South");
        setSize(325, 411);
        setVisible(true);

        input_Text.requestFocus(); //Place cursor at run time, work after screen is shown

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        input_Text.addActionListener(this); //Event registration

        sendImageBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                int result = fileChooser.showOpenDialog(SocketClient.this);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    sendImage(selectedFile);
                }
            }
        });

        // Add the send image button to the frame
        getContentPane().add(sendImageBtn, "North");

        try {
            FileHandler fileHandler = new FileHandler("client.log");
            fileHandler.setFormatter(new SimpleFormatter());
            logger.addHandler(fileHandler);
        } catch (Exception e) {
            logger.log(Level.WARNING, "Failed to initialize logging", e);
        }
    }

    public void serverConnection() {
        try {
            String IP = JOptionPane.showInputDialog(this, "Please enter a server IP.", JOptionPane.INFORMATION_MESSAGE);
            sk = new Socket(IP, 1234);

            String name = JOptionPane.showInputDialog(this, "Please enter a nickname", JOptionPane.INFORMATION_MESSAGE);
/*            while (name.length() > 7) {
                name = JOptionPane.showInputDialog(this, "Please enter a nickname.(7 characters or less)", JOptionPane.INFORMATION_MESSAGE);
            }
*/
            //read
            br = new BufferedReader(new InputStreamReader(sk.getInputStream()));

            //writing
            pw = new PrintWriter(sk.getOutputStream(), true);
            pw.println(name); // Send to server side

            new Thread(this).start();
            logger.info("Attempting to connect to the server...");

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Socket Connection error", e);
        }
    }

    public static void main(String[] args) {
        new SocketClient().serverConnection(); //Method call at the same time object creation
    }

    @Override
    public void run() {
        String data = null;
        try {
            logger.info("Client thread started");
            while ((data = br.readLine()) != null) {
                if (data.startsWith("/userlist:")) {
                    // Extract the user list from the message and display it in the chat window
                    String userList = data.substring("/userlist:".length());
                    textArea.append("Connected Users: " + userList + "\n");
                }
                textArea.append(data + "\n"); //textArea Decrease the position of the box's scroll bar by the length of the text entered
                textArea.setCaretPosition(textArea.getText().length());
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Client run fail", e);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            logger.info("Sending message to server...");
            String data = input_Text.getText();
            // Check if the user typed the "/users" command
            if ("/users".equalsIgnoreCase(data.trim())) {
                // Send a request to the server to get the list of connected users
                pw.println("/users");
            } else {
                // Send the message to the server
                pw.println(data);
            }
            input_Text.setText("");
        }catch (Exception ex){
            logger.log(Level.SEVERE, "Error sending message to server", ex);
        }
    }

    private void sendImage(File file) {
        try {
            // Read the image file into a byte array
            FileInputStream fis = new FileInputStream(file);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                bos.write(buffer, 0, bytesRead);
            }
            fis.close();
            byte[] imageData = bos.toByteArray();

            // Send the byte array containing the image data to the server
            pw.println("/image");
            ObjectOutputStream oos = new ObjectOutputStream(sk.getOutputStream());
            oos.writeObject(imageData);
            oos.flush();
            JOptionPane.showMessageDialog(this, "Image sent successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
            logger.info("Image sent successfully");
        } catch (IOException ex) {
            logger.log(Level.SEVERE, "Error sending image", ex);
        }
    }
}