/*
 author Anastasiya
 created on 05/08/2021
 */

package com.example.anycliptest.service;


import com.example.anycliptest.model.Request;
import com.example.anycliptest.model.Response;

public interface DefaultHandler {
    Response putToFile(Request logRequest, String message);

}
