# 使用AspectJ在Android中实现Aop

## AOP介绍
>   AOP为Aspect Oriented Programming的缩写，意为：面向切面编程，通过预编译方式和运行期动态代理实现程序功能的统一维护的一种技术。AOP是OOP的延续，是软件开发中的一个热点，也是Spring框架中的一个重要内容，是函数式编程的一种衍生范型。利用AOP可以对业务逻辑的各个部分进行隔离，从而使得业务逻辑各部分之间的耦合度降低，提高程序的可重用性，同时提高了开发的效率。 --百度百科



## AspectJ概念介绍

 AspectJ 中几个必须要了解的概念：

- ``Aspect（切面）`` ： Aspect 声明类似于 Java 中的类声明，在 Aspect 中会包含着一些 Pointcut 以及相应的 Advice。

- ``Joint point（连接点）``：表示在程序中明确定义的点，典型的包括方法调用，对类成员的访问以及异常处理程序块的执行等等，它自身还可以嵌套其它 joint point。

- ``Pointcut（切入点）``：表示一组 joint point，这些 joint point 或是通过逻辑关系组合起来，或是通过通配、正则表达式等方式集中起来，它定义了相应的 Advice 将要发生的地方。提供一种使得开发者能够选择自己需要的JoinPoints的方法。

- ``Advice（通知）``：Advice 定义了在 pointcut 里面定义的程序点具体要做的操作，它通过 before、after 和 around 来区别是在每个 joint point 之前、之后还是代替执行的代码。

- ``Weaving（织入）``: 注入代码（advices）到目标位置（joint points）的过程。

## AOP在Android中的使用场景

- 日志
- 持久化
- 性能监控
- 数据校验
- 登录校验
- 用户行为统计
- 运行时权限检查
- ...

## AsecptJ使用举例
  我们以``性能监控``举例，在开发过程中，需要进行性能优化，比较方法的执行效率，那么我们会统计方法执行的时间，如果有很多方法需要检测执行时间，那必然会导致代码冗余，则使用AOP进行性能监控是你的不二之选。

### 环境配置
  1. 在`build.gradle(Project:app)`中添加``Aspect``的java运行环境。

````java
buildscript {
    repositories {
        google()
        jcenter()
        mavenCentral()
    }
    dependencies {
        classpath 'com.android.tools.build:gradle:3.0.1'
        classpath 'org.aspectj:aspectjtools:1.8.13'
        classpath 'org.aspectj:aspectjweaver:1.8.13'
    }
}

allprojects {
    repositories {
        google()
        jcenter()
        mavenCentral()
    }
}
````
2. 在`build.gradle(Module:app)`中添加``Aspect``依赖jar包和配置信息

````java
dependencies {
    compile group: 'org.aspectj', name: 'aspectjrt', version: '1.8.13'
}

//AspectJ配置
import org.aspectj.bridge.IMessage
import org.aspectj.bridge.MessageHandler
import org.aspectj.tools.ajc.Main

final def log = project.logger
final def variants = project.android.applicationVariants

variants.all { variant ->
    if (!variant.buildType.isDebuggable()) {
        log.debug("Skipping non-debuggable build type '${variant.buildType.name}'.")
        return;
    }

    JavaCompile javaCompile = variant.javaCompile
    javaCompile.doLast {
        String[] args = ["-showWeaveInfo",
                         "-1.8",
                         "-inpath", javaCompile.destinationDir.toString(),
                         "-aspectpath", javaCompile.classpath.asPath,
                         "-d", javaCompile.destinationDir.toString(),
                         "-classpath", javaCompile.classpath.asPath,
                         "-bootclasspath", project.android.bootClasspath.join(File.pathSeparator)]
        log.debug "ajc args: " + Arrays.toString(args)

        MessageHandler handler = new MessageHandler(true);
        new Main().run(args, handler);
        for (IMessage message : handler.getMessages(null, true)) {
            switch (message.getKind()) {
                case IMessage.ABORT:
                case IMessage.ERROR:
                case IMessage.FAIL:
                    log.error message.message, message.thrown
                    break;
                case IMessage.WARNING:
                    log.warn message.message, message.thrown
                    break;
                case IMessage.INFO:
                    log.info message.message, message.thrown
                    break;
                case IMessage.DEBUG:
                    log.debug message.message, message.thrown
                    break;
            }
        }
    }
}
````

### 使用

1. 写一个界面，界面上有两个按钮，按钮的点击模拟两个方法 ``readFile``和 ``writeFile``。代码中注释的耗时计算是最常规的方式。
````java
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    //trace名称，通过Trace的 value()方法获取
      @Trace("读文件")
    public void readFile(View view) {

//        long start = System.currentTimeMillis();

        //使用Random模仿方法运行时间
        SystemClock.sleep(Math.abs(new Random().nextInt(1000)));

//        long duration = System.currentTimeMillis() - start;
//        Log.i("trace", String.format("功能：%s,耗时：%d ms", "读文件", duration));
    }

    @Trace("写文件")
    public void writeFile(View view) {

//       long start = System.currentTimeMillis();

        //使用Random模仿方法运行时间
        SystemClock.sleep(Math.abs(new Random().nextInt(1000)));

//        long duration = System.currentTimeMillis() - start;
//        Log.i("trace", String.format("功能：%s,耗时：%d ms", "读文件", duration));
    }
}
````

2. 自定义注解`Trace`，将这个注解标志在需要被监听的方法上。
````jva
//@Target表示这个注解只能给函数使用
//@Retention表示注解内容需要包含在Class字节码里，属于运行时需要的。
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Trace {

    //使用Trace注解传进来的String值
    String value();

}
````

3. 定义代表性能检测的切面`TraceAspect`

````java

//@Aspect定义切面
@Aspect
public class TraceAspect {

    //@Pointcut定义切入点
    //execution 定义切面上需要执行的被该注解标注的方法
    //cn.lee.aop.annotation.Trace为注解的全路径
    //* *(..)代表 在任意类的任意方法使用了Trace注解的地方作为切入点，织入（weave）代码。
    @Pointcut("execution(@cn.lee.aop.annotation.Trace  * *(..))")
    public void methodAnnotatedWithBehaviorTrace() {
    }

    //定义Advice，织入代码。
    @Around("methodAnnotatedWithBehaviorTrace()")
    public Object weaveJoinPoint(ProceedingJoinPoint joinPoint) throws Throwable {
        //获取功能名称
        MethodSignature sign = (MethodSignature) joinPoint.getSignature();
        Trace anno = sign.getMethod().getAnnotation(Trace.class);
        String func = anno.value();

        long start = System.currentTimeMillis();

        //执行，功能方法
        Object ret = joinPoint.proceed();

        long duration = System.currentTimeMillis() - start;
        Log.i("trace", String.format("功能：%s,耗时：%d ms", func, duration));
        return ret;
    }

}
````
