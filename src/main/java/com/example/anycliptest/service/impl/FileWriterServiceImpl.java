package com.example.anycliptest.service.impl;

import com.example.anycliptest.service.FileWriterService;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class FileWriterServiceImpl implements FileWriterService {

    @Override
    public void writeToFile(String fileName, String message) {
        // no need in lock - lock by OS (mentioning https://softwareengineering.stackexchange.com/a/281809)
        try (FileWriter fw = new FileWriter(fileName, true)) {

            fw.write(message + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    @SneakyThrows
    public Long getLinesCount(String fileName) {
        return Files.lines(Path.of(fileName), Charset.defaultCharset()).count();
    }

    @Override
    public boolean fileExists(String fileName) {
        return Files.exists(Path.of(fileName));
    }
}
