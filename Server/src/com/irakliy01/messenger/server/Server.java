package com.irakliy01.messenger.server;


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

public class Server {

    private static int port;
    private static ExecutorService executeIt = Executors.newFixedThreadPool(2); // Number of connections that server can handle
    private static String programUsageString = "java ... <port>\nwhere <port> is port number between 1025–65535\nif you type '0' as port than server will listen on any free port";
    private static Logger LOGGER = Logger.getLogger(Server.class.getName());

    public static void main(String[] args) {

        Scanner consoleInput = new Scanner(System.in); // Stream for input from console

        if (args.length == 1) {
            setPort(args[0]);
        } else if (args.length > 1) {
            System.err.println("Too much arguments passed to the program\n" + programUsageString);
            System.exit(3);
        } else {
            System.out.print("Type port: ");
            setPort(consoleInput.nextLine());
        }

        clearConsole(); // tries to clear console
        consoleInput.close();

        try (ServerSocket serverSocket = new ServerSocket(port)) {

            System.out.println("SERVER HAS BEEN CREATED\n\nLocal IPs:");
            showLocalIPs();
            System.out.println("\nPort: " + serverSocket.getLocalPort() + "\n");

            while (!serverSocket.isClosed()) {

                Socket socket = serverSocket.accept();
                System.out.println("Found client. Connecting to ".concat(socket.getInetAddress().getCanonicalHostName()).concat(" [").concat(socket.getInetAddress().getHostAddress()).concat("]..."));
                executeIt.execute(new EchoThread(socket));

            }

            System.out.println("SERVER IS SHUTTING DOWN");
            executeIt.shutdown();

        } catch (IOException e) {
            LOGGER.severe(e.getMessage());
        }

    }

    private static void showLocalIPs() {

        List<String> addresses = IPsGetter.getLocalAddresses();

        for (String ip : addresses) {
            System.out.println(ip);
        }

    }


    private static void setPort(String port) {

        int tmp = 0;

        try {
            tmp = Integer.parseInt(port.trim());
        } catch (NumberFormatException ex) {
            System.err.println("Parameter format error! Notice that parameter should be a number\n" +
                    "Usage of the program:\n".concat(programUsageString));
            System.exit(2);
        }

        if (tmp >= 1025 && tmp <= 65535 || tmp == 0)
            Server.port = tmp;
        else {
            System.err.println("Parameter format error! Notice that parameter should be a number\n" +
                    "Usage of the program:\n".concat(programUsageString));
            System.exit(2);
        }
    }

    private static void clearConsole() {
        System.out.println();
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }


}
