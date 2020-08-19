package client;

import java.net.InetAddress;
import java.util.*;

public class Main {

    public static void main(String[] args) {

        Scanner in = new Scanner(System.in);

        String ip = "";
        int port = 0;
        String cab = "";

        System.out.println("\nEnter server address: ");
        try {
            ip = in.next();
        }
        catch (Exception e) {
            // Set default ip
        }
        System.out.println("\nEnter port:");
        try {
            port = in.nextInt();
        }
        catch (Exception e) {
            // Set default port
        }
        System.out.println("\nEnter cabinet:");
        try {
            cab = in.next();
        }
        catch (Exception e) {}
        in.close();

        NetworkHelperClient nhc = createClient(ip, port, cab);
        if (nhc == null) {
            System.out.println("Client creation failed");
            System.exit(0);
        }
        else {
            System.out.println("\nConnection successful\n");
        }
    }

    private static NetworkHelperClient createClient(String ip, int port, String cab) {

        try {
            if (!InetAddress.getByName(ip).isReachable(1000)) {
                if (!InetAddress.getByName(ip).isReachable(5000)) {
                    System.out.println("Server is not reachable");
                    System.exit(0);
                }
            }

            return new NetworkHelperClient(ip, port, cab);
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return null;
    }
}