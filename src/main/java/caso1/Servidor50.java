package caso1;

import java.util.Scanner;

public class Servidor50 {

    TCPServer50 mTcpServer;
    Scanner sc;

    public static void main(String[] args) {
        Servidor50 objser = new Servidor50();
        objser.iniciar();
    }

    void iniciar() {
        new Thread(
                new Runnable() {
            @Override
            public void run() {
                mTcpServer = new TCPServer50(
                        new TCPServer50.OnMessageReceived() {
                    @Override
                    public void messageReceived(String message) {
                        synchronized (this) {
                        System.out.println("mensaje " + message);
                            ServidorRecibe(message);
                        }
                    }
                }
                );
                mTcpServer.run();
            }
        }
        ).start();
        //-----------------
        String salir = "n";
        sc = new Scanner(System.in);
        System.out.println("Servidor bandera 01");
        while (!salir.equals("s")) {
            salir = sc.nextLine();
            ServidorEnvia(salir);
        }
        System.out.println("Servidor bandera 02");

    }
    int contarcliente = 0;
    double rptacli[] = new double[20];
    double sumclient = 0;

    void ServidorRecibe(String llego) {
        System.out.println("SERVIDOR40 El mensaje:" + llego);
        if (llego != null && !llego.equals("")) {
            if (llego.trim().contains("rpta")) {
                String arrString[] = llego.split("\\s+");
                double data = Double.parseDouble(arrString[1]);
                if (data > 0) {
                    rptacli[contarcliente] = data;
                    System.out.println("i:" + contarcliente + " rptacli[i]" + rptacli[contarcliente]);
                    System.out.println("Llego el servidor: " + data);
                    System.out.println("Ellos son:" + this.mTcpServer.nrcli);//cuantos clientes son
                    contarcliente = contarcliente + 1; //incremento un cliente

                    if (contarcliente == this.mTcpServer.nrcli) {
                        for (int i = 0; i < contarcliente; i++) {
                            System.out.println("ya   i:" + i + " rptacli[i]" + rptacli[i]);
                            sumclient = sumclient + rptacli[i];

                        }
                        System.out.println("LA RESPUESTA TOTAL ES:" + sumclient);
                        
                        sumclient = 0;
                        for (int i = 0; i < contarcliente; i++) {
                            rptacli[i] = 0;
                        }
                        contarcliente = 0;

                    }
                }
            }
        }
    }

    void ServidorEnvia(String envia) {//El servidor tiene texto de envio
        if (envia != null) {
            System.out.println("Soy Server y envio" + envia);
            if (envia.trim().contains("enviar")) {
                System.out.println("SI TIENE ENVIO!!!");
               
                
                
                //String arrayString[] = envia.split("\\s+");
//                
//                String funcion = (arrayString[0]);
//                int a = Integer.parseInt(arrayString[1]);
//                int b = Integer.parseInt(arrayString[2]);
//                int segmentos = Integer.parseInt(arrayString[3]);
                
                
                if (mTcpServer != null) {
                    mTcpServer.sendMessageTCPServerRango(envia);
                }
            } else {
                System.out.println("NO TIENE ENVIO!!!");
            }
        }
    }
}
