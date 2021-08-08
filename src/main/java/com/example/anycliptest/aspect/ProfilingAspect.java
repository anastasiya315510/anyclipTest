package com.example.anycliptest.aspect;


import com.example.anycliptest.dto.LogResponse;
import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.Counter;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;


@Component
@Aspect
@Slf4j
public class ProfilingAspect {

    public static Counter requestCount;
    public static Set<Long> timeSetLong = ConcurrentHashMap.newKeySet();
    public static Set<Long> writingTimeSetLong = ConcurrentHashMap.newKeySet();
    private Long startProcess;
    private Long finishProcess;
    private Long startWriteToFile;
    private Long finishWriteToFile;

    public ProfilingAspect(CollectorRegistry collectorRegistry) {
        requestCount = Counter.build()
                .name("request_count")
                .help("Number of putToFile requests.")
                .register(collectorRegistry);


    }

    @Before("execution(* com.example.anycliptest.service.impl.DefaultLogWriterServiceImpl.writeToFile(..))")
    public void beforeService(JoinPoint joinPoint) {
        startWriteToFile = System.currentTimeMillis();
        log.info(joinPoint.getThis().toString() + " Aspect before DefaultHandlerImpl write to file");
    }

    @After("execution(* com.example.anycliptest.service.impl.DefaultLogWriterServiceImpl.writeToFile(..))")
    public void afterService(JoinPoint joinPoint) {
        finishWriteToFile = System.currentTimeMillis();
        long durationWritingToFile = finishWriteToFile - startWriteToFile;
        writingTimeSetLong.add(durationWritingToFile);
        log.info(joinPoint.getThis().toString() + " Aspect after DefaultHandlerImpl write to file");
    }

    @Before("execution(* com.example.anycliptest.service.impl.ABTestLogWriterServiceImpl.writeToFile(..))")
    public void beforeServiceEnabledTrue(JoinPoint joinPoint) {
        startWriteToFile = System.currentTimeMillis();
        log.info(joinPoint.getThis().toString() + " Aspect before ServiceEnabledTrue write to file: " + startWriteToFile);
    }

    @After("execution(* com.example.anycliptest.service.impl.ABTestLogWriterServiceImpl.writeToFile(..))")
    public void afterServiceEnabledTrue(JoinPoint joinPoint) {
        finishWriteToFile = System.currentTimeMillis();
        long durationWritingToFile = finishWriteToFile - startWriteToFile;
        writingTimeSetLong.add(durationWritingToFile);
        log.info(joinPoint.getThis().toString() + " Aspect after LogHandlerEnabledTrue write to file: " + durationWritingToFile);
    }

    @Before("execution(* com.example.anycliptest.controller.LogController.putToFile(..))")
    public void before(JoinPoint joinPoint) {
        startProcess = System.currentTimeMillis();
        log.info(joinPoint.getThis().toString() + " Aspect before");
    }

    @AfterReturning(pointcut = "execution(* com.example.anycliptest.controller.LogController.putToFile(..))",
            returning = "result")
    public void afterReturn(JoinPoint joinPoint, LogResponse result) {
        requestCount.inc();
        log.info(joinPoint.getThis().toString() + " Aspect afterReturn, counter: " + requestCount.get());
    }

    @After("execution(* com.example.anycliptest.controller.LogController.putToFile(..))")
    public void after(JoinPoint joinPoint) {
        finishProcess = System.currentTimeMillis();
        long durationProcess = finishProcess - startProcess;
        timeSetLong.add(durationProcess);
        log.info(joinPoint.getThis().toString() + " Aspect after timeSet: " + durationProcess);
    }
}
