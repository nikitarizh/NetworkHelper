package server;

import java.io.IOException;
import java.util.*;

public class Main {
    public static void main(String[] args) throws IOException {
        System.setProperty("java.net.preferIPv4Stack" , "true");   
        Scanner in = new Scanner(System.in);
        
        System.out.println("\nEnter port: ");
        int port = 0;
        try {
            port = in.nextInt();
        }
        catch (Exception e) {
            // Set default port
        }
        
        final NetworkHelperServer nhs = new NetworkHelperServer(port);
        
        new InputListener(nhs);

        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (!nhs.userScan) {
                    nhs.checkHosts("192.168.100", 5000);
                }
            }
        }, (long) 0, (long) 10000);

    }
}