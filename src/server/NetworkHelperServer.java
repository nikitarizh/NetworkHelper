package server;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

class NetworkHelperServer {

    public boolean isScanning = false;
    public boolean userScan = false;

    private String subnet;
    private String serverIp;
    private TreeSet<String> ips;
    private HashMap<String, String> ipByLocation;
    private HashMap<String, String> locationByIp;
    
    public NetworkHelperServer(String sub, int port) {
        // set subnet and initialize HashMaps
        subnet = sub;
        ips = new TreeSet<String>();
        ipByLocation = new HashMap<String, String>();
        locationByIp = new HashMap<String, String>();
        
        try {
            // set ip of the server
            serverIp = InetAddress.getLocalHost().getHostAddress();
            // push it to HashMaps
            pushIpLocation(serverIp, "SERVER");
        }
        catch (Exception e) {}
        
        // set new connection handler
        new ConnectionHandler(port).start();
        
    }

    // API: check online hosts
    // returns true if check was successful, false otherwise (e.g. thread was interrupted)
    public boolean checkHosts(int timeout) {
        // block new requests
        isScanning = true;

        // create new ThreadPool (to synchronize scanning)
        ExecutorService es = Executors.newCachedThreadPool();

        // check all ips in subnet (/24)
        for (int i = 1; i < 255; i++) {
            // current host ip
            String host = subnet + "." + i;
            // start new thread
            es.execute(new Runnable() {
                public void run() {
                    try {
                        // trying to ping with timeout { timeout }
                        //  if host is reachable
                        if (InetAddress.getByName(host).isReachable(timeout)) {
                            // and if it's new
                            if (!ips.contains(host)) {
                                // log new connection to a network
                                Logger.logChange(host, true);
                                // and add it to online hosts list
                                ips.add(host);
                            }
                        }
                        //  if host is not reachable
                        else {
                            // if host was online
                            if (ips.contains(host)) {
                                // log disconnection
                                Logger.logChange(host, false);
                                // remove ip from online hosts list
                                ips.remove(host);
                            }
                        }
                    } 
                    catch (Exception e) {
                        // System.out.println(e.getMessage());
                    }
                }
            });
        }

        // shutdown threads
        es.shutdown();
        boolean finished = false;
        try {
            // await their termination
            finished = es.awaitTermination(10, TimeUnit.SECONDS);
        }
        catch (InterruptedException e) {}

        userScan = false;
        isScanning = false;
        
        return finished;
    }

    // API: check particular host
    public void checkHost(String ip, int timeout) {
        System.out.println("Trying to ping " + ip);
        try {
            if (InetAddress.getByName(ip).isReachable(timeout)) {
                System.out.println("Host " + ip + " is reachable");
            }
            else {
                System.out.println("Host " + ip + " is NOT reachable");
            }
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    // API: returns server ip
    public String getIp() {
        return serverIp;
    }

    // API: returns list of online hosts
    public TreeSet<String> getHosts() {
        if (!isScanning) {
            Logger.log("    Initiating scan... (getHosts)");
            checkHosts(5000);
            Logger.log("    Scan finished (getHosts)");
        }
        else {
            Logger.report("    System is currently performing a scan, waiting... (getHosts)");
            try {
                Thread.sleep(5100);
            } catch (Exception e) {}
        }
        return ips;
    }

    // API: returns HashMap with key: ip, value: location
    public HashMap<String, String> getLocationByIp() {
        return locationByIp;
    }

    // ConnectionHandler class
    // handles new connections to the server
    private class ConnectionHandler extends Thread {
        private int port;

        public ConnectionHandler(int p) {
            port = p;
        }

        public void run() {
            ServerSocket ss = null;
            Socket s = null;
            try {
                ss = new ServerSocket(port);
            }
            catch (Exception e) {
                System.out.println("Server socket creation failed");
                System.exit(0);
            }
            
            // handle new connection
            while (true) {
                try {
                    s = ss.accept();
                }
                catch (Exception e) {}

                // start a new thread that will handle Client requests
                new ServerThread(s).start();
            }
        }
    }

    // ServerThread class
    // handles Client requests
    private class ServerThread extends Thread {
        private Socket socket;

        public ServerThread(Socket clientSocket) {
            socket = clientSocket;
        }

        public void run() {

            // set input and output streams
            final DataInputStream din = getDIN();
            final DataOutputStream dout = getDOUT();

            // handle new connection
            try {
                // get location and ip of client
                String location = din.readUTF().split("-")[2];
                String ip = getClientIp();

                // push them into HashMaps
                pushIpLocation(ip, location);

                // log new connection
                Logger.logConnection(ip, locationByIp.get(ip));
            }
            catch (Exception e) {
                e.printStackTrace();
            }

            // Client requests handler
            ScheduledExecutorService executor = Executors.newScheduledThreadPool(10);
            executor.scheduleAtFixedRate(() -> {
                int inp;
                String out = "";
                String request = "";
                try {
                    if (din.available() > 0) {
                        inp = din.readInt();
                        out += Integer.toString(inp) + ';';
                        // 1 - get online hosts
                        if (inp == 1) {
                            request = "1 - 'Get online hosts'";
                            Logger.logRequestAccepted(getClientIp(), request);
                            TreeSet<String> outIps = getHosts();
                            
                            for (String ip : outIps) {
                                out += ip + '-' + locationByIp.get(ip) + ';';
                            }
                        }
                        // 2 - initiate immediate scan (?)
                        else if (inp == 2) {
                            
                        }
                        // 0 - close connection
                        else if (inp == 0) {
                            String ip = getClientIp();
                            Logger.logDisconnection(ip, locationByIp.get(ip));
                            removeIpLocation(ip, locationByIp.get(ip));
                            din.close();
                            dout.close();
                            socket.close();
                            executor.shutdownNow();
                            return;
                        }
                        // other - invalid
                        else {
                            out = "Incorrect command";
                        }

                        // send response
                        dout.writeUTF(out);
                        dout.flush();

                        // log response
                        Logger.logRequestFinished(getClientIp(), request);
                    }
                }
                catch (IOException e) {
                    System.out.println("err: " + e.getMessage());
                };
            }, 0, 500, TimeUnit.MILLISECONDS);
        }

        // returns ip of client connected to current ServerThread
        private String getClientIp() {
            return ((Inet4Address)
                        ((InetSocketAddress)
                            socket.getRemoteSocketAddress())
                        .getAddress())
                    .toString();
        }

        // returns new DataInputStream of socket
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

        // returns new DataOutputStream of socket
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

    // adds ip and location to HashMaps
    private void pushIpLocation(String ip, String location) {
        ipByLocation.put(location, ip);
        locationByIp.put(ip, location);
    }

    // removes ip and location from HashMaps
    private void removeIpLocation(String ip, String location) {
        ipByLocation.remove(ip);
        locationByIp.remove(location);
    }
}