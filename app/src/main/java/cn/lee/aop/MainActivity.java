package cn.lee.aop;

import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import java.util.Random;

import cn.lee.aop.annotation.Trace;

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
