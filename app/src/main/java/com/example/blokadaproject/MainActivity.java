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

public class MainActivity extends AppCompatActivity {

    public ArrayList<String> AppName, HashSum, PackageList;
    public List<ApplicationInfo> AppList;
    private RecyclerView recyclerView;
    private RecyclerViewAdapter adapter;
    private MainActivity mainActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.rv_main);

        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                1);

        mainActivity = new MainActivity();
        PackageList = new ArrayList<>();
        AppList = mainActivity.getApplicationList(this);
        AppName = new ArrayList<>();
        HashSum = new ArrayList<>();

        //fill PackageList
        for (ApplicationInfo al : AppList) {
            //ЗДЕСЬ ПОЛУЧАЮ ДИРЕКТОРИЮ МОИХ ФАЙЛОВ
            String path = al.sourceDir;
            PackageList.add(path);
        }

//        //fill the array with null objects
//        for (int i = 0; i < AppList.size(); i++) {
//            AppName.add(null);
//        }
//
//        //fill the array with null objects
//        for (int i = 0; i < AppList.size(); i++) {
//            HashSum.add(null);
//        }

        //set Adapter with arrays full of null

        //fill AppName with applic's name
        for (int i = 0; i < AppList.size(); i++) {
//            String applicName = mainActivity.applicationLabel(this, AppList.get(i));
//            AppName.add(applicName);
        }

        for (int i = 0; i < PackageList.size(); i++) {
            final String path = PackageList.get(i);
            String applicName = mainActivity.applicationLabel(this, AppList.get(i));
            AppName.add(applicName);
           Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    File file = new File(path);
                    MessageDigest shaDigest = null;
                    String shaChecksum = null;
                    try {
                        shaDigest = MessageDigest.getInstance("SHA-256");
//                        Log.d("TAG", " " + shaDigest);
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    }
                    try {
                        shaChecksum = getFileChecksum(shaDigest, file);
                        HashSum.add(shaChecksum);
                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.d("TAG", " " + e);
                    }

                }
            };
           
            Thread thread = new Thread(runnable);
            thread.start();
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RecyclerViewAdapter(AppName, HashSum, this);
        recyclerView.setAdapter(adapter);

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

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Toast.makeText(MainActivity.this, "Permission was granted", Toast.LENGTH_LONG).show();

                } else {

                    Toast.makeText(MainActivity.this, "Permission denied to read your External storage", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }

}
