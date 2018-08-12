package com.irakliy01.messenger.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.logging.Logger;

class EchoThread implements Runnable {

    private Socket socket;
    private Logger LOGGER = Logger.getLogger(EchoThread.class.getName());
    //    private int id;
    private InetAddress inetAddress;


    EchoThread(Socket clientSocket/*, int id*/) {
        socket = clientSocket;
        inetAddress = socket.getInetAddress();
//        this.id = id;

        if (!socket.isClosed() && socket.isConnected())
            System.out.println(socket.getInetAddress().getCanonicalHostName().concat(" [").concat(socket.getInetAddress().getHostAddress()).concat("] successfully connected to the server"));
    }

    private void sendMessage(String message) {

        String finalMessage = "[".concat(inetAddress.getCanonicalHostName()).concat("] ").concat(message);

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

        } catch (Exception e) {
            LOGGER.severe(e.getMessage());
            ClientList.RemoveOldClient(inetAddress);
            System.out.println(inetAddress.getCanonicalHostName().concat(" [").concat(inetAddress.getHostAddress()).concat("] disconnected from the server"));
        }

    }

}
