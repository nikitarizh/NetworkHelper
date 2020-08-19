package server;

import java.util.Scanner;

public class InputListener {
    Scanner in = new Scanner(System.in);

    public InputListener(NetworkHelperServer nh) {
        printInfo();
        new Thread(new Runnable() {
            public void run() {
                int cmd = in.nextInt();
                while (cmd != 0) {
                    switch (cmd) {
                        case 1: 
                            nh.printHosts();
                            cmd = in.nextInt();
                            break;
                        case 2:
                            if (nh.isScanning) {
                                System.out.println("System is currently performing a scan. Please, try again later");
                                cmd = in.nextInt();
                                break;
                            }
                            nh.userScan = true;
                            System.out.println("\nEnter subnet (e. g. 192.168.1): ");
                            String subnet = in.next();
                            nh.checkHosts(subnet, 5000);
                            cmd = in.nextInt();
                            break;
                        default:
                            System.out.println("Invalid command");
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