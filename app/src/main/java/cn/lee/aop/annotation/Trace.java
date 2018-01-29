package cn.lee.aop.annotation;

import org.aspectj.lang.JoinPoint;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Lee
 * @Title: {性能分析}
 * @Description:{监听方法执行耗时，打印并输出}
 * @date 2018/1/29
 */
//@Target表示这个注解只能给函数使用
//@Retention表示注解内容需要包含在Class字节码里，属于运行时需要的。
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Trace {

    //使用Trace注解传进来的String值
    String value();

}
