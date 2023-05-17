package caso1;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPServer50 {

    private String message;

    int nrcli = 0;

    public static final int SERVERPORT = 4444;
    private OnMessageReceived messageListener = null;
    private boolean running = false;
    TCPServerThread50[] sendclis = new TCPServerThread50[10];

    PrintWriter mOut;
    BufferedReader in;

    ServerSocket serverSocket;

    //el constructor pide una interface OnMessageReceived
    public TCPServer50(OnMessageReceived messageListener) {
        this.messageListener = messageListener;
    }

    public OnMessageReceived getMessageListener() {/////¨
        return this.messageListener;
    }

    public void sendMessageTCPServer(String message) {
        for (int i = 1; i <= nrcli; i++) {
            sendclis[i].sendMessage(message);
            System.out.println("ENVIANDO A JUGADOR " + (i));
        }
    }

    public void sendMessageTCPServerRango(String message) {

        double min, max;

        String arrayString[] = message.split("\\s+");

        String funcion = (arrayString[1]);
        double a = Integer.parseInt(arrayString[2]);
        double b = Integer.parseInt(arrayString[3]);
        int segmentos = Integer.parseInt(arrayString[4]);

        double d = ((b - a) / nrcli);// a = 5, b = 10, d = 1, range: 5 - 10, clientes = 6
        int num_segments = (int) (segmentos / nrcli);

        //double numeroOriginal = 0.8455;
        int lugaresDecimales = 1;
        double factor = Math.pow(10, lugaresDecimales);
        d = Math.floor(d * factor) / factor;
        System.out.println(d);

        for (int i = 1; i <= nrcli; i++) {

            if (i == nrcli) {
                min = ((i - 1) * d + a);
                max = b;
                num_segments = segmentos - num_segments * (nrcli - 1);
            } else {

                min = ((i - 1) * d + a);// min = 5, min = 5.8, 6.6 , 7.4 , 8.2 , 9
                max = ((i - 1) * d + d + a);//max = 5.8, max = 6.6, 7.4 , 8.2, 9, 10
            }
            
            sendclis[i].sendMessage("evalua "+ funcion +" " + min + " " + max+ " "+num_segments);
            
         
            
            System.out.println("ENVIANDO A JUGADOR " + (i));
        }
//        sendclis[nrcli].sendMessage("evalua " + ((d * (nrcli - 1)) + 1) + " " + (Rango));
//        System.out.println("ENVIANDO A JUGADOR " + (nrcli));

    }

    public void run() {
        running = true;
        try {
            System.out.println("TCP Server" + "S : Connecting...");
            serverSocket = new ServerSocket(SERVERPORT);

            while (running) {
                Socket client = serverSocket.accept();
                System.out.println("TCP Server" + "S: Receiving...");
                nrcli++;
                System.out.println("Engendrado " + nrcli);
                sendclis[nrcli] = new TCPServerThread50(client, this, nrcli, sendclis);
                Thread t = new Thread(sendclis[nrcli]);
                t.start();
                System.out.println("Nuevo conectado:" + nrcli + " jugadores conectados");

            }

        } catch (Exception e) {
            System.out.println("Error" + e.getMessage());
        } finally {

        }
    }

    public TCPServerThread50[] getClients() {
        return sendclis;
    }

    public interface OnMessageReceived {

        public void messageReceived(String message);
    }
}
