import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;

public class ChangeListener {

    public static void logChange(String ip, boolean state) {
        if (state) {
            System.out.println("New host " + ip + " detected (" + getCurrentDate() + ")");
        }
        else {
            System.out.println("Host " + ip + " is no longer reachable (" + getCurrentDate() + ")");
        }
    }

    private static String getCurrentDate() {
        return DateTimeFormatter.ofPattern("HH:mm:ss dd/MM/yyyy").format(LocalDateTime.now());
    }
}