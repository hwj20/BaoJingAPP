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
import android.os.Message;
import android.telephony.PhoneNumberUtils;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.text.Editable;
import android.util.ArraySet;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import android.telephony.SmsManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.baojing.gpstools.GPS;
import com.example.baojing.gpstools.GPSConverterUtils;
import com.example.baojing.gpstools.LongClickView;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    String msg_test = "test";
    String key = "8571538e1f074b5bf2e86d7c9bc049b0";
    String gps_msg = "";
    String formatted_address = "";
    GPS gps = new GPS(0, 0);
    TextView gps_text;
    // read Info
    String phoneCall;
    String msg;
    List<String> phoneList;
    boolean enableGms = false, enableCall = false, enableGPSSend = true;


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
                Intent intent = new Intent(MainActivity.this, SettingActivity.class);
                startActivity(intent);
            }
        });

        readInfo();

        // start button
//        Button button_start = (Button) findViewById(R.id.button_start);
//        button_start.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//        readInfo();
//                if (enableGms) {
//                    String sendMsg = msg;
//                    if (enableGPSSend) {
//                        sendMsg = sendMsg + gps_msg;
//                    }
//                    for (String phone : phoneList) {
//                        sendSMS(phone, sendMsg);
//                    }
//                }
////                phoneCall = editTextPhoneNum.getText().toString();
//                if (enableCall) {
//                    callPhone(phoneCall);
//                }
//            }
//        });

        LongClickView mLongClickView = findViewById(R.id.long_click_start);
        mLongClickView.setMyClickListener(new LongClickView.MyClickListener() {
            @Override
            public void longClickFinish() {
            }

            @Override
            public void allFinish() {
                readInfo();
                if (enableGms) {
                    String sendMsg = msg;
                    if (enableGPSSend) {
                        sendMsg = sendMsg + gps_msg;
                    }
                    for (String phone : phoneList) {
                        sendSMS(phone, sendMsg);
                    }
                }
                if (enableCall) {
                    callPhone(phoneCall);
                }
            }

            @Override
            public void singleClickFinish() {
            }
        });
    }


    public void sendSMS(String phoneNumber, String message) {
        if (PhoneNumberUtils.isGlobalPhoneNumber(phoneNumber)) {
            int checkCallPhonePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS);
            if (checkCallPhonePermission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, 1);
                return;
            }
            SmsManager smsManager = SmsManager.getDefault();
            ArrayList<String> list = smsManager.divideMessage(message);

            SubscriptionManager localSubscriptionManager = SubscriptionManager.from(getApplicationContext());
            if (localSubscriptionManager.getActiveSubscriptionInfoCount() > 1) {
                List localList = localSubscriptionManager.getActiveSubscriptionInfoList();

                SubscriptionInfo simInfo = (SubscriptionInfo) localList.get(getCardSelect()-1);

                for (int i = 0; i < list.size(); i++) {
                    Log.d("sendM", list.get(i));
                    SmsManager.getSmsManagerForSubscriptionId(simInfo.getSubscriptionId()).sendTextMessage(phoneNumber, null, list.get(i), null, null);
                }
            }
        }
    }


    public void getGPS() {
        int checkGPSPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        if (checkGPSPermission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }

        Toast toast = Toast.makeText(getApplicationContext(), "Try GPS", Toast.LENGTH_SHORT);
        toast.show();

        FusedLocationProviderClient fusedLocationProvider = LocationServices.getFusedLocationProviderClient(this);
//        gps_text.setText(fusedLocationProvider.getLastLocation());
        LocationCallback locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    Log.d("gpsh", "get one");
                    gps.setLat(location.getLatitude());
                    gps.setLon(location.getLongitude());
                    gps = GPSConverterUtils.gps84_To_Gcj02(gps.getLat(),gps.getLon());

                    gps_msg = "参考位置:" + gps.getLon() + "," + gps.getLat();
                    Log.d("gpsh", gps_msg);
                    httpRegeocode();
                }
            }
        };

        LocationRequest request = LocationRequest.create();
        Log.d("gpsh", "begin requiring");
        request.setInterval(10000);
        request.setFastestInterval(10000);
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        fusedLocationProvider.requestLocationUpdates(request, locationCallback, Looper.getMainLooper());
    }


    public static final String[] dualSimTypes = {"subscription", "Subscription",
            "com.android.phone.extra.slot",
            "phone", "com.android.phone.DialingMode",
            "simId", "simnum", "phone_type",
            "simSlot"};

    public void callPhone(String phoneNum) {
        int checkCallPhonePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE);
        int cardSelection = getCardSelect();
        if (checkCallPhonePermission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, 1);
            return;
        }
        Intent intent = new Intent(Intent.ACTION_CALL);
        Uri data = Uri.parse("tel:" + phoneNum);
        intent.setData(data);
        for (String dualSimType : dualSimTypes) {
            intent.putExtra(dualSimType, cardSelection);
        }
        startActivity(intent);
    }

    private List<String> getContactList() {
        SharedPreferences contactInfo = getSharedPreferences("contactInfo", 0);
        Set<String> stringSet = contactInfo.getStringSet("contactList", new ArraySet<>());
        return new ArrayList<>(stringSet);
    }

    private String getGmsTextFile() {
        SharedPreferences gmsInfo = getSharedPreferences("gmsInfo", 0);
        return gmsInfo.getString("gmsText", "");
    }

    private String getCallPhone() {
        SharedPreferences callPhoneInfo = getSharedPreferences("callPhoneInfo", 0);
        return callPhoneInfo.getString("callPhone", "");
    }


    boolean getGmsEnable() {
        SharedPreferences enableInfo = getSharedPreferences("enableInfo", 0);
        return enableInfo.getBoolean("gmsEnable", false);
    }

    int getCardSelect(){
        SharedPreferences enableInfo = getSharedPreferences("enableInfo", 0);
        return enableInfo.getInt("cardSelect", 1);
    }


    boolean getCallEnable() {
        SharedPreferences enableInfo = getSharedPreferences("enableInfo", 0);
        return enableInfo.getBoolean("callEnable", false);
    }

    void readInfo(){
        phoneCall = getCallPhone();
        msg = getGmsTextFile();
        phoneList = getContactList();
        enableGms = getGmsEnable();
        enableCall = getCallEnable();
        enableGPSSend = true;
    }


    private void httpRegeocode() {
        new Thread(new Runnable() {

            @Override
            public void run() {
                InputStreamReader in = null;
                HttpURLConnection httpUrlConnection = null;
                try {
                    String urlString = "https://restapi.amap.com/v3/geocode/regeo?output=JSON&location="
                            +gps.getLon()+','+gps.getLat()+"&key="+key+"&radius=1000&extensions=all";

                    URL url = new URL(urlString);
                    httpUrlConnection = (HttpURLConnection) url.openConnection();
                    httpUrlConnection.setRequestMethod("GET"); //设置请求方法
                    httpUrlConnection.setConnectTimeout(8000); //设置链接超时的时间
                    //将读超时设置为指定的超时值，以毫秒为单位。用一个非零值指定在建立到资源的连接后从input流读入时的超时时间。
                    //如果在数据可读取之前超时期满，则会引发一个 java.net.sockettimeoutexception。超时时间为零表示无穷大超时。
                    httpUrlConnection.setReadTimeout(8000);
                    httpUrlConnection.setDoInput(true); //允许输入流，即允许下载
                    httpUrlConnection.setDoOutput(true); //允许输出流，即允许上传
                    httpUrlConnection.setUseCaches(false); //设置是否使用缓存
                    //建立连接，上面对urlConn的所有配置必须要在connect之前完，这里需要注意的是
                    //connect这个方法，在getInputStream()方法中会隐式的被调用，所以这里不写也没有问题
                    httpUrlConnection.connect();
                    InputStream inputStream = httpUrlConnection.getInputStream();
                    in = new InputStreamReader(inputStream);
                    BufferedReader bf = new BufferedReader(in);
                    StringBuilder sb = new StringBuilder();
                    String inputLine = null;

                    while ((inputLine = bf.readLine()) != null) {
                        sb.append(inputLine);
                    }
                    Log.d("Regeo", sb.toString());

                    // 解析
                    JSONObject jo = new JSONObject(sb.toString());
                    JSONObject regjo = (JSONObject)jo.get("regeocode");
                    formatted_address = regjo.getString("formatted_address");
                    Log.d("Regeo", formatted_address);
                    gps_msg += " "+formatted_address;

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (httpUrlConnection != null) {
                        httpUrlConnection.disconnect();
                    }
                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            gps_text.setText(gps_msg);
                        }
                    });
                }
            }
        }).start();

    }

}

