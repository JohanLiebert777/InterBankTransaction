package com.distributed.transaction.conf.stream.limiter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class ControllerStreamLimiter {

	@Autowired
	private StreamLimiter limiter;

	@Pointcut("execution(* com.distributed.transaction.controller..*.*(..))")
	public void hotControllerMethods() {
	}

	@Before("hotControllerMethods()")
	public void doBefore(final JoinPoint jp) {
		if (needLimit(jp) && !limiter.tryAcquire(StreamDomainConstant.TRANSFER)) {
			throw new RequestBlockedException("Request is blocked by rate limiter!");
		}
	}

	private boolean needLimit(JoinPoint jp) {
		Class<? extends Object> clazz = jp.getTarget().getClass();
		String methodName = jp.getSignature().getName();
		for (Method method : clazz.getDeclaredMethods()) {
			if (method.getName().equalsIgnoreCase(methodName)) {
				for (Annotation annotation : method.getDeclaredAnnotations()) {
					if (annotation instanceof LimitRequest) {
						return true;
					}
				}
				return false;
			}
		}
		return false;
	}

}
