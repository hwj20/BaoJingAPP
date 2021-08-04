package com.example.baojing;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.EditText;

public class CallSettingActivity extends AppCompatActivity {

    EditText call_phone_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_setting);
        call_phone_text = findViewById(R.id.call_phone_text);
        call_phone_text.setText(getCallPhone());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        saveCallPhone(call_phone_text.getText().toString());
    }

    private void saveCallPhone(String phone){
        SharedPreferences callPhoneInfo = getSharedPreferences("callPhoneInfo",0);
        SharedPreferences.Editor editor = callPhoneInfo.edit();
        editor.putString("callPhone", phone);
        editor.commit();
    }


    private String getCallPhone(){
        SharedPreferences callPhoneInfo = getSharedPreferences("callPhoneInfo",0);
        return callPhoneInfo.getString("callPhone", "");
    }
}