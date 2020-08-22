package server;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

class NetworkHelperServer {

    private String subnet;
    private String serverIp;
    private TreeSet<String> ips;
    private HashMap<String, String> ipByLocation;
    private HashMap<String, String> locationByIp;

    private Vector<Thread> threads;

    public boolean isScanning = false;
    public boolean userScan = false;

    public NetworkHelperServer(String sub, int port) {
        subnet = sub;
        ips = new TreeSet<String>();
        ipByLocation = new HashMap<String, String>();
        locationByIp = new HashMap<String, String>();
        threads = new Vector<Thread>();
        
        try {
            serverIp = InetAddress.getLocalHost().getHostAddress();
            pushIpLocation(serverIp, "SERVER");
        }
        catch (Exception e) {}
        
        new ConnectionHandler(port).start();
        
    }

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
            
            while (true) {
                try {
                    s = ss.accept();
                }
                catch (Exception e) {}

                new ServerThread(s).start();
            }
        }
    }

    private class ServerThread extends Thread {
        private Socket socket;

        public ServerThread(Socket clientSocket) {
            socket = clientSocket;
        }

        public void run() {

            final DataInputStream din = getDIN();
            final DataOutputStream dout = getDOUT();

            try {
                String location = din.readUTF().split("-")[2];
                String ip = getClientIp();

                pushIpLocation(ip, location);

                Logger.log("Host " + ip + " (location: " + locationByIp.get(ip) + ") connected");
            }
            catch (Exception e) {
                e.printStackTrace();
            }

            ScheduledExecutorService executor = Executors.newScheduledThreadPool(10);
            executor.scheduleAtFixedRate(() -> {
                int inp;
                String out = "";
                try {
                    if (din.available() > 0) {
                        inp = din.readInt();
                        out += Integer.toString(inp) + ';';
                        if (inp == 1) {
                            TreeSet<String> outIps = getHosts();

                            for (String ip : outIps) {
                                out += ip + '-' + locationByIp.get(ip) + ';';
                            }
                        }
                        else if (inp == 2) {
                            
                        }
                        else if (inp == 0) {
                            String ip = getClientIp();
                            Logger.log("Host " + ip + " (location: " + locationByIp.get(ip) + ") disconnected");
                            removeIpLocation(ip, locationByIp.get(ip));
                            socket.close();
                            executor.shutdownNow();
                            return;
                        }
                        else {
                            out = "Incorrect command";
                        }

                        dout.writeUTF(out);
                        dout.flush();
                    }
                }
                catch (IOException e) {
                    System.out.println("err: " + e.getMessage());
                };
            }, 0, 500, TimeUnit.MILLISECONDS);
        }

        private String getClientIp() {
            return ((Inet4Address)
                        ((InetSocketAddress)
                            socket.getRemoteSocketAddress())
                        .getAddress())
                    .toString();
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

    public boolean checkHosts(int timeout) {
        isScanning = true;

        ExecutorService es = Executors.newCachedThreadPool();

        for (int i = 1; i < 255; i++) {
            String host = subnet + "." + i;
            es.execute(new Runnable() {
                public void run() {
                    try {
                        if (InetAddress.getByName(host).isReachable(timeout)) {
                            if (!ips.contains(host)) {
                                Logger.logChange(host, true);
                                ips.add(host);
                            }
                        }
                        else {
                            if (ips.contains(host)) {
                                Logger.logChange(host, false);
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

        es.shutdown();
        boolean finished = false;
        try {
            finished = es.awaitTermination(10, TimeUnit.SECONDS);
        }
        catch (InterruptedException e) {}

        userScan = false;
        isScanning = false;
        
        return finished;
    }

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

    public TreeSet<String> getHosts() {
        if (!isScanning) {
            Logger.log("Initiating scan... (getHosts)");
            checkHosts(5000);
            Logger.log("Scan finished (getHosts)");
        }
        else {
            Logger.report("System is currently performing a scan, waiting... (getHosts)");
            try {
                Thread.sleep(5100);
            } catch (Exception e) {}
        }
        return ips;
    }

    private void pushIpLocation(String ip, String location) {
        ipByLocation.put(location, ip);
        locationByIp.put(ip, location);
    }

    private void removeIpLocation(String ip, String location) {
        ipByLocation.remove(ip);
        locationByIp.remove(location);
    }

    public HashMap<String, String> getLocationByIp() {
        return locationByIp;
    }

    public String getIp() {
        return serverIp;
    }


}