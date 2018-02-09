package com.distributed.transaction.conf.stream.limiter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.core.annotation.AliasFor;

@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface LimitRequest {

	@AliasFor("domain")
	StreamDomainConstant value() default StreamDomainConstant.UNKNOW;

}
