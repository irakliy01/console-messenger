package com.irakliy01.messenger.server;

import java.io.DataOutputStream;
import java.net.InetAddress;
import java.util.HashSet;

/**
 * This class allows to store information about every user connected to the server. It stores their inetAddresses and writers (dataOutputStream)
 *
 * @author irakliy01
 * @version 13/10/2018
 */
class ClientList {

    private static HashSet<ClientList> clients = new HashSet<>();

    private DataOutputStream writer;
    private InetAddress inetAddress;

    private ClientList(InetAddress address, DataOutputStream dataOutputStream) {
        writer = dataOutputStream;
        inetAddress = address;
    }

    /**
     * Adds new user to the list of connected to the server users
     * @param address address of new client
     * @param dataOutputStream output stream of new client
     * @throws UserExistsException throws this exception if user with such IP address is already connected to the server
     */
    static void AddNewClient(InetAddress address, DataOutputStream dataOutputStream) throws UserExistsException {
        ClientList client = new ClientList(address, dataOutputStream);
        if (clients.contains(client))
            throw new UserExistsException(address);
        clients.add(client);
    }

    /**
     * Removes client from list of connected users
     * @param address IP address of user
     */
    static void RemoveOldClient(InetAddress address) {
        for (ClientList client : clients) {
            if (client.inetAddress.equals(address)) {
                clients.remove(client);
                break;
            }
        }
    }

    /**
     * Get list of clients connected to the server
     * @return list of clients
     */
    static HashSet<ClientList> GetClientList() {
        return clients;
    }

    /**
     * Get IP address of certain client
     * @return inetAddress of certain client
     */
    InetAddress getInetAddress() {
        return inetAddress;
    }

    /**
     * Get DataOutputStream of certain client
     * @return DataOutputStream of certain client
     */
    DataOutputStream getWriter() {
        return writer;
    }

}
