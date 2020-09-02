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
        
        // initialize subnet, port and other config; set defaults for config
        String subnet = "";
        int port = 0;
        boolean logRequests = true;
        boolean logPing = true;
        boolean logConnections = true;
        boolean logDisconnections = true;
        boolean logChanges = true;

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
                    logRequests = Boolean.parseBoolean(props.getProperty("logRequests"));
                    logPing = Boolean.parseBoolean(props.getProperty("logPing"));
                    logConnections = Boolean.parseBoolean(props.getProperty("logConnections"));
                    logDisconnections = Boolean.parseBoolean(props.getProperty("logDisconnections"));
                    logChanges = Boolean.parseBoolean(props.getProperty("logChanges"));
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
        final NetworkHelperServer nhs = new NetworkHelperServer(subnet, port, logRequests, logPing, logConnections, logDisconnections, logChanges);
        
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