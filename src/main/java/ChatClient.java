/**
 * Created by h3dg3wytch on 9/26/16.
 */
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.Executors;

/**
 * Created by root on 9/24/16.
 */
public class ChatClient {

    private BufferedReader in;
    private PrintWriter out;
    private JFrame frame = new JFrame("ChatUp");
    private JTextField textField = new JTextField(40);
    private JTextArea messageArea = new JTextArea(8, 40);

    public ChatClient(){
        //layout GUI
        textField.setEditable(false);
        messageArea.setEditable(false);
        frame.getContentPane().add(textField, "South");
        frame.getContentPane().add(new JScrollPane(messageArea), "Center");
        frame.pack();

        //Add listeners
        textField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                out.println(textField.getText());
                textField.setText("");
            }
        });
    }

    private String getServerAdress() {
        return JOptionPane.showInputDialog(frame, "Enter IP Address of the Server:", "Welcome! Thanks for joining!", JOptionPane.QUESTION_MESSAGE);
    }

    public String getName() {
        return JOptionPane.showInputDialog(frame, "Choose a screen name:", "Screen name selection", JOptionPane.PLAIN_MESSAGE);
    }

    private void run(){
        String serverAddress = getServerAdress();
        try(Socket socket = new Socket(serverAddress, 8080);
        ){
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            while (true){
                String line = in.readLine();
                System.out.println(line);
                if(line.startsWith("SUBMITNAME")){
                    String name = getName();
                    out.println(name);
                }else if(line.startsWith("NAMEACCEPTED")){
                    textField.setEditable(true);
                }else if(line.startsWith("MESSAGE")){
                    messageArea.append(line.substring(8) +"\n");
                }
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception{
        ChatClient client = new ChatClient();
        client.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        client.frame.setVisible(true);
        client.run();
    }

}