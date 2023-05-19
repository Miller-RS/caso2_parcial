package caso1;

import java.util.Scanner;

class Cliente50 {

    public double sum[] = new double[40];
    TCPClient50 mTcpClient;
    Scanner sc;

    public static void main(String[] args) {
        Cliente50 objcli = new Cliente50();
        objcli.iniciar();
    }

    void iniciar() {
        new Thread(
                new Runnable() {
            @Override
            public void run() {
                mTcpClient = new TCPClient50("192.168.1.7",
                        new TCPClient50.OnMessageReceived() {
                    @Override
                    public void messageReceived(String message) {
                        ClienteRecibe(message);
                    }
                }
                );
                mTcpClient.run();
            }
        }
        ).start();
        //---------------------------

        String salir = "n";
        sc = new Scanner(System.in);
        System.out.println("Cliente bandera 01");
        while (!salir.equals("s")) {
            salir = sc.nextLine();
            ClienteEnvia(salir);
        }
        System.out.println("Cliente bandera 02");

    }

    void ClienteRecibe(String llego) {
        System.out.println("CLINTE50 El mensaje::" + llego);
        if (llego.trim().contains("evalua")) {
            String arrayString[] = llego.split("\\s+");

            String funcion = arrayString[1];
            double min = Double.parseDouble(arrayString[2]);
            double max = Double.parseDouble(arrayString[3]);
            int num_segment = Integer.parseInt(arrayString[4]);

            System.out.println("el min:" + min + " el max:" + max);
            procesar(funcion, min, max, num_segment);
        }
    }

    private static double evaluarExpresion(String expresion, double x) {
        double resultado = 0.0;

        //"7x^1+8x^2"
        // Eliminar espacios en blanco y separar por el signo '+'
        String[] terminos = expresion.replaceAll("\\s+", "").split("\\+");
        //7x^1
        //8x^2

        // Evaluar cada término y sumarlos al resultado
        for (String termino : terminos) {
            // Extraer el coeficiente y el exponente
            String[] partes = termino.split("x\\^");
            //7 1
            double coeficiente = Double.parseDouble(partes[0]);
            int exponente = Integer.parseInt(partes[1]);

            // Evaluar el término y sumarlo al resultado
            resultado += coeficiente * Math.pow(x, exponente);
        }

        return resultado;
    }

    void ClienteEnvia(String envia) {
        if (mTcpClient != null) {
            mTcpClient.sendMessage(envia);
        }
    }

    double funcion(int fin) {
        double sum = 0;
        for (int j = 0; j <= fin; j++) {
            sum = sum + Math.sin(j * Math.random());
        }
        return sum;
    }

    void procesar(String f, double a, double b, double n) {

        double min, max;
        double N = (b - a);//14;
        int H = 6;//luego aumentar
        double d =  ((N) / H);
        System.out.println("valor del d: "+d);
        double ancho = N / n;
        double numero = n / H;
        double num_segments_hilo = Math.round(numero);
        
        System.out.println("numero: "+ numero);
        System.out.println("valor del num_"+ num_segments_hilo);

        Thread todos[] = new Thread[40];
        for (int i = 0; i < H; i++) {

            if (i == H - 1) {
                min = ((i) * d + a);
                max = b;
                num_segments_hilo = n - num_segments_hilo * (H - 1);
                System.out.println("a: " + min + "b: " + max + "hilo: " + i + "num_segments: " + num_segments_hilo);
            } else {

                min = ((i) * d + a);// min = 5, min = 5.8, 6.6 , 7.4 , 8.2 , 9
                max = ((i) * d + d + a);//max = 5.8, max = 6.6, 7.4 , 8.2, 9, 10
                System.out.println("a: " + min + "b: " + max + "hilo: " + i + "num_segments: " + num_segments_hilo);
            }
            todos[i] = new tarea0101(min, max, f, num_segments_hilo, i);
            todos[i].start();

        }
//        System.out.println("a" + ((d * (H - 1)) + a) + "b" + (b + 1) + " i" + (H - 1));
//        Thread Hilo;
//        todos[H - 1] = new tarea0101(((d * (H - 1)) + a), (b + 1), H - 1);
//        todos[H - 1].start();
        for (int i = 0; i <H; i++) {//AQUI AQUI VER <=
            try {
                todos[i].join();
            } catch (InterruptedException ex) {
                System.out.println("error" + ex);
            }
        }
        double areatotal = 0;
        for (int i = 0; i < H; i++) {
            areatotal = areatotal + sum[i];
        }
        System.out.println("Area total TOTAL____:" + areatotal);
        ClienteEnvia("rpta " + areatotal);
    }

    public class tarea0101 extends Thread {

        public double max, min;
        double segments;
        int id;
        String funtion;

        tarea0101(double min_, double max_, String funtion_, double segments_, int id_) {
            max = max_;
            min = min_;
            segments = segments_;
            funtion = funtion_;
            id = id_;
        }

        public void run() {
            System.out.println("bandera 1");
            double area = 0, ancho, ultimo_ancho;
            ancho = (max - min) / segments;
            System.out.println("bandera 2");
            int lugaresDecimales = 1, count = 0;
//            double factor = Math.pow(10, lugaresDecimales);
//            ancho = Math.floor(ancho * factor) / factor;
//            System.out.println("bandera 3");
//            System.out.println(ancho);
            for (double i = min; i < max; i += ancho) {
                
                System.out.println("valores del minimo: "+i+"del id: "+ id);
                //System.out.println("bandera 3.5");
                area += ancho * evaluarExpresion(funtion, i);//195
                //.out.println("bandera 3.8");
                 System.out.println("valores del area: "+id+" "+area);
                count++;
                
            }
            //195
            System.out.println("bandera 4");
            //ultimo_ancho = max - ancho * count;
            //area = area + ancho * evaluarExpresion(funtion, min + ancho * count);
            sum[id] = area;
            System.out.println("bandera 5");
            //System.out.println(" min:" + min + " max:" + (max - 1) + " id:" + id + " suma:" + suma);
        }
    }

}
