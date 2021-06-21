package gameserver;

// Fig. 28.3: Server.java
// Server portion of a client/server stream-socket connection.
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;


public class GameServer
{
    private ObjectOutputStream output;   // output stream to client
    private ObjectInputStream input;     // input stream from client
    private ServerSocket server;         // server socket
    private Socket connection;           // connection to client

    private final Set<String> userNames = new HashSet<>();               // Usernames of online clients.
    private final ArrayList<ClientThread> clientThreads = new ArrayList<>();     // Threads of online clients.

    private BufferedReader in;
    private PrintWriter out;

    //public static final String POS1 = "p{\"x\":\"0\", \"y\": \"2.12\" ,\"z\": \"-32.5\"}\n";
    //public static final String POS2 = "p{\"x\":\"3.4\",\"y\": \"2.34\",\"z\": \"29.01\"}\n";
    // set up and run server
    public void runServer()
    {
        try // set up server to receive connections; process connections
        {
            server = new ServerSocket(12345, 3); // create ServerSocket

            while (true)
            {
                try
                {
                    waitForConnection();     // wait for a connection
                    getStreams();            // get input & output streams
                    //processConnection();     // process connection
                    ClientThread newClient = new ClientThread(connection, out, this,in);
                    if(clientThreads.size()== 1){

                        newClient.setEnemy(clientThreads.get(0));
                        clientThreads.get(0).setEnemy(newClient);
                        clientThreads.get(0).sendConnectionInfo("c{\"con\":\"yesnew\"}\n");
                        System.out.println("ilk data => " + "c{\"con\":\"yesnew\"}\n");

                    }

                    clientThreads.add(newClient);
                    newClient.start();
                }
                catch (EOFException eofException)
                {
                    System.out.println("\nServer terminated connection");
                }
            }
        }
        catch (IOException ioException)
        {

        }
    }

    // wait for connection to arrive, then display connection info
    private void waitForConnection() throws IOException
    {
        System.out.println("Waiting for connection\n");
        connection = server.accept(); // allow server to accept connection

        System.out.println("Connection received from: " +
                connection.getInetAddress().getHostName());
    }

    // get streams to send and receive data
    private void getStreams()
    {
        try {
            System.out.println("Streamlar alınıyor");
            // set up output stream for objects
            //output = new ObjectOutputStream(connection.getOutputStream());
            //  output.flush(); // flush output buffer to send header information

            out = new PrintWriter(connection.getOutputStream());
            System.out.println("Streamlar alınıyor output");
            // set up input stream for objects
            // input = new ObjectInputStream(connection.getInputStream());
            in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            System.out.println("Streamlar alınıyor input");

            //StringBuilder will_be_sent= new StringBuilder();

            int value = 0;
            while((value = in.read()) != -1) {

                // converts int to character
                char c = (char)value;
                //      will_be_sent.append(c);


                // prints character
                //    System.out.println(c + " " + value);
                if ((String.valueOf(c).equals("}"))){
                    if(clientThreads.size() == 0){
                        System.out.println("ilk data => " + "c{\"con\":\"no\"}\n");
                        sendData("c{\"con\":\"no\"}\n");
                    }
                    else{
                        System.out.println("ilk data => " + "c{\"con\":\"yesold\"}\n");
                        sendData("c{\"con\":\"yesold\"}\n");
                    }
                    break;
                }



            }








            //  System.out.println(("kullanıcıdan gelen => " +  in.read()));
            System.out.println("\nGot I/O streams\n");
        }catch (Exception e){
            System.out.println("Exception meydana geldi");
            e.printStackTrace();
        }

    }


    private void sendData(String data){
//        JSONObject o = null;
//        try {
//            o = new JSONObject(data);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        assert o != null;
//        if(o.length() == 1){
//            System.out.println("size => " + clientThreads.size());
//            if(clientThreads.size()== 0){
//
//                data  = ChatServer.POS1;
//            }else{
//                data = ChatServer.POS2;
//            }
//        }
        //   System.out.println("pozisyon ayarlandı" + data);
        out.write(data);
        out.flush();
    }

    // close streams and socket
    private void closeConnection()
    {
        System.out.println("\nTerminating connection\n");

        try
        {
            output.close(); // close output stream
            input.close(); // close input stream
            connection.close(); // close socket
        }
        catch (IOException ioException)
        {
            ioException.printStackTrace();
        }
    }







    void addUserName(String userName) {
        userNames.add(userName);
    }


    void removeUser(String userName, ClientThread aUser) {
        boolean removed = userNames.remove(userName);
        if (removed) {
            clientThreads.remove(aUser);

            String message = "QUIT-"+userName +"-The user " + userName + " has left the chat";
        }
    }

    Set<String> getUserNames() {
        return this.userNames;
    }

    boolean hasUsers() {
        return !this.userNames.isEmpty();
    }

    public static void main(String[] args)
    {
        new GameServer().runServer();          // run server application
    }
}
