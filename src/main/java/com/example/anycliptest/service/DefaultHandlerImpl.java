/*
 author Anastasiya
 created on 05/08/2021
 */


package com.example.anycliptest.service;


import com.example.anycliptest.model.Request;
import com.example.anycliptest.model.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

@Service
@Slf4j
public class DefaultHandlerImpl implements DefaultHandler {
    long numOfLines;
    @Override
    public Response putToFile(Request logRequest, String message) {
        if(Files.exists(Path.of(logRequest.getLogFile()))) {
            try (Stream<String> lines = Files.lines(Path.of(logRequest.getLogFile()), Charset.defaultCharset())) {
                numOfLines = lines.count();

            } catch (IOException e) {
                e.printStackTrace();
            }
            if (numOfLines > Integer.parseInt(logRequest.getLogLimit())) {
                log.error("Count of lines more than expected. Expected: " + logRequest.getLogLimit() + " Current: " + numOfLines);
                return Response.builder().build();
            }
            writeToFile(logRequest, message);
            log.info("Quantity of lines: " + numOfLines);
            return Response.builder().logFile(logRequest.getLogFile()).value(message).build();
        }else{
            writeToFile(logRequest, message);
            log.info("First write to file");
            return Response.builder().logFile(logRequest.getLogFile()).value(message).build();
        }
    }

    private void writeToFile(Request logRequest, String message) {
        try(FileWriter fw = new FileWriter(logRequest.getLogFile(), true);
            BufferedWriter bw = new BufferedWriter(fw)) {
            bw.write(message);
            bw.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
