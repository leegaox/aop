package cn.lee.aop;

import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import java.util.Random;

import cn.lee.aop.annotation.Trace;

public class MainActivity extends AppCompatActivity {

    private final String TAG = getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Trace("doSpeak")
    public void doSpeak(View view) {
        //使用Random模仿方法运行时间
        SystemClock.sleep(Math.abs(new Random().nextInt(500)));
    }

    @Trace("doRead")
    public void doRead(View view) {
        //使用Random模仿方法运行时间
        SystemClock.sleep(Math.abs(new Random().nextInt(500)));
    }
}
