package com.irakliy01.messenger.server;

import java.net.InetAddress;

/**
 * Custom exception that must be thrown when new user tries to connect to the server and user with such IP address is already connected to the server
 */
public class UserExistsException extends Exception {

    /**
     * Default constructor. Writes message that user with such IP address is already connected to the server
     */
    public UserExistsException() {
        super("User with such IP address is already connected to the server");
    }

    /**
     * Throws exception and writes message to console
     * @param message message to write to the console
     */
    public UserExistsException(String message) {
        super(message);
    }

    /**
     * Writes custom message using that user with such address if already connected to the server
     * @param address address of the user
     */
    UserExistsException(InetAddress address) {
        super("User with ip address " + address.getHostAddress() + " is already connected to the server");
    }

}
