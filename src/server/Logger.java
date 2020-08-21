package server;

import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;

public class Logger {

    public static void logChange(String ip, boolean state) {
        if (state) {
            System.out.println("New host " + ip + " detected (" + getCurrentDate() + ")");
        }
        else {
            System.out.println("Host " + ip + " is no longer reachable (" + getCurrentDate() + ")");
        }
    }

    public static void log(String message) {
        System.out.println(message + " (" + getCurrentDate() + ")");
    }

    public static void report(String message) {
        System.out.println(message);
    }

    private static String getCurrentDate() {
        return DateTimeFormatter.ofPattern("HH:mm:ss dd/MM/yyyy").format(LocalDateTime.now());
    }
}