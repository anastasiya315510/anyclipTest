package com.example.anycliptest.service;

public interface FileWriterService {

    void writeToFile(String fileName, String message);
    Long getLinesCount(String fileName);
    boolean fileExists(String fileName);
}
