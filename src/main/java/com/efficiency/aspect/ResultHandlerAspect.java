package com.efficiency.aspect;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.efficiency.entity.R;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

/**
 * 分页.
 * @author vincent.jiao
 */
@Aspect
@Component
@Slf4j
public class ResultHandlerAspect {
    /**
     * 拦截的包
     */
    @Pointcut("execution(* com.efficiency.controller..*.*(..))")
    public void applicationPackagePointcut() {
    }

    /**
     * @param joinPoint join point
     * @return result
     * @throws Throwable throws IllegalArgumentException
     */
    @Around("applicationPackagePointcut()")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        R r = null;
        Object result = null;
        try {
            result = joinPoint.proceed();
            r = result instanceof R ? (R) result : R.ok(result);

        } catch (Exception e) {
            e.printStackTrace();
            //显示详细错误信息
            StackTraceElement stackObj = e.getStackTrace()[0];
            for (StackTraceElement stack : e.getStackTrace()) {
                if (stack.getClassName().startsWith("com.efficiency")) {
                    stackObj = stack;
                    break;
                }
            }

            String msg = e.getClass() + ":" + e.getMessage();
            msg += "错误路径：" + stackObj.getClassName()+"."+stackObj.getMethodName()+" 行号："+stackObj.getLineNumber();
            r = R.fail(msg);
        }

        //方法无返回值的使用输出流输出
        if (result == null) {
            Class claasObj = joinPoint.getTarget().getClass();
            Method[] methods = claasObj.getDeclaredMethods();
            Method method = null;
            for (Method item : methods) {
                if (item.getName().equals(joinPoint.getSignature().getName())) {
                    method = item;
                    break;
                }
            }

            if (method != null) {
                if ("void".equals(method.getReturnType().getName())) {
                    ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
                    if (attrs != null) {
                        HttpServletResponse response = attrs.getResponse();
//                        response.setStatus(r.getCode());
                        response.setCharacterEncoding("UTF-8");
                        response.setContentType("application/json;charset=UTF-8");
                        response.getWriter().print(JSONUtil.parse(r).toString());
                    }

                    return null;
                }
            }
        }

        return r;
    }
}
