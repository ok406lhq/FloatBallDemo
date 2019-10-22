package study.lhq.com.floatballdemo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.lang.reflect.Method;

import study.lhq.com.floatballdemo.floatball.DraggableFloatView;
import study.lhq.com.floatballdemo.floatball.DraggableFloatWindow;

public class MainActivity extends Activity implements View.OnClickListener {

    private DraggableFloatWindow mFloatWindow;
    private static final String TAG = "lhqqq";
    private Button show_float;
    private Button dismiss_float;
    private boolean isShow = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        show_float = findViewById(R.id.show_f);
        dismiss_float = findViewById(R.id.dismiss_f);

        show_float.setOnClickListener(this);
        dismiss_float.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.show_f:
                if (commonROMPermissionCheck(MainActivity.this)) {
                    showFloat();
                } else {
                    requestAlertWindowPermission();
                }
                break;

            case R.id.dismiss_f:
                if (mFloatWindow != null) {
                    mFloatWindow.dismiss();
                }
                break;
        }
    }

    private void showFloat() {
        mFloatWindow = DraggableFloatWindow.getDraggableFloatWindow(this, null);
        mFloatWindow.show();
        isShow = true;
        mFloatWindow.setOnTouchButtonListener(new DraggableFloatView.OnTouchButtonClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "点击了悬浮球", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private static final int REQUEST_CODE = 1;

    //判断权限
    private boolean commonROMPermissionCheck(Context context) {
        Boolean result = true;
        if (Build.VERSION.SDK_INT >= 23) {
            try {
                Class clazz = Settings.class;
                Method canDrawOverlays = clazz.getDeclaredMethod("canDrawOverlays", Context.class);
                Settings.canDrawOverlays(context);
                result = (Boolean) canDrawOverlays.invoke(null, context);
            } catch (Exception e) {
                Log.e(TAG, Log.getStackTraceString(e));
            }
        }
        return result;
    }

    //申请权限
    private void requestAlertWindowPermission() {
        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
        intent.setData(Uri.parse("package:" + getPackageName()));
        startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    //处理回调
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (Settings.canDrawOverlays(this)) {
                    Log.i(TAG, "onActivityResult granted");
                    showFloat();
                } else {
                    Log.i(TAG, "onActivityResult noGranted!");
                }
            }
        }
    }

    /**
     * 重新打开app恢复悬浮球
     */
    @Override
    protected void onResume() {
        super.onResume();
        if (isShow) {
            showFloat();
        }
    }

    /**
     * 点击HOME键位悬浮球消失
     */
    @Override
    protected void onPause() {
        super.onPause();
        if (mFloatWindow != null) {
            mFloatWindow.dismiss();
        }
    }
}
