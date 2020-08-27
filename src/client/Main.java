package client;

import java.net.InetAddress;
import java.util.*;
import java.io.*;

public class Main {

    public static void main(String[] args) {

        Scanner in = new Scanner(System.in);

        String ip = "";
        int port = 0;
        String location = "";

        // try to find config file
        File configFile = new File("clientConfig.properties");
        try {
            FileReader reader = new FileReader(configFile);
            Properties props = new Properties();
            props.load(reader);
        
            String fileIp = props.getProperty("serverIp");
            String filePort = props.getProperty("port");
            String fileLocation = props.getProperty("location");
        
            reader.close();

            if (isNotNullOrEmpty(fileIp) && isNotNullOrEmpty(filePort) && isNotNullOrEmpty(fileLocation)) {
                System.out.println("Found a config file. Do you want to load parameters from it? y/n");
                char inp = in.next().charAt(0);
                if (inp == 'y' || inp == 'Y') {
                    ip = fileIp;
                    port = Integer.parseInt(filePort);
                    location = fileLocation;
                }
            }
        }
        catch (Exception e) {}

        // enter parameters (if no config found or user wanted to enter manually)
        if (ip.equals("") || port == 0 || location.equals("")) {
            // enter parameters
            System.out.println("\nEnter server address: ");
            while (true) {
                try {
                    ip = in.next();
                    break;
                }
                catch (Exception e) {
                    System.out.println("Incorrect input. Please, try again");
                    in.next();
                }
            }
            System.out.println("\nEnter port:");
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
            System.out.println("\nEnter location:");
            while (true) {
                try {
                    location = in.next();
                    break;
                }
                catch (Exception e) {
                    System.out.println("Incorrect input. Please, try again");
                    in.next();
                }
            }
        }

        // initialize client
        NetworkHelperClient nhc = createClient(ip, port, location);
        if (nhc == null) {
            System.out.println("Client creation failed");
            System.exit(0);
        }

        // shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(() -> nhc.closeConnection()));
    }

    // returns new NetworkHelperClient
    private static NetworkHelperClient createClient(String ip, int port, String location) {

        try {
            // check if server is reachable
            if (!InetAddress.getByName(ip).isReachable(1000)) {
                if (!InetAddress.getByName(ip).isReachable(5000)) {
                    System.out.println("Server is not reachable");
                    System.exit(0);
                }
            }

            // if yes, return Client instance
            return new NetworkHelperClient(ip, port, location);
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return null;
    }

    private static boolean isNotNullOrEmpty(String s) {
        return (s != null) && !(s.isEmpty());
    }
}