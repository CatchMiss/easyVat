package com.moptim.easyvat.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.moptim.easyvat.R;
import com.moptim.easyvat.utils.SoundManager;

import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends AppCompatActivity  {

    private static final String TAG = LoginActivity.class.getSimpleName();

    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mHandler = new Handler();

        List<String> permissionList = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.CAMERA);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        }



        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Log.e(TAG, "System don't support bluetooth LE!");
        }

        SoundManager.getInstance().init(this);

        if (!permissionList.isEmpty()) {
            int size = permissionList.size();
            ActivityCompat.requestPermissions(this, permissionList.toArray(new String[size]), 100);
        } else {
            startHome();
        }
    }

    private void startHome() {
        mHandler.removeCallbacks(mStart);
        mHandler.postDelayed(mStart, 500);
    }

    private Runnable mStart = new Runnable() {
        @Override
        public void run() {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            if (grantResults.length > 0) {
                boolean permission = true;
                for (int i = 0; i < grantResults.length; i++) {
                    Log.e(TAG, permissions[i] + " =权限= " + grantResults[i]);
                    if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                        Log.e(TAG, permissions[i] + "权限被拒绝了");
                        permission = false;
                    }
                }

                if (permission) {
                    startHome();
                } else {
                    new AlertDialog.Builder(LoginActivity.this)
                            .setMessage("需要权限才能运行")
                            .setPositiveButton(getString(R.string.setting), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent();
                                    intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                                    intent.setData(Uri.parse("package:" + getPackageName()));
                                    startActivity(intent);
                                }
                            })
                            .create()
                            .show();
                }
            }
        }
        startHome();
    }
}
