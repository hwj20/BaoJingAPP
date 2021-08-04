package com.example.baojing;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.ArraySet;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GmsSettingActivity extends AppCompatActivity {
    private final String TAG  = "GmsSetting";
    EditText gmsText;
    ContactAdapter contactAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_gms);

        gmsText = findViewById(R.id.gms_text);
        gmsText.setText(getGmsTextFile());

        List<String> contactList = getContextList();
//        List<String> contactList = new ArrayList<>();
        RecyclerView recyclerView = findViewById(R.id.contact_list);
        contactAdapter = new ContactAdapter(this, contactList);

        Button button_add_contact = findViewById(R.id.button_add_contact);
        button_add_contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                contactAdapter.addAndSelect();
//                contactAdapter.setFocusOnLast();
            }
        });

        Button button_edit_contact = findViewById(R.id.button_edit_contact);
        button_edit_contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                contactAdapter.setEdit();
            }
        });

        Button button_delete_contact = findViewById(R.id.button_delete_contact);
        button_delete_contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                contactAdapter.setDelete();
            }
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(contactAdapter);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        saveGmsTextFile(gmsText.getText().toString());
        saveContact(contactAdapter.getData());
        Toast toast = Toast.makeText(getApplicationContext(), "已保存", Toast.LENGTH_SHORT);
        toast.show();
    }


    private List<String> getContextList(){
        SharedPreferences contactInfo = getSharedPreferences("contactInfo",0);
        Set<String> stringSet = contactInfo.getStringSet("contactList", new ArraySet<>());
        return new ArrayList<>(stringSet);
    }


    private String getGmsTextFile(){
        SharedPreferences gmsInfo = getSharedPreferences("gmsInfo",0);
        return gmsInfo.getString("gmsText","");
    }


    private void saveGmsTextFile(String gms){
        SharedPreferences gmsInfo = getSharedPreferences("gmsInfo",0);
        SharedPreferences.Editor editor = gmsInfo.edit();
        editor.putString("gmsText", gms);
        editor.commit();
    }

    private void saveContact(List<String> contactList){
        SharedPreferences contactInfo = getSharedPreferences("contactInfo",0);
        SharedPreferences.Editor editor = contactInfo.edit();
        Set<String> stringSet = new ArraySet<String>();
        stringSet.addAll(contactList);
        editor.putStringSet("contactList", stringSet);
        editor.commit();
    }
}