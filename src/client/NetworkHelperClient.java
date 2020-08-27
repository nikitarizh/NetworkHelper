package client;

import java.net.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.*;


public class NetworkHelperClient {

    private String serverIp;
    private int port;
    private String location;
    private boolean sentPing;
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
        sentPing = false;

        // connect to the server
        if (!connect()) {
            System.out.println("Couldn't connect to the server");
            System.exit(0);
        }

        new Timer().scheduleAtFixedRate(new TimerTask() {
            public void run() {
                if (!sentPing) {
                    checkConnection();
                }
                else {
                    System.out.println("Connection lost");
                    System.out.println("Trying to reconnect...");
                    boolean reconnected = reconnect();
                    System.out.println(reconnected);
                }
            }
        }, 5000, 10000);
    }

    // API: closes connection
    public void closeConnection() {
        requestListener.closeConnection();
    }

    // API: checks connection status
    public void checkConnection() {
        try {
            dout.writeUTF("__SYSTEM__-ping");
            dout.flush();
            sentPing = true;
        }
        catch (Exception e) {};
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

        // returns DataInputStream of socket
        private DataInputStream getDIN() {
            try {
                return new DataInputStream(socket.getInputStream());
            }
            catch (Exception e) {
                System.out.println("Creating DIN failed");
            }
            return null;
        }

        // returns DataOutputStream of socket
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
                                // if location is unknown, display "unknown" instead of "null"
                                if (ipWithLocation[1].equals("null")) {
                                    System.out.println(ipWithLocation[0] + " (unknown)");
                                }
                                // display the location if it's known
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

                            // disconnect
                            if (command.equals("disconnect")) {
                                System.out.println("Server closed connection");
                            }

                            // ping
                            else if (command.equals("ping")) {
                                sentPing = false;
                            }
                        }

                        // debug output
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

        // closes connection with Server (sends code 0; closes socket)
        public void closeConnection() {
            try {
                dout.writeUTF("0");
                dout.flush();
                socket.close();
            }
            catch (Exception e) {}
        }

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

        // requests online hosts (sends code 1)
        private void getOnlineHosts() {
            try {
                dout.writeUTF("1");
                dout.flush();
            }
            catch (Exception e) {
                System.out.println("Connection lost");
                reconnect();
            }

        }
    }

    // connects to Server
    // returns true if connected, false if no or error occured
    private boolean connect() {
        
        // resetting IO
        socket = null;
        din = null;
        dout = null;

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