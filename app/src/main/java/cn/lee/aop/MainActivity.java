package cn.lee.aop;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.util.Random;

import cn.lee.aop.annotation.Permission;
import cn.lee.aop.annotation.Trace;
import cn.lee.aop.util.PermissionUtil;
import cn.lee.aop.util.SnackBarBuilder;

import static android.Manifest.permission.BODY_SENSORS;
import static android.Manifest.permission.CALL_PHONE;
import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_CALENDAR;
import static android.Manifest.permission.READ_CONTACTS;
import static android.Manifest.permission.SEND_SMS;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static cn.lee.aop.util.PermissionUtil.REQUEST_CAMERA;
import static cn.lee.aop.util.PermissionUtil.REQUEST_CONTACTS;
import static cn.lee.aop.util.PermissionUtil.REQUEST_PERMISSIONS;

public class MainActivity extends AppCompatActivity {

    private final String TAG = "MainActivity";

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

    @Permission(values = {READ_CONTACTS, WRITE_EXTERNAL_STORAGE, READ_CALENDAR, SEND_SMS, CALL_PHONE})
    public void checkGroupPermissions(View view) {
        Log.i(TAG, "检查危险权限组");
    }

    @Permission(values = {CAMERA})
    public void checkSinglePermission(View view) {
        Log.i(TAG, "检查相机权限");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CAMERA:
                // Check if the only required permission has been granted
                if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplicationContext(), "Request Camera Success", Toast.LENGTH_SHORT).show();
                } else {
                    if (PermissionUtil.shouldShowRequestPermissionRationale(this, CAMERA)) {
                        new SnackBarBuilder(this, getString(R.string.permission_not_granted), Snackbar.LENGTH_LONG).setBackgroundColor(R.color.white).show();
                    } else {
                        PermissionUtil.showAppSettingsSnackBar(this, REQUEST_CAMERA);
                    }
                    Log.i(TAG, "CAMERA permission was NOT granted.");

                }
                break;
            case REQUEST_CONTACTS:
                // We have requested multiple permissions for contacts, so all of them need to be checked.
                if (PermissionUtil.verifyPermissions(grantResults)) {
                    // All required permissions have been granted, do next.
                    Toast.makeText(getApplicationContext(), "Request Contacts Success", Toast.LENGTH_SHORT).show();
                } else {
                    if (PermissionUtil.shouldShowRequestPermissionRationale(this, READ_CONTACTS)) {
                        new SnackBarBuilder(this, getString(R.string.permission_not_granted), Snackbar.LENGTH_LONG).setBackgroundColor(R.color.white).show();
                    } else {
                        PermissionUtil.showAppSettingsSnackBar(this, REQUEST_CONTACTS);
                    }
                    Log.i(TAG, "Contacts permissions were NOT granted.");

                }
                break;
            case REQUEST_PERMISSIONS:
                // We have requested multiple permissions for contacts, so all of them need to be checked.
                if (PermissionUtil.verifyPermissions(grantResults)) {
                    // All required permissions have been granted, do next.
                    Toast.makeText(getApplicationContext(), "Request Contacts Success", Toast.LENGTH_SHORT).show();
                } else {
                    //show Dialog
                    Toast.makeText(getApplicationContext(), "Request Contacts error", Toast.LENGTH_SHORT).show();
                    PermissionUtil.requestPermissions(this, permissions, REQUEST_PERMISSIONS);

                }

                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                break;
        }

    }
}
