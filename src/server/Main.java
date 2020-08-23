package server;

import java.io.IOException;
import java.util.*;

public class Main {
    public static void main(String[] args) throws IOException {
        // prefer IPv4 over IPv6
        System.setProperty("java.net.preferIPv4Stack" , "true");   
        // set Input scanner
        Scanner in = new Scanner(System.in);
        
        // initialize subnet and port
        String subnet = "";
        int port = 0;

        // enter parameters
        System.out.println("\nEnter subnet: ");
        try {
            subnet = in.next();
        }
        catch (Exception e) {
            // Set default subnet
        }
        
        System.out.println("\nEnter port: ");
        try {
            port = in.nextInt();
        }
        catch (Exception e) {
            // Set default port
        }

        // initialize Server
        final NetworkHelperServer nhs = new NetworkHelperServer(subnet, port);
        
        // initialize InputHandler
        new InputHandler(nhs);

        // scan online hosts every 10 seconds
        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (!nhs.userScan) {
                    nhs.checkHosts(5000);
                }
            }
        }, (long) 0, (long) 10000);
    }
}