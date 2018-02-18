import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Zacky Kharboutli on 2018-02-14.
 */
public class serverSocket {
    final static int PORT =5000;
    private static ServerSocket connection=null;


    public static void main (String args[]) throws IOException {

        try{
            connection = new ServerSocket(PORT);
            while (true){
                Socket socket = connection.accept();
                int userId=0;
                clientThread client = new clientThread(socket,++userId);
                Thread thread = new Thread(client);
                thread.start();
            }
        }catch (IOException e){
            System.out.println("Error: The chosen port is not valid or in use");
            System.exit(1);        }

    }
}

