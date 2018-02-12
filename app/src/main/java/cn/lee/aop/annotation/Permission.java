package cn.lee.aop.annotation;

import android.app.Activity;
import android.content.Context;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Lee
 * @Title: {权限检查}
 * @Description:{}
 * @date 2018/02/12
 */
//@Target表示这个注解只能给函数使用
//@Retention表示注解内容需要包含在Class字节码里，属于运行时需要的。
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Permission {

    //使用Trace注解传进来的String值
    String[] values();

}
