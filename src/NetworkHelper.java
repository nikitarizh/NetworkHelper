import java.net.*;
import java.util.*;

class NetworkHelper {

    private Vector<String> ips;

    public NetworkHelper() {
        ips = new Vector<String>();
    }

    public void checkHosts(String subnet, int timeout) {
        
        for (int i = 1; i < 255; i++) {
            String host = subnet + "." + i;
            new Thread(new Runnable() {
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
                    } catch (Exception e) {
                        // System.out.println(e.getMessage());
                    }
                }
            }).start();
            
        }
    }

    public static void checkHost(String ip, int timeout) {
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
}