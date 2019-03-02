package com.irakliy01.messenger.server;


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

import static com.irakliy01.messenger.lib.Writer.writeMessage;

/**
 * <p>Main class of the module Server. It contains only private static methods and one public method (main)</p>
 * <p>It starts server on some port and listens for incoming connections</p>
 *
 * @author irakliy01
 * @version 14/10/2018
 */
public class Server {

    private static int port;
    private static ExecutorService executeIt = Executors.newFixedThreadPool(2); // Number of connections that server can handle
    private static String programUsageString = "java ... <port>\nwhere <port> is port number between 1025–65535\nif you type '0' as port than server will listen on any free port";
    private static Logger LOGGER = Logger.getLogger(Server.class.getName());

    /**
     * Main method of class Server. Starts server and listens for connections
     *
     * @param args port that user writes in console. If user did not provide port in console, he will be asked to do it later. If user writes more than one argument than program will write an error message and close.
     */
    public static void main(String[] args) {

        Scanner consoleInput = new Scanner(System.in); // Stream for input from console

        if (args.length == 1) {
            setPort(args[0]);
        } else if (args.length > 1) {
            System.err.println("Too much arguments passed to the program\n" + programUsageString);
            System.exit(-1);
        } else {
            System.out.print("Type port: ");
            setPort(consoleInput.nextLine());
        }

        clearConsole(); // tries to clear console
        consoleInput.close();

        try (ServerSocket serverSocket = new ServerSocket(port)) {

            writeMessage("SERVER HAS BEEN CREATED\n\nLocal IPs:");
            showLocalIPs();
            System.out.println("\nPort: " + serverSocket.getLocalPort() + "\n");

            while (!serverSocket.isClosed()) {

                Socket socket = serverSocket.accept();
                writeMessage("Found client. Connecting to ".concat(socket.getInetAddress().getCanonicalHostName()).concat(" [").concat(socket.getInetAddress().getHostAddress()).concat("]..."));
                executeIt.execute(new EchoThread(socket));

            }

            writeMessage("SERVER IS SHUTTING DOWN");
            executeIt.shutdown();

        } catch (IOException e) {
            LOGGER.severe(e.getMessage());
        }

    }

    /**
     * Prints at console all local IP
     */
    private static void showLocalIPs() {

        List<String> addresses = IPsGetter.getLocalAddresses();

        for (String ip : addresses) {
            System.out.println(ip);
        }

    }

    /**
     * Sets server port
     *
     * @param port port
     */
    private static void setPort(String port) {

        int tmp = 0;

        try {
            tmp = Integer.parseInt(port.trim());
        } catch (NumberFormatException ex) {
            System.err.println("Parameter format error! Notice that parameter should be a number\n" +
                    "Usage of the program:\n".concat(programUsageString));
            System.exit(-1);
        }

        if (tmp >= 1025 && tmp <= 65535 || tmp == 0)
            Server.port = tmp;
        else {
            System.err.println("Parameter format error! Notice that parameter should be a number\n" +
                    "Usage of the program:\n".concat(programUsageString));
            System.exit(-1);
            // TODO: change LOGGER messages to serr where it should be used
        }
    }


    /**
     * Clears console
     */
    private static void clearConsole() {
        try {
            if (System.getProperty("os.name").contains("Windows"))
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            else
                Runtime.getRuntime().exec("clear");
        } catch (IOException | InterruptedException ex) {
            LOGGER.severe(ex.getMessage());
        }
    }


}
