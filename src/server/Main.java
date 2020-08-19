package server;

import java.io.IOException;
import java.util.*;

public class Main {
    public static void main(String[] args) throws IOException {
        System.setProperty("java.net.preferIPv4Stack" , "true");        
        final NetworkHelperServer nh = new NetworkHelperServer();
        
        new InputListener(nh);

        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (!nh.userScan) {
                    nh.checkHosts("192.168.100", 5000);
                }
            }
        }, (long) 0, (long) 10000);

    }
}