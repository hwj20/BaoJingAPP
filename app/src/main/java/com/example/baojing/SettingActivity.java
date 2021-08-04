package com.example.baojing;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

public class SettingActivity extends AppCompatActivity {

    CheckBox gmsCheckBox, callCheckBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        Button button_gms_setting = (Button) findViewById(R.id.button_gms_setting);
        button_gms_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SettingActivity.this, GmsSettingActivity.class);
                startActivity(intent);
            }
        });


        Button button_call_setting = findViewById(R.id.button_call_setting);
        button_call_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SettingActivity.this, CallSettingActivity.class);
                startActivity(intent);
            }
        });

        gmsCheckBox = findViewById(R.id.gms_enable);
        gmsCheckBox.setChecked(getGmsEnable());

        callCheckBox = findViewById(R.id.call_enable);
        callCheckBox.setChecked(getCallEnable());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        saveEnableInfo();
    }

    boolean getGmsEnable(){
        SharedPreferences enableInfo = getSharedPreferences("enableInfo", 0);
        return enableInfo.getBoolean("gmsEnable", false);
    }


    boolean getCallEnable(){
        SharedPreferences enableInfo = getSharedPreferences("enableInfo", 0);
        return enableInfo.getBoolean("callEnable", false);
    }


    void saveEnableInfo(){
        SharedPreferences enableInfo = getSharedPreferences("enableInfo", 0);
        SharedPreferences.Editor editor = enableInfo.edit();
        editor.putBoolean("gmsEnable", gmsCheckBox.isChecked());
        editor.putBoolean("callEnable", callCheckBox.isChecked());
        editor.commit();
    }
}