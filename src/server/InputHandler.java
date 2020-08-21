package server;

import java.util.*;

public class InputHandler {
    Scanner in = new Scanner(System.in);

    public InputHandler(NetworkHelperServer nhs) {
        printInfo();
        new Thread(new Runnable() {
            public void run() {
                int cmd = in.nextInt();
                while (cmd != 0) {
                    switch (cmd) {
                        case 1: 
                            TreeSet<String> ips = nhs.getHosts();
                            String serverIp = nhs.getIp();
                            System.out.println("\n------ONLINE HOSTS------\n");

                            for (String ip : ips) {
                                if (ip.equals(serverIp)) {
                                    System.out.println(ip + " (SERVER)");
                                }
                                else {
                                    System.out.println(ip);
                                }
                            }
                            System.out.println("\n-----------***----------");
                            cmd = in.nextInt();
                            break;
                        case 2:
                            if (nhs.isScanning) {
                                Logger.report("System is currently performing a scan. Please, try again later");
                                cmd = in.nextInt();
                                break;
                            }
                            Logger.log("Initiating scan...");
                            nhs.userScan = true;
                            nhs.checkHosts(5000);
                            Logger.log("Scan finished");

                            cmd = in.nextInt();
                            break;
                        default:
                            Logger.report("Invalid command");
                            printInfo();
                            cmd = in.nextInt();
                            break;
                    }
                }
                in.close();
                System.exit(0);
            }
        }).start();
    }

    private void printInfo() {
        System.out.println("\n----------***----------\n");
        System.out.println("1 - print online hosts");
        System.out.println("2 - perform immediate scan");
        System.out.println("0 - exit");
        System.out.println("\n----------***----------\n");
    }
}