import java.util.Scanner;

public class InputListener {
    Scanner in = new Scanner(System.in);

    public InputListener(NetworkHelper nh) {
        printInfo();
        new Thread(new Runnable() {
            public void run() {
                int cmd = in.nextInt();
                while (cmd != 0) {
                    switch (cmd) {
                        case 1: 
                            nh.printHosts();
                            cmd = in.nextInt();
                            break;
                        default:
                            System.out.println("Invalid command");
                            printInfo();
                            cmd = in.nextInt();
                            break;
                    }
                }
                in.close();
                System.exit(0);
            }
        }).start();
    }

    private void printInfo() {
        System.out.println("\n----------***----------\n");
        System.out.println("1 - print online hosts");
        System.out.println("0 - exit");
        System.out.println("\n----------***----------\n");
    }
}