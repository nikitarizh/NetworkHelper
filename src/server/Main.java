package server;

import java.io.IOException;
import java.util.*;
import java.io.*;

public class Main {
    public static void main(String[] args) throws IOException {
        // prefer IPv4 over IPv6
        System.setProperty("java.net.preferIPv4Stack" , "true");
        // set Input scanner
        Scanner in = new Scanner(System.in);
        
        // initialize subnet and port
        String subnet = "";
        int port = 0;

        // try to find config file
        File configFile = new File("serverConfig.properties");
        try {
            FileReader reader = new FileReader(configFile);
            Properties props = new Properties();
            props.load(reader);
        
            String fileSubnet = props.getProperty("subnet");
            String filePort = props.getProperty("port");
        
            reader.close();
            if (isNotNullOrEmpty(fileSubnet) && isNotNullOrEmpty(filePort)) {
                System.out.println("Found a config file. Do you want to load parameters from it? y/n");
                char inp = in.next().charAt(0);
                if (inp == 'y' || inp == 'Y') {
                    subnet = fileSubnet;
                    port = Integer.parseInt(filePort);
                }
            }
        }
        catch (Exception e) {}

        // enter parameters (if no config found or user wanted to enter manually)
        if (subnet.equals("") || port == 0) {
            System.out.println("\nEnter subnet: ");
            while (true) {
                try {
                    subnet = in.next();
                    break;
                }
                catch (Exception e) {
                    System.out.println("Incorrect input. Please, try again");
                    in.next();
                }
            }
            
            System.out.println("\nEnter port: ");
            while (true) {
                try {
                    port = in.nextInt();
                    break;
                }
                catch (Exception e) {
                    System.out.println("Incorrect input. Please, try again");
                    in.next();
                }
            }
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

        // correct server shutdown
        Runtime.getRuntime().addShutdownHook(new Thread(() -> nhs.shutdown()));
    }

    // returns false is string is null or its' length is 0, true otherwise
    private static boolean isNotNullOrEmpty(String s) {
        return (s != null) && !(s.isEmpty());
    }
}