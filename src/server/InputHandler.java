package server;

import java.util.*;

public class InputHandler {
    // set new Scanner
    private Scanner in = new Scanner(System.in);

    public InputHandler(NetworkHelperServer nhs) {

        // print info about commands
        printInfo();

        // set new Thread that will listen to user input
        new Thread(new Runnable() {
            public void run() {
                // set command code
                int cmd = in.nextInt();
                // while cmd is not exit code
                while (cmd != 0) {
                    switch (cmd) {
                        // 1 - print online hosts
                        case 1: 
                            TreeSet<String> ips = nhs.getHosts();
                            HashMap<String, String> locations = nhs.getLocationByIp();
                            String serverIp = nhs.getIp();
                            System.out.println("\n------ONLINE HOSTS------\n");

                            for (String ip : ips) {
                                if (ip.equals(serverIp)) {
                                    System.out.println(ip + " (SERVER)");
                                }
                                else {
                                    System.out.println(ip + 
                                                        (!(locations.get(ip) == null) 
                                                            ? (" (" + locations.get(ip) + ")") 
                                                            : " (unknown)")
                                                        );
                                }
                            }
                            System.out.println("\n-----------***----------");
                            break;
                        // 2 - initiate immediate scan
                        case 2:
                            // if now there is a scheduled scan
                            if (nhs.isScanning) {
                                // tell user to try again later
                                Logger.report("System is currently performing a scan. Please, try again later");
                                // and listen to his input
                                cmd = in.nextInt();
                                break;
                            }

                            // otherwise, initiate new scan
                            Logger.log("Initiating scan...");
                            nhs.userScan = true;
                            nhs.checkHosts(5000);
                            Logger.log("Scan finished");
                            break;
                        // system requests
                        //  update all clients
                        case 900: 
                            nhs.updateClients();
                            break;
                        // other - invalid
                        default:
                            Logger.report("Invalid command");
                            printInfo();
                            break;
                    }
                    cmd = in.nextInt();
                }
                // when input is exit code, close client
                System.exit(0);
            }
        }).start();
    }

    // print info about commands
    private void printInfo() {
        System.out.println("\n----------***----------\n");
        System.out.println("1 - print online hosts");
        System.out.println("2 - perform immediate scan");
        System.out.println("SYSTEM REQUESTS:");
        System.out.println("900 - update all clients");
        System.out.println("0 - exit");
        System.out.println("\n----------***----------\n");
    }
}