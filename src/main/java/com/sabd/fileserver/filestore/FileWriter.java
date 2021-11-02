package com.sabd.fileserver.filestore;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class FileWriter {

    Path filePath;
    boolean isFirst = true;


    public FileWriter(Path path) {
        this.filePath = path;
    }

    public void write(Long reference) {
        try {
            Files.writeString(filePath, reference.toString() + "\n", isFirst ? StandardOpenOption.CREATE : StandardOpenOption.APPEND);
            if (isFirst) {
                isFirst = false;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}