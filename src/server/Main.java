package server;

import java.io.IOException;
import java.util.*;

public class Main {
    public static void main(String[] args) throws IOException {
        System.setProperty("java.net.preferIPv4Stack" , "true");   
        Scanner in = new Scanner(System.in);
        String subnet = "";
        int port = 0;

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
        
        final NetworkHelperServer nhs = new NetworkHelperServer(subnet, port);
        
        new InputHandler(nhs);

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