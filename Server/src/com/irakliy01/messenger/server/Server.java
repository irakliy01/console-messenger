package com.irakliy01.messenger.server;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Logger;

public class Server {

    private static int port;
    private static String programUsageString = "java ... <port>\nwhere <port> is port number between 1025–65535";
    private static Logger LOGGER = Logger.getLogger(Server.class.getName());

    public static void main(String[] args) {

        Scanner console = new Scanner(System.in); // Stream for input from console

        if (args.length == 1) {
            setPort(args[0]);
        } else {
            System.out.print("Type port: ");
            setPort(console.nextLine());
        }

        clearConsole(); // tries to clear console
        console.close();

        try (ServerSocket serverSocket = new ServerSocket(port); BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in))) {

            System.out.println("SERVER HAS BEEN ESTABLISHED\n\nLocal IPs:");
            showLocalIPs();
            System.out.println("\nPort: " + port);

        } catch (IOException e) {
            LOGGER.severe(e.getMessage());
        }

    }

    private static void showLocalIPs() {

        List<String> addresses = IPsGetter.getLocalAdresses();

        for (String ip : addresses) {
            System.out.println(ip);
        }

    }


    private static void setPort(String port) {

        int tmp = Integer.parseInt(port.trim());
        if (tmp >= 1025 && tmp <= 65535 || tmp == 0)
            Server.port = tmp;
        else {
            System.err.println("Parameter format error! Notice that parameter should be a number\n" +
                    "Usage of the program:\n".concat(programUsageString) +
                    "\nif you type '0' as port, then server will listen on any free port");
            System.exit(1);
        }
    }

    private static void clearConsole() {
        System.out.println();
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }


}
