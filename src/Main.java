import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;

import java.io.IOException;

import java.util.*;

public class Main {
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
}