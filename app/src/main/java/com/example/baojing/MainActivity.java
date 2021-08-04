package com.example.baojing;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.telephony.PhoneNumberUtils;
import android.text.Editable;
import android.util.ArraySet;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import android.telephony.SmsManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

public class MainActivity extends AppCompatActivity {

    String msg_test = "test";
    String gps_msg = "";
    TextView gps_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // gps text
        gps_text = findViewById(R.id.gps_text);
        getGPS();

        // setting button
        ImageView image_setting = (ImageView) findViewById(R.id.image_setting);
        image_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,SettingActivity.class);
                startActivity(intent);
            }
        });

        // read Info
        String phoneCall = getCallPhone();
        String msg = getGmsTextFile();
        List<String> phoneList = getContactList();
        boolean enableGms = getGmsEnable(), enableCall = getCallEnable(), enableGPSSend = true;

        // start button
        Button  button_start = (Button) findViewById(R.id.button_start);
        button_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(enableGms) {
                    String sendMsg = msg;
                    if(enableGPSSend){
                        sendMsg = sendMsg + gps_msg;
                    }
                    for (String phone : phoneList) {
                        sendSMS(phone, sendMsg);
                    }
                }
//                phoneCall = editTextPhoneNum.getText().toString();
                if(enableCall) {
                    callPhone(phoneCall);
                }
            }
        });
    }

    public void sendSMS(String phoneNumber,String message){
        if(PhoneNumberUtils.isGlobalPhoneNumber(phoneNumber)){
            int checkCallPhonePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS);
            if (checkCallPhonePermission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, 1);
                return;
            }
            SmsManager smsManager = SmsManager.getDefault();
            ArrayList<String> list = smsManager.divideMessage(message);
            for (int i = 0; i < list.size(); i++) {
                Log.d("sendM", list.get(i));
                smsManager.sendTextMessage(phoneNumber, null, list.get(i), null, null);
            }
        }
    }


    public void getGPS(){
        Log.d("trygps", "1");
        int checkGPSPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        if (checkGPSPermission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }

        FusedLocationProviderClient fusedLocationProvider = LocationServices.getFusedLocationProviderClient(this);
//        gps_text.setText(fusedLocationProvider.getLastLocation());
        LocationCallback locationCallback = new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult) {
                Log.d("getone", "1");
                if(locationResult == null){
                    return;
                }
                for(Location location: locationResult.getLocations()) {
                    gps_msg = "位置: 经度" + location.getLongitude()+" 纬度"+ location.getLatitude();
                    Log.d("gpsh", gps_msg);
                    gps_text.setText(gps_msg);
                }
            }
        };

        LocationRequest request = LocationRequest.create();
        request.setInterval(10000);
        request.setFastestInterval(5000);
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        fusedLocationProvider.requestLocationUpdates(request, locationCallback, Looper.getMainLooper());
    }


    public static final String[] dualSimTypes = { "subscription", "Subscription",
            "com.android.phone.extra.slot",
            "phone", "com.android.phone.DialingMode",
            "simId", "simnum", "phone_type",
            "simSlot" };

    public void callPhone(String phoneNum){
        int checkCallPhonePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE);
        if (checkCallPhonePermission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, 1);
            return;
        }
        Intent intent = new Intent(Intent.ACTION_CALL);
        Uri data = Uri.parse("tel:" + phoneNum);
        intent.setData(data);
        for (String dualSimType : dualSimTypes) {
            intent.putExtra(dualSimType, 1);
        }
        startActivity(intent);
    }

    private List<String> getContactList(){
        SharedPreferences contactInfo = getSharedPreferences("contactInfo",0);
        Set<String> stringSet = contactInfo.getStringSet("contactList", new ArraySet<>());
        return new ArrayList<>(stringSet);
    }

    private String getGmsTextFile(){
        SharedPreferences gmsInfo = getSharedPreferences("gmsInfo",0);
        return gmsInfo.getString("gmsText","");
    }

    private String getCallPhone(){
        SharedPreferences callPhoneInfo = getSharedPreferences("callPhoneInfo",0);
        return callPhoneInfo.getString("callPhone", "");
    }


    boolean getGmsEnable(){
        SharedPreferences enableInfo = getSharedPreferences("enableInfo", 0);
        return enableInfo.getBoolean("gmsEnable", false);
    }


    boolean getCallEnable(){
        SharedPreferences enableInfo = getSharedPreferences("enableInfo", 0);
        return enableInfo.getBoolean("callEnable", false);
    }
}

