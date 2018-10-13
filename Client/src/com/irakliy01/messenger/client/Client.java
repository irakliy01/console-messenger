package com.irakliy01.messenger.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.NoRouteToHostException;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;
import java.util.logging.Logger;

/**
 * <p>Main class of the module Client. It contains only private static methods and one public method (main)</p>
 * <p>It connects to the server and sends/receives messages</p>
 *
 * @author irakliy01
 * @version 14/10/2018
 */
public class Client {

    private static Logger LOGGER = Logger.getLogger(Client.class.getName());

    /**
     * Main method of class Client. Connects to the server
     *
     * @param args this parameter is not used
     */
    public static void main(String[] args) {

        Scanner console = new Scanner(System.in); // Stream for input from console

        String address;
        int port;

        System.out.print("Type address: ");
        address = console.nextLine().trim();
        if (!validIP(address)) {
            System.err.println("Unsupported IPv4 address format!");
            System.exit(-1);
        }
        System.out.print("Type port: ");
        port = Integer.parseInt(console.nextLine().trim());
        if (!validPort(port)) {
            System.err.println("Unsupported port number!");
            System.exit(-1);
        }

        clearConsole();

        try (Socket socket = new Socket(address, port);
             DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
             DataInputStream dataInputStream = new DataInputStream(socket.getInputStream())) {

            writeMessage("Successfully connected to the server\n");

            new Thread(() -> {
                while (!socket.isClosed() && socket.isConnected()) {
                    String input;
                    try {
                        input = dataInputStream.readUTF();
                        System.out.println(input);
                    } catch (IOException e) {
                        System.err.println("Stopped receiving messages from the server\nTry to reconnect");
                        break;
                    }
                }
                System.exit(-1);
            }).start();

            while (true) {
                String line = console.nextLine();
                if (line.toLowerCase().trim().equals("exit"))
                    break;
                dataOutputStream.writeUTF(line);
                dataOutputStream.flush();
            }

        } catch (NoRouteToHostException e) {
            System.err.println("Server unreachable");
        } catch (ConnectException e) {
            System.err.println("Connection refused. Check port and try again");
        } catch (IOException e) {
            LOGGER.severe(e.getMessage());
        }

    }

    private static boolean validIP(String ip) {
        try {
            if (ip == null || ip.isEmpty()) {
                return false;
            }

            String[] parts = ip.split("\\.");
            if (parts.length != 4) {
                return false;
            }

            for (String s : parts) {
                int i = Integer.parseInt(s);
                if ((i < 0) || (i > 255)) {
                    return false;
                }
            }
            return !ip.endsWith(".");
        } catch (NumberFormatException nfe) {
            LOGGER.severe(nfe.getMessage());
            return false;
        }
    }

    private static boolean validPort(int port) {
        return port >= 1025 && port <= 65535 || port == 0;
    }


    /**
     * Writes to console message with timestamp
     *
     * @param message message
     */
    private static void writeMessage(String message) {

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss dd.MM.yyyy");

        String finalMessage = "[".concat(dateTimeFormatter.format(LocalDateTime.now()).concat("] "));

        System.out.println(finalMessage.concat(message));
    }

    private static void clearConsole() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

}
