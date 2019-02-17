package com.irakliy01.messenger.server;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.Vector;
import java.util.logging.Logger;

/**
 * Class which has only one method to get local IP addresses
 *
 * @author irakliy01
 * @version 13/10/2018
 */
class IPsGetter {

    private static Logger LOGGER = Logger.getLogger(Server.class.getName());

    /**
     * Returns all local IP addresses as Vector
     * @return all local IP addresses as Vector
     */
    static Vector<String> getLocalAddresses() {

        Vector<String> addresses = new Vector<>();

        try {

            Enumeration e = NetworkInterface.getNetworkInterfaces();
            while (e.hasMoreElements()) {
                NetworkInterface networkInterface = (NetworkInterface) e.nextElement();
                Enumeration ee = networkInterface.getInetAddresses();
                while (ee.hasMoreElements()) {
                    InetAddress inetAddress = (InetAddress) ee.nextElement();
                    if (inetAddress.isLoopbackAddress()) // Skip localhost
                        continue;
                    if (inetAddress instanceof Inet4Address)  // Add only IPv4 addresses
                        addresses.add(inetAddress.getHostAddress());
                }
            }

        } catch (SocketException ex) {
            LOGGER.severe(ex.getMessage());
        }

        return addresses;
    }

}
