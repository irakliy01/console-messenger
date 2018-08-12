package com.irakliy01.messenger.server;

import java.io.DataOutputStream;
import java.net.InetAddress;
import java.util.HashSet;

class ClientList {

    private static HashSet<ClientList> clients = new HashSet<>();

    private DataOutputStream writer;
    private InetAddress inetAddress;

    private ClientList(InetAddress address, DataOutputStream dataOutputStream) {
        writer = dataOutputStream;
        inetAddress = address;
    }

    static void AddNewClient(InetAddress address, DataOutputStream dataOutputStream) throws Exception {
        ClientList client = new ClientList(address, dataOutputStream);
        if (clients.contains(client))
            throw new Exception("This client is already registered in server");
        clients.add(client);
    }

    static void RemoveOldClient(InetAddress address) {
        for (ClientList client : clients) {
            if (client.inetAddress.equals(address)) {
                clients.remove(client);
                break;
            }
        }
    }

    static HashSet<ClientList> GetClientList() {
        return clients;
    }

    InetAddress getInetAddress() {
        return inetAddress;
    }

    DataOutputStream getWriter() {
        return writer;
    }

}
