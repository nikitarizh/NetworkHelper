import java.io.IOException;
import java.util.*;

public class Main {
    public static void main(String[] args) throws IOException {
        System.setProperty("java.net.preferIPv4Stack" , "true");
        
        final NetworkHelper nh = new NetworkHelper();

        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                nh.checkHosts("192.168.100", 5000);
                try {
                    Thread.currentThread().join(9999);
                }
                catch (InterruptedException e) {

                }
            }
        }, (long) 0, (long) 10000);

    }
}