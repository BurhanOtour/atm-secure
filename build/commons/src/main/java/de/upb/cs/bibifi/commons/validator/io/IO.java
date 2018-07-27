package de.upb.cs.bibifi.commons.validator.io;

import java.io.*;

public class IO {

    public void writeToFile(String content) throws IOException {

        final File file = new File("mypath");// TODO: 27/07/2018 get directory from properties file

        Writer writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(file));
            writer.write(content);
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }
}
