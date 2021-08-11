package com.example.baojing;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SubscriptionManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.Toast;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class SettingActivity extends AppCompatActivity {

    CheckBox gmsCheckBox, callCheckBox;
    Spinner cardSelectSpinner;
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


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, 1);
            return;
        }

        cardSelectSpinner = findViewById(R.id.phone_card_select);
        int cardCount = SubscriptionManager.from(this).getActiveSubscriptionInfoCount();
        List<String> spinnerList = new ArrayList<String>();
        for(int i = 1; i <= cardCount; i++){
            spinnerList.add(String.valueOf(i)+'卡');
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item,spinnerList);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        cardSelectSpinner.setAdapter(adapter);
        cardSelectSpinner.setSelection(getCardSelect()-1);
//        Toast toast = Toast.makeText(getApplicationContext(), "card:"+getCardSelect(), Toast.LENGTH_SHORT);
//        toast.show();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        saveEnableInfo();
    }


//    void setSmsCard(int simID){
//        if(simID == 0){
//
//        }
//        else if(simID == 1)
//        try {
//            Method method = Class.forName("android.os.ServiceManager").getMethod("getService", String.class);
//            method.setAccessible(true);
//            Object param = method.invoke(null, cardName);
//        } catch (NoSuchMethodException e) {
//            e.printStackTrace();
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        }
//    }


    boolean getGmsEnable(){
        SharedPreferences enableInfo = getSharedPreferences("enableInfo", 0);
        return enableInfo.getBoolean("gmsEnable", false);
    }


    boolean getCallEnable(){
        SharedPreferences enableInfo = getSharedPreferences("enableInfo", 0);
        return enableInfo.getBoolean("callEnable", false);
    }

    int getCardSelect(){
        SharedPreferences enableInfo = getSharedPreferences("enableInfo", 0);
        return enableInfo.getInt("cardSelect", 1);
    }


    void saveEnableInfo(){
        SharedPreferences enableInfo = getSharedPreferences("enableInfo", 0);
        SharedPreferences.Editor editor = enableInfo.edit();
        editor.putBoolean("gmsEnable", gmsCheckBox.isChecked());
        editor.putBoolean("callEnable", callCheckBox.isChecked());
        editor.putInt("cardSelect", cardSelectSpinner.getSelectedItemPosition()+1);
        editor.commit();
    }
}