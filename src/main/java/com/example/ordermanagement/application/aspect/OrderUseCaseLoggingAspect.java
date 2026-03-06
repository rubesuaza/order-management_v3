package com.example.ordermanagement.application.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * AOP Aspect implementing Proxy pattern for transparent cross-cutting logging.
 * Intercepts Order use case invocations without modifying business logic.
 */
@Aspect
@Component
public class OrderUseCaseLoggingAspect {

    private static final Logger log = LoggerFactory.getLogger(OrderUseCaseLoggingAspect.class);

    @Around("execution(* com.example.ordermanagement.application.service.*.*(..))")
    public Object logUseCaseInvocation(ProceedingJoinPoint joinPoint) throws Throwable {
        String method = joinPoint.getSignature().toShortString();
        log.debug("Order use case invoked: {}", method);
        long start = System.currentTimeMillis();
        try {
            Object result = joinPoint.proceed();
            log.debug("Order use case completed: {} in {}ms", method, System.currentTimeMillis() - start);
            return result;
        } catch (Throwable t) {
            log.warn("Order use case failed: {} - {}", method, t.getMessage());
            throw t;
        }
    }
}
