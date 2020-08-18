import java.net.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

class NetworkHelper {

    private Vector<String> ips;

    public boolean isScanning = false;
    public boolean userScan = false;

    public NetworkHelper() {
        ips = new Vector<String>();
    }

    public boolean checkHosts(String subnet, int timeout) {
        isScanning = true;

        ExecutorService es = Executors.newCachedThreadPool();

        for (int i = 1; i < 255; i++) {
            String host = subnet + "." + i;
            es.execute(new Runnable() {
                public void run() {
                    try {
                        if (InetAddress.getByName(host).isReachable(timeout)) {
                            if (!ips.contains(host)) {
                                ChangeListener.logChange(host, true);
                                ips.add(host);
                            }
                        }
                        else {
                            if (ips.contains(host)) {
                                ChangeListener.logChange(host, false);
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

    public void printHosts() {
        System.out.println("\n------ONLINE HOSTS:------\n");
        for (String string : ips) {
            System.out.println(string);
        }
        System.out.println("\n-----------***-----------\n");
    }
}