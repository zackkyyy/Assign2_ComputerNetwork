import HttpHandlar.HttpRequest;
import HttpHandlar.HttpResponse;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;


/**
 * Created by Zacky Kharboutli on 2018-02-15.
 */
public class clientThread implements Runnable {
    public static final int BUFSIZE = 214748360;
    public Socket socket;
    public int userNumber;
    public DataInputStream in;
    public DataOutputStream out;
    byte[] buf;

    public clientThread(Socket socket, int userNumber) {
        this.socket = socket;
        this.userNumber = userNumber;
        buf = new byte[BUFSIZE];

        try {
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            System.err.println("Error: unable to stream");
            e.printStackTrace();
        }


    }

    public void run() {
        String receivedMessage = "";      // here we will get the desired method
        String[] receivedMessageArray=new String[10];
        int ind = 0;
        while (ind == 0) {

            try {

                int nbrOfString=0;
                int byteRead = 0;
                try {
                    if ((byteRead = in.read(buf)) != -1) {
                        receivedMessage += new String(buf, 0, byteRead);
                        //System.out.println(receivedMessage);

                      /*  if(receivedMessage.length()>20000){
                            receivedMessageArray[nbrOfString]=receivedMessage;
                            receivedMessage="";
                            nbrOfString+=1;
                        }*/
                       /* System.out.println("one"+receivedMessageArray[0]);
                        System.out.println("two"+receivedMessageArray[1]);
                        System.out.println("three"+receivedMessageArray[2]);*/
                    }




                    HttpResponse response = new HttpResponse(new HttpRequest(receivedMessage), buf);

                    // write the html response when errors occurs
                    out.write(response.getStatus().getBytes());
                    System.out.println(response.getStatus());
                    out.write(response.getResponse().getBytes());
                    System.out.println(response.getResponse());

                    // stream png files
                    if (response.isImage()) {
                        out.write(response.getImgData());
                    } else if (response.isSomethingWrong()) {
                        out.write(response.getHttpBody().getBytes());
                    }
                    out.flush();
                    ind = 1;

                } catch (IOException e) {
                    e.printStackTrace();
                }

                System.out.println();


            } catch (Exception e) {
                e.getMessage();
            }
        }
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("            Server with the user " + socket.getInetAddress() + " is now closed");
    }
}
