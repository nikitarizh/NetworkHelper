import java.io.IOException;
import java.net.*;
import java.util.*;

import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;

class NetworkHelper {
    private Vector<String> ips;
    public static void main(String[] args) throws IOException {
        System.setProperty("java.net.preferIPv4Stack" , "true");
        
        final NetworkHelper nh = new NetworkHelper();

        final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss dd/MM/yyyy");  
        LocalDateTime now = LocalDateTime.now();  
        System.out.println(dtf.format(now));  

        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                System.out.println("\n------SCAN " + dtf.format(LocalDateTime.now()) + "------\n");
                nh.checkHosts("192.168.100", 5000);
                try {
                    Thread.currentThread().join(9999);
                }
                catch (InterruptedException e) {

                }
                System.out.println("\n------SCAN FINISHED------\n");
            }
        }, (long) 0, (long) 10000);

    }

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
                                System.out.println("New host " + host + " detected");
                                ips.add(host);
                            }
                        }
                        else {
                            if (ips.contains(host)) {
                                System.out.println("Host " + host + " is no longer reachable");
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

    public static String getIP() {
        return null;
    }
}