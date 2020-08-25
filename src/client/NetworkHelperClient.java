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
    private ClientThread clientThread;
    private RequestListener requestListener;
    private ResponseListener responseListener;

    public NetworkHelperClient(String ip, int p, String loc) throws Exception {
        
        //set defaults
        clientThread = null;
        requestListener = null;
        responseListener = null;

        // set config
        serverIp = ip;
        port = p;
        location = loc;

        // connect to the server
        if (!connect()) {
            System.out.println("Couldn't connect to the server");
            System.exit(0);
        }
    }

    // API: closes connection
    public void closeConnection() {
        requestListener.closeConnection();
    }

    // ClientThread class
    // sets connection to server
    private class ClientThread extends Thread {
        public boolean threadStarted = false;
        public void run() {
            try {
                socket = new Socket(serverIp, port);
                din = getDIN();
                dout = getDOUT();

                dout.writeUTF("__SYSTEM__-location-" + location);
                dout.flush();

                threadStarted = true;
            }
            catch (Exception e) {
                threadStarted = false;
                System.out.println("Error creating ClientThread");
                System.out.println(e.getMessage());
            }
        }

        private DataInputStream getDIN() {
            try {
                return new DataInputStream(socket.getInputStream());
            }
            catch (Exception e) {
                System.out.println("Creating DIN failed");
            }
            return null;
        }

        private DataOutputStream getDOUT() {
            try {
                return new DataOutputStream(socket.getOutputStream());
            }
            catch (Exception e) {
                System.out.println("Creating DOUT failed");
            }
            return null;
        }
    }

    // ResponseListener class
    // listens to server responses
    private class ResponseListener {
        public boolean threadStarted = false;
        public boolean listenToResponses = true;
        public ResponseListener() {
            threadStarted = true;
            ScheduledExecutorService executor = Executors.newScheduledThreadPool(10);
            // try to read InputStream every 500ms
            executor.scheduleAtFixedRate(() -> {
                try {
                    if (din.available() > 0 && listenToResponses) {
                        // read response
                        String res = din.readUTF();
                        // process responses
                        //  1 - print online hosts
                        if (res.charAt(0) == '1') {
                            // remove response code
                            res = res.substring(res.indexOf(';') + 1);
                            // ';' is ip separator
                            String[] ips = res.split(";");

                            // display this machine's ip
                            String currIp = InetAddress.getLocalHost().getHostAddress();
                            for (int i = 0; i < ips.length; i++) {
                                if (ips[i].split("-")[0].equals(currIp)) {
                                    ips[i] += " (THIS MACHINE)";
                                }
                            }

                            // print result
                            System.out.println("\n------ONLINE HOSTS:------\n");

                            for (int i = 0; i < ips.length; i++) {
                                String[] ipWithLocation = ips[i].split("-");
                                if (ipWithLocation[1].equals("null")) {
                                    System.out.println(ipWithLocation[0] + " (unknown)");
                                }
                                else {
                                    System.out.println(ipWithLocation[0] + " (" + ipWithLocation[1] + ")");
                                }
                            }
                            System.out.println("\n-----------***-----------\n");
                        }
                        // process system responses
                        else if (res.startsWith("__SYSTEM__")) {
                            String[] parsedSystem = res.split("-");
                            String command = parsedSystem[1];
                            if (command.equals("disconnect")) {
                                System.out.println("Server closed connection");
                                System.exit(0);
                            }
                        }
                        else {
                            System.out.println("SERVER: " + res);
                        }
                    }
                }
                catch (Exception e) {}

            }, 0, 500, TimeUnit.MILLISECONDS);
        }
    }

    // RequestListener class
    // listens to user input
    private class RequestListener extends Thread {
        public boolean threadStarted = false;

        public void run() {
            threadStarted = true;
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
            System.exit(0);
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
                socket.close();
            }
            catch (Exception e) {}
        }
    }

    // connects to Server
    // returns true if connected, false if no or error occured
    private boolean connect() {
        // set new ClientThread
        clientThread = null;
        clientThread = new ClientThread();
        clientThread.start();

        // set new user input listener
        requestListener = null;
        requestListener = new RequestListener();
        requestListener.start();

        // set server response listener
        responseListener = null;
        responseListener = new ResponseListener(); 

        try {
            Thread.sleep(1000);
        } catch (Exception e) {}
        

        return clientThread.threadStarted && requestListener.threadStarted && responseListener.threadStarted;
    }

    // tries to reconnect
    // returns true if reconnected, false if no or error occured
    private boolean reconnect() {
        System.out.println("Trying to reconnect...");
        return connect();
    }
}