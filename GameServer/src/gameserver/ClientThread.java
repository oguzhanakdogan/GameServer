
package gameserver;
import java.io.*;
import java.net.Socket;

public class ClientThread extends Thread {
    private final Socket connection;
    private final GameServer server;
    // private final ObjectOutputStream output;   // output stream to client
    //  private final ObjectInputStream input;     // input stream from client
    private final PrintWriter out;
    private String username;

    private final BufferedReader in;
    private ClientThread enemy;
    public ClientThread(Socket connection,
                        PrintWriter out,
                        GameServer server,
                        BufferedReader in) {
        this.connection = connection;
        this.out = out;

        this.server = server;
        this.in = in;
    }

    public void run() {
        try {
            System.out.println("bekleniyor => ");
            int value = 0;
            StringBuilder will_be_sent= new StringBuilder();


            while((value = in.read()) != -1) {

                // converts int to character
                char c = (char)value;
                if (String.valueOf(c).equals("\n")){
                    continue;
                }
                will_be_sent.append(c);


                // prints character
                //  System.out.println(c + " " + value);
                if ((String.valueOf(c).equals("}"))){
                    sendData(will_be_sent.toString());
                    will_be_sent = new StringBuilder();
                }

            }


            server.removeUser(username, this);
            connection.close();

        } catch (IOException ex) {
            System.out.println("Error in UserThread: " + ex.getMessage());
            server.removeUser(username,this);
            ex.printStackTrace();
        }
    }

    private void sendData(String data){

        if(enemy != null) {
            //   System.out.println("enemy'e gönderilen => " + data);
            enemy.sendPos(data);
        }

    }

    public void sendConnectionInfo(String info){
        out.write(info);
        out.flush();

    }
    public void sendPos(String position){


        out.write(position + "\n");
        out.flush();
    }
    public ClientThread getEnemy() {
        return enemy;
    }

    public void setEnemy(ClientThread enemy) {
        this.enemy = enemy;
    }

    public String getUsername() {
        return username;
    }

    public String toString(){


        return "";
    }
}