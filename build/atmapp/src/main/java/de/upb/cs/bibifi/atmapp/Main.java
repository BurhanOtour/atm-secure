package de.upb.cs.bibifi.atmapp;

import de.upb.cs.bibifi.atmapp.util.CommandLineHandler;

public class Main {
    public static void main(String[] args) {
        System.out.println("ATM started...");
        new CommandLineHandler(args).init();
    }
}
