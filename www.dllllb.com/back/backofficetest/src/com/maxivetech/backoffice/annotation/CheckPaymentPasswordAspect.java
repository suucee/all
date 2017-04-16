package com.maxivetech.backoffice.annotation;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.maxivetech.backoffice.dao.AdminLogDao;
import com.maxivetech.backoffice.dao.UserDao;
import com.maxivetech.backoffice.entity.Users;
import com.maxivetech.backoffice.pojo.PojoSession;
import com.maxivetech.backoffice.service.user.impl.SessionServiceImpl;
import com.maxivetech.backoffice.util.ForbiddenException;
import com.maxivetech.backoffice.util.HelperAuthority;
import com.maxivetech.backoffice.util.HelperPassword;

import java.lang.reflect.Field;  
import java.lang.reflect.Method;  
import java.lang.reflect.Parameter;

import javassist.ClassClassPath;  
import javassist.ClassPool;  
import javassist.CtClass;  
import javassist.CtMethod;  
import javassist.Modifier;  
import javassist.NotFoundException;  
import javassist.bytecode.CodeAttribute;  
import javassist.bytecode.LocalVariableAttribute;  
import javassist.bytecode.MethodInfo;  

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;


@Aspect
@Component
public class CheckPaymentPasswordAspect {
	@Autowired
	private UserDao userDao;
	
	// Service层切点
	@Pointcut("@annotation(com.maxivetech.backoffice.annotation.CheckPaymentPassword)")
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
		
		PojoSession pojoSession = SessionServiceImpl.getCurrent(session);
		if (pojoSession.getHoldAdminId() > 0) {	//如果是代管登录，无需验证支付密码
			return joinPoint.proceed();
		} else {
			String paymentPassword = getPaymentPassword(joinPoint);
			if (paymentPassword != null) {
	
				Users user = (Users) userDao.getById(HelperAuthority.getId(session));
				if (HelperPassword.verifyPaymentPassword(paymentPassword, user)) {
					return joinPoint.proceed();
				}
			}
		}
		
		throw new RuntimeException("错误的支付密码！");
	}

	private String getPaymentPassword(JoinPoint joinPoint) throws Exception {
		String targetName = joinPoint.getTarget().getClass().getName();
		String methodName = joinPoint.getSignature().getName();
		Object[] arguments = joinPoint.getArgs();
		Class targetClass = Class.forName(targetName);
		Method[] methods = targetClass.getMethods();

		for (Method method : methods) {
			if (method.getName().equals(methodName)) {
				Class[] clazzs = method.getParameterTypes();
				//有个风险，如果方法名一样，参数个数也一样，又有不同的本注解，可能会串
				if (clazzs.length == arguments.length && method.getAnnotation(CheckPaymentPassword.class) != null) {
					Object ret = arguments[method.getAnnotation(CheckPaymentPassword.class).parameterIndex()];
					return ret == null ? null : ret.toString();
				}
			}
		}
		
		return null;
	}

}