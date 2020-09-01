package server;

import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;

public class Logger {

    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_BLACK = "\u001B[30m";
    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_YELLOW = "\u001B[33m";
    private static final String ANSI_BLUE = "\u001B[34m";
    private static final String ANSI_PURPLE = "\u001B[35m";
    private static final String ANSI_CYAN = "\u001B[36m";
    private static final String ANSI_WHITE = "\u001B[37m";

    private static final String ANSI_BLACK_BACKGROUND = "\u001B[40m";
    private static final String ANSI_RED_BACKGROUND = "\u001B[41m";
    private static final String ANSI_GREEN_BACKGROUND = "\u001B[42m";
    private static final String ANSI_YELLOW_BACKGROUND = "\u001B[43m";
    private static final String ANSI_BLUE_BACKGROUND = "\u001B[44m";
    private static final String ANSI_PURPLE_BACKGROUND = "\u001B[45m";
    private static final String ANSI_CYAN_BACKGROUND = "\u001B[46m";
    private static final String ANSI_WHITE_BACKGROUND = "\u001B[47m";

    // logs connections and disconnections
    public static void logChange(String ip, boolean state) {
        
        if (state) {
            System.out.println(ANSI_GREEN + "New host " + ip + " detected (" + getCurrentDate() + ")" + ANSI_RESET);
        }
        else {
            System.out.println(ANSI_RED + "Host " + ip + " is no longer reachable (" + getCurrentDate() + ")" + ANSI_RESET);
        }
    }

    public static void logConnection(String ip, String location) {
        System.out.println(ANSI_BLUE + "\nHost " + ip + " (location: " + location + ") connected" + ANSI_RESET);
    }

    public static void logDisconnection(String ip, String location) {
        System.out.println(ANSI_PURPLE + "\nHost " + ip + " (location: " + location + ") disconnected" + ANSI_RESET);
    }

    public static void logRequestAccepted(String ip, String request) {
        System.out.println(ANSI_CYAN + "\nHost " + ip + " requested " + request + "(" + getCurrentDate() + ")" + ANSI_RESET);
    }

    public static void logRequestFinished(String ip, String request) {
        System.out.println(ANSI_CYAN + "Request " + request + " of host " + ip + " has been processed (" + getCurrentDate() + ")" + ANSI_RESET);
    }

    public static void logRed(String message) {
        System.out.println(ANSI_RED + "\n" + message + ANSI_RESET);
    }

    public static void logYellow(String message) {
        System.out.println(ANSI_YELLOW + "\n" + message + ANSI_RESET);
    }

    public static void logSuccess(String message) {
        System.out.println(ANSI_GREEN + "\n" + message + ANSI_RESET);
    }

    public static void logWarning(String message) {
        System.out.println(ANSI_YELLOW + "\n" + message + ANSI_RESET);
    }

    public static void logError(String message) {
        System.out.println(ANSI_RED + "\n" + message + ANSI_RESET);
    }

    // prints message + current date
    public static void log(String message) {
        System.out.println(message + " (" + getCurrentDate() + ")");
    }

    // prints message
    public static void report(String message) {
        System.out.println("\n" + message);
    }

    // returns current date
    private static String getCurrentDate() {
        return DateTimeFormatter.ofPattern("HH:mm:ss dd/MM/yyyy").format(LocalDateTime.now());
    }
}