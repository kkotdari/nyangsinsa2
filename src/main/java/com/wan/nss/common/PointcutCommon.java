package com.wan.nss.common;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

@Aspect
public class PointcutCommon {
	@Pointcut("execution(* com.wan.nss.controller..*Controller.*(..))")
	public void aPointcut() {} // 포인트컷 선언
}