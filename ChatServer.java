package ChatClient;

import javax.net.ssl.SSLSocket;
import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;

/**
 * Created by root on 9/24/16.
 */
public class ChatServer{

    private static final int PORT_NUMBER = 8080;
    private static HashSet<String> names = new HashSet<String>();
    private static HashSet<PrintWriter> writers = new HashSet<>();

    public static void main(String[] args) {
        try(ServerSocket serverSocket = new ServerSocket(PORT_NUMBER)){
            while(true)
                new MessageHandler(serverSocket.accept()).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class MessageHandler extends Thread {
        private String name;
        private Socket clientSocket;
        private BufferedReader in;
        private PrintWriter out;

        public MessageHandler(Socket clientSocket){
            super("MessageThread");
            this.clientSocket = clientSocket;
        }

        @Override
        public void run() {
            try{
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                out = new PrintWriter(clientSocket.getOutputStream(), true);
                //Request a name from the client. It will keep requesting until it gives you a unique user name
                while(true){
                    out.println("SUBMITNAME");
                    name = in.readLine();
                    System.out.println(name);
                    if(name == null){
                       continue;
                    }

                    synchronized (names){
                        if(!names.contains(name)){
                            names.add(name);
                            break;
                        }
                    }
                }

                //We get the name, and so now we add the writer to the set of all writers so we can receive broadcast messages
                out.println("NAMEACCEPTED");
                writers.add(out);

                //Accept messages from the client, adnt ehn broadcast them.
                while(true){
                    String input = in.readLine();
                    if(input == null){
                        return;
                    }
                    for(PrintWriter writer : writers){
                        writer.println("MESSAGE " + name + ": " + input);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }finally {

                if(name != null)
                    names.remove(name);
                if(out != null)
                    writers.remove(out);
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }



}
