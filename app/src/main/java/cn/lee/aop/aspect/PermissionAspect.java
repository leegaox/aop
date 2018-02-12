package cn.lee.aop.aspect;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.util.Log;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import cn.lee.aop.MainActivity;
import cn.lee.aop.annotation.Permission;
import cn.lee.aop.util.PermissionUtil;

/**
 * @author yanfa
 * @Title: {标题}
 * @Description:{描述}
 * @date 2018/2/12
 */
@Aspect
public class PermissionAspect {

    @Pointcut("execution(@cn.lee.aop.annotation.Permission  * *(..))")
    public void pointCut() {
    }


    //定义Advice，织入代码。
    @Around("pointCut()")
    public void weaveJoinPoint(ProceedingJoinPoint joinPoint) throws Throwable {
        //获取功能名称
        MethodSignature sign = (MethodSignature) joinPoint.getSignature();
        Permission anno = sign.getMethod().getAnnotation(Permission.class);
        String[] func = anno.values();
        int length = func.length;
        for (String per : func) {
            Log.e("AAA", "request permission:" + per);
        }

        Activity activity = getActivity();
        if (activity == null) {
            return;
        }
        if (length == 1) {
            if (!PermissionUtil.checkSelfPermission(activity, func[0])) {
                for (int i = 0; i < PermissionUtil.permissions.length; i++) {
                    if (PermissionUtil.permissions[i].equals(func[0])) {
                        Log.e("AAA", "请求权限...");
                        PermissionUtil.doRequest(activity, func[0], PermissionUtil.requestCodes[i]);
                    } else {
                        Log.e("AAA", "无需权限1...");
                    }
                }
            } else {
                Log.e("AAA", "无需权限2...");
                Object ret = joinPoint.proceed();
            }
        } else if (length > 1) {
            PermissionUtil.requestPermissions(activity, func, PermissionUtil.REQUEST_PERMISSIONS);
        } else {
            //执行，功能方法
            Object ret = joinPoint.proceed();
        }

    }

    public static Activity getActivity() {
        Class activityThreadClass = null;
        try {
            activityThreadClass = Class.forName("android.app.ActivityThread");
            Object activityThread = activityThreadClass.getMethod("currentActivityThread").invoke(null);
            Field activitiesField = activityThreadClass.getDeclaredField("mActivities");
            activitiesField.setAccessible(true);
            Map activities = (Map) activitiesField.get(activityThread);
            for (Object activityRecord : activities.values()) {
                Class activityRecordClass = activityRecord.getClass();
                Field pausedField = activityRecordClass.getDeclaredField("paused");
                pausedField.setAccessible(true);
                if (!pausedField.getBoolean(activityRecord)) {
                    Field activityField = activityRecordClass.getDeclaredField("activity");
                    activityField.setAccessible(true);
                    Activity activity = (Activity) activityField.get(activityRecord);
                    return activity;
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        return null;
    }
}
