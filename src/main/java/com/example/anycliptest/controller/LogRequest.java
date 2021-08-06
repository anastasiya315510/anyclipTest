/*
 author Anastasiya
 created on 05/08/2021
 */


package com.example.anycliptest.controller;



import com.example.anycliptest.model.GeneralStatistics;
import com.example.anycliptest.model.Request;
import com.example.anycliptest.model.Response;
import com.example.anycliptest.model.Variant;
import com.example.anycliptest.service.DefaultHandler;
import com.example.anycliptest.service.EnabledTrue;
import com.example.anycliptest.service.StatisticsReceiver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
@Slf4j
public class LogRequest {
  @Autowired
  private EnabledTrue enableTrueHandler;

  @Autowired
  private DefaultHandler defaultLogHandler;

  @Autowired
  private StatisticsReceiver statistics;


  @PostMapping("/api/v1/log")
  public Response putToFile(@RequestBody Request logRequest) {

    if (!logRequest.getAbTest().isEnabled()) {
      log.info("  enabled is false");
      String message = "NA";
      return defaultLogHandler.putToFile(logRequest, message);
    } else if (!checkVariants(logRequest)) {
      log.error("sum of percent is not 100");
      return Response.builder().build();
    } else if (isVariantsMoreThen5(logRequest)) {
      log.error("variants should be up to 5");
      return Response.builder().build();
    } else {
      Response response = enableTrueHandler.putToFile(logRequest);
      return response;
    }
  }

  private boolean isVariantsMoreThen5(Request logRequest) {
    return logRequest.getAbTest().getVariants().size()>5;
  }

  private boolean checkVariants(Request logRequest) {
    return logRequest.getAbTest().getVariants().stream().map(Variant::getPercentage).reduce(0, Integer::sum)==100;


  }

  @GetMapping("/api/v1/log/stats")
    public GeneralStatistics getStatistics(){
    return statistics.getStatistics();
  }

}
