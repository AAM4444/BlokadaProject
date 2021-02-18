package com.example.blokadaproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.example.blokadaproject.adapter.RecyclerViewAdapter;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class MainActivity extends AppCompatActivity {

    public ArrayList<String> appNameList, hashSumList, sourceDirList;
    public List<ApplicationInfo> appList;
    private RecyclerView recyclerView;
    private RecyclerViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.rv_main);

        sourceDirList = new ArrayList<>();
        appNameList = new ArrayList<>();
        hashSumList = new ArrayList<>();
        appList = getApplicationList(this);

        //fill PackageList
        for (ApplicationInfo al : appList) {
            String path = al.sourceDir;
            sourceDirList.add(path);
        }

        final Handler handler = new Handler();
        Thread thread = new Thread() {
            @Override
            public void run() {

                //fill the array with null objects
                for (int i = 0; i < appList.size(); i++) {
                    hashSumList.add(null);
                }

                //fill the array with null objects
                for (int i = 0; i < appList.size(); i++) {
                    appNameList.add(null);
                }

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        //set Adapter with arrays full of null
                        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                        adapter = new RecyclerViewAdapter(appNameList, hashSumList, getApplicationContext());
                        recyclerView.setAdapter(adapter);
                        Timber.d("appNameList size before = %s", appNameList.size());
                    }
                });

                for (int j = 0; j < sourceDirList.size(); j++) {
                    final int i = j;
                    String path = sourceDirList.get(i);
                    String applicName = applicationLabel(getApplicationContext(), appList.get(i));
                    appNameList.set(i, applicName);

                    File file = new File(path);
                    MessageDigest shaDigest = null;
                    String shaChecksum;
                    try {
                        shaDigest = MessageDigest.getInstance("SHA-256");
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    }
                    try {
                        shaChecksum = getFileChecksum(shaDigest, file);
                        hashSumList.set(i, shaChecksum);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    handler.post(new Runnable(){
                        @Override
                        public void run() {
                            adapter.notifyItemChanged(i);

                        }
                    });
                }
            }
        };

        thread.start();

    }

    public String applicationLabel(Context context, ApplicationInfo info) {
        PackageManager p = context.getPackageManager();
        String label = p.getApplicationLabel(info).toString();
        return label;
    }

    public List<ApplicationInfo> getApplicationList(Context context){
        PackageManager p = context.getPackageManager();
        List<ApplicationInfo> info = p.getInstalledApplications(0);
        return info;
    }

    private static String getFileChecksum (MessageDigest digest, File file) throws IOException {

        FileInputStream fis = new FileInputStream(file);

        byte[] byteArray = new byte[1024];
        int bytesCount = 0;

        while ((bytesCount = fis.read(byteArray)) != -1) {
            digest.update(byteArray, 0, bytesCount);
        };

        fis.close();

        byte[] bytes = digest.digest();

        StringBuilder sb = new StringBuilder();
        for(int i=0; i< bytes.length ;i++)
        {
            sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
        }

        return sb.toString();
    }

}