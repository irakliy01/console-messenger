package com.irakliy01.messenger.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Logger;

/**
 * Thread which works with every user independently. It reads char sequence from the console and send it to other clients
 *
 * @author irakliy01
 * @version 14/10/2018
 */
class EchoThread implements Runnable {

    private Socket socket;
    private Logger LOGGER = Logger.getLogger(EchoThread.class.getName());
    private InetAddress inetAddress;

    /**
     * Default constructor for EchoThread. Gets information of the user from socket
     * @param clientSocket socket of client from which certain info will be taken
     */
    EchoThread(Socket clientSocket) {
        socket = clientSocket;
        inetAddress = socket.getInetAddress();

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss dd.MM.yyyy");

        if (!socket.isClosed() && socket.isConnected())
            System.out.println("[" + dateTimeFormatter.format(LocalDateTime.now()) + "] " + inetAddress.getCanonicalHostName().concat(" [").concat(socket.getInetAddress().getHostAddress()).concat("] successfully connected to the server"));
    }

    private void sendMessage(String message) {

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss dd.MM.yyyy");

        String finalMessage = "\n[".concat(dateTimeFormatter.format(LocalDateTime.now())).concat(" | " + inetAddress.getCanonicalHostName()).concat("] ").concat(message);

        for (ClientList client : ClientList.GetClientList()) {
            if (!client.getInetAddress().equals(socket.getInetAddress())) {
                try {
                    client.getWriter().writeUTF(finalMessage);
                    client.getWriter().flush();
                } catch (IOException e) {
                    LOGGER.severe(e.getMessage());
                }
            }
        }
    }

    @Override
    public void run() {

        try (DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
             DataInputStream dataInputStream = new DataInputStream(socket.getInputStream())) {

            ClientList.AddNewClient(socket.getInetAddress(), dataOutputStream);

            while (!socket.isClosed() && socket.isConnected()) {

                String inputString = dataInputStream.readUTF();
                sendMessage(inputString);

            }

            ClientList.RemoveOldClient(inetAddress);
            System.out.println(inetAddress.getCanonicalHostName().concat(" [").concat(inetAddress.getHostAddress()).concat("] disconnected from the server"));

        } catch (IOException e) {
            ClientList.RemoveOldClient(inetAddress);
            System.out.println(inetAddress.getCanonicalHostName().concat(" [").concat(inetAddress.getHostAddress()).concat("] disconnected from the server"));
        } catch (UserExistsException e) {
            System.out.println(inetAddress.getCanonicalHostName().concat(" [").concat(inetAddress.getHostAddress()).concat("] disconnected from the server"));
            try {
                socket.close();
            } catch (IOException e1) {
                LOGGER.severe(e.getMessage());
            }
        }

    }

}
