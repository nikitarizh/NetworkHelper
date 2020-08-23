package client;

import java.net.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.*;


public class NetworkHelperClient {

    private String serverIp;
    private int port;
    private String location;
    private Socket socket;
    private DataInputStream din;
    private DataOutputStream dout;
    private RequestListener requestListener;

    public NetworkHelperClient(String ip, int p, String loc) throws Exception {
        // set config
        serverIp = ip;
        port = p;
        location = loc;

        // set new ClientThread
        new ClientThread().start();

        // set new user input listener
        requestListener = new RequestListener();
        requestListener.start();

        // set server response listener
        new ResponseListener();

    }

    // API: closes connection
    public void closeConnection() {
        requestListener.closeConnection();
    }

    // ClientThread class
    // sets connection to server
    private class ClientThread extends Thread {

        public void run() {
            try {
                socket = new Socket(serverIp, port);
                din = getDIN();
                dout = getDOUT();


                dout.writeUTF("__SYSTEM__-location-" + location);
                dout.flush();
            }
            catch (Exception e) {
                System.out.println("Error creating ClientThread");
                System.out.println(e.getMessage());
                System.exit(0);
            }
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

    // ResponseListener class
    // listens to server responses
    private class ResponseListener {
        public ResponseListener() {
            ScheduledExecutorService executor = Executors.newScheduledThreadPool(10);
            // try to read InputStream every 500ms
            executor.scheduleAtFixedRate(() -> {
                try {
                    if (din.available() > 0) {
                        // read response
                        String res = din.readUTF();
                        // process responses
                        //  1 - print online hosts
                        if (res.charAt(0) == '1') {
                            // remove response code
                            res.substring(res.indexOf(';') + 1);
                            // ';' is ip separator
                            String[] ips = res.split(";");

                            // display this machine ip
                            String currIp = InetAddress.getLocalHost().getHostAddress();
                            for (int i = 0; i < ips.length; i++) {
                                System.out.println(ips[i].split("-")[0] + " ---- " + currIp);
                                if (ips[i].split("-")[0].equals(currIp)) {
                                    ips[i] += " (THIS MACHINE)";
                                }
                            }

                            // print result
                            System.out.println("\n------ONLINE HOSTS:------\n");

                            for (int i = 0; i < ips.length; i++) {
                                String[] ipWithLocation = ips[i].split("-");
                                System.out.println(ipWithLocation[0] + " (" + ipWithLocation[1] + ")");
                            }
                            System.out.println("\n-----------***-----------\n");
                        }
                        else {
                            System.out.println("SERVER: " + res);
                        }
                    }
                }
                catch (Exception e) {};

            }, 0, 500, TimeUnit.MILLISECONDS);
        }
    }

    // RequestListener class
    // listens to user input
    private class RequestListener extends Thread {
        public void run() {
            Scanner in = new Scanner(System.in);
            int req = in.nextInt();
            while (req != 0) {
                switch (req) {
                    case 1: 
                        getOnlineHosts();
                        req = in.nextInt();
                        break;
                    case 0:
                        closeConnection();
                        break;
                    default:
                        System.out.println("Incorrect command");
                        req = in.nextInt();
                        break;
                }
            }
            in.close();
            System.exit(0);
        }

        private void reconnect() {
            try {
                System.out.println("Trying to reconnect...");
                socket = new Socket(serverIp, port);
                System.out.println("Connection restored");
            }
            catch(Exception e1) {
                System.out.println("Reconnection to server failed");
                System.exit(0);
            }
        }

        private void getOnlineHosts() {
            try {
                dout.writeInt(1);
                dout.flush();
            }
            catch (Exception e) {
                System.out.println("Connection lost");
                reconnect();
            }

        }

        public void closeConnection() {
            try {
                dout.writeInt(0);
                dout.flush();
            }
            catch (Exception e) {
                System.out.println("Connection lost");
                reconnect();
            }
        }
    }
    
}