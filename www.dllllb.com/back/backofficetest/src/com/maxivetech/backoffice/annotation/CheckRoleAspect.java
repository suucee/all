package com.maxivetech.backoffice.annotation;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.maxivetech.backoffice.dao.AdminLogDao;
import com.maxivetech.backoffice.util.ForbiddenException;
import com.maxivetech.backoffice.util.HelperAuthority;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import java.lang.reflect.Method;

/**
 * 切点类
 * 
 * @author tiangai
 * @since 2014-08-05 Pm 20:35
 * @version 1.0
 */
@Aspect
@Component
public class CheckRoleAspect {
	// Service层切点
	@Pointcut("@annotation(com.maxivetech.backoffice.annotation.CheckRole)")
	public void serviceAspect() {
	}

	/**
	 * 前置通知
	 *
	 * @param joinPoint
	 *            切点
	 */
	@Around("serviceAspect()") 
	public Object doAround(ProceedingJoinPoint joinPoint) throws Throwable {
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder
				.getRequestAttributes()).getRequest();
		HttpSession session = request.getSession();

		Role[] roles = this.getRoles(joinPoint);
		if (roles != null) {
			for (Role role : roles) {
				if (HelperAuthority.is(session, role.toString())) {
					return joinPoint.proceed();
				}
			}
		}
		
		throw new ForbiddenException();
	}

	private Role[] getRoles(JoinPoint joinPoint) throws Exception {
		String targetName = joinPoint.getTarget().getClass().getName();
		String methodName = joinPoint.getSignature().getName();
		Object[] arguments = joinPoint.getArgs();
		Class targetClass = Class.forName(targetName);
		Method[] methods = targetClass.getMethods();
		Role[] roles = null;
		for (Method method : methods) {
			if (method.getName().equals(methodName)) {
				Class[] clazzs = method.getParameterTypes();
				//有个风险，如果方法名一样，参数个数也一样，又有不同的本注解，可能会串
				if (clazzs.length == arguments.length && method.getAnnotation(CheckRole.class) != null) {
					roles = method.getAnnotation(CheckRole.class).role();
					break;
				}
			}
		}
		
		return roles;
	}

}