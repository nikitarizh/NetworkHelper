package client;

import java.net.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.*;


public class NetworkHelperClient {

    public NetworkHelperClient(String serverIp, int port, String cabinet) throws Exception {
        new ClientThread(serverIp, port, cabinet).start();
    }

    private class ClientThread extends Thread {

        private String serverIp;
        private int port;
        private String cabinet;
        private Socket socket;

        public ClientThread(String ip, int p, String cab) {
            serverIp = ip;
            port = p;
            cabinet = cab;
        }

        public void run() {
            try {
                init();
            }
            catch (Exception e) {
                System.out.println("Error creating ClientThread");
                System.out.println(e.getMessage());
                System.exit(0);
            }
        }

        public void init() throws Exception {
            socket = new Socket(serverIp, port);

            final DataInputStream din = getDIN();
            final DataOutputStream dout = getDOUT();
            final Scanner scanner = new Scanner(System.in);

            // dout.writeBytes("__:system:__-cab-" + cabinet);
            // dout.flush();

            ScheduledExecutorService executor = Executors.newScheduledThreadPool(10);
            executor.scheduleAtFixedRate(() -> {
                int s1 = -1;
                String s2 = "";
                try {
                    s1 = scanner.nextInt();
                    if (s1 == 0) {
                        System.exit(0);
                    }
    
                    dout.writeInt(s1);
                    dout.flush();
                    s2 = din.readUTF();
    
                    if (s1 == 1) {
                        String[] ips = s2.split(";");
    
                        String currIp = InetAddress.getLocalHost().getHostAddress();
                        for (int i = 0; i < ips.length; i++) {
                            if (ips[i].equals(currIp)) {
                                ips[i] += " (THIS MACHINE)";
                            }
                            else if (ips[i].equals(serverIp)) {
                                ips[i] += " (SERVER)";
                            }
                        }
    
                        System.out.println("\n------ONLINE HOSTS:------\n");
    
                        for (int i = 0; i < ips.length; i++) {
                            System.out.println(ips[i]);
                        }
                        System.out.println("\n-----------***-----------\n");
                    }
                    else {
                        System.out.println("SERVER: " + s2);
                    }
                }
                catch (Exception e) {
                    System.out.println("Error: " + e.getCause());
                };

                s2 = "";

            }, 0, 500, TimeUnit.MILLISECONDS);
        }

        private DataInputStream getDIN() {
            try {
                return new DataInputStream(socket.getInputStream());
            }
            catch (Exception e) {
                System.out.println("Creating DIN failed");
                System.exit(0);
            }
            return null;
        }

        private DataOutputStream getDOUT() {
            try {
                return new DataOutputStream(socket.getOutputStream());
            }
            catch (Exception e) {
                System.out.println("Creating DOUT failed");
                System.exit(0);
            }
            return null;
        }
    }
    
}