package cn.lee.aop.aspect;

import android.util.Log;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;


import cn.lee.aop.annotation.Trace;


/**
 * @author yanfa
 * @Title: {标题}
 * @Description:{代表性能检测的切面}
 * @date 2018/1/29
 */
//@Aspect定义切面
@Aspect
public class TraceAspect {

    //@Pointcut定义切入点
    //execution 定义切面上需要执行的连接点   * *(..)代表 任意类的任意方法。
    //执行任意类的任意方法注解了cn.lee.aop.annotation.Trace的连接点
    @Pointcut("execution(@cn.lee.aop.annotation.Trace  * *(..))")
    public void pointCut() {
    }

    //定义Advice，织入代码。
    @Around("pointCut()")
    public Object weaveJoinPoint(ProceedingJoinPoint joinPoint) throws Throwable {
        //获取功能名称
        MethodSignature sign = (MethodSignature) joinPoint.getSignature();
        Trace anno = sign.getMethod().getAnnotation(Trace.class);
        String func = anno.value();

        long start = System.currentTimeMillis();
        //执行，功能方法

        Object ret = joinPoint.proceed();

        long duration = System.currentTimeMillis() - start;
        Log.i("trace", String.format("功能：%s，耗时：%d ms", func, duration));
        return ret;
    }

}