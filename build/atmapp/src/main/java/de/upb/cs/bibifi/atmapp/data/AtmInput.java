package de.upb.cs.bibifi.atmapp.data;

import org.apache.commons.cli.CommandLine;

public class AtmInput {

    private CommandLine commandLine;
    private String PIN;

    public AtmInput(CommandLine commandLine, String PIN) {
        this.commandLine = commandLine;
        this.PIN = PIN;
    }

    public CommandLine getCommandLine() {
        return commandLine;
    }

    public String getPIN() {
        return PIN;
    }
}
