package com.example.goldenratio;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.FileObserver;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class openTxtActivity extends Activity {
    DataBaseHelper dataBaseHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        dataBaseHelper = new DataBaseHelper(this);
//        dataBaseHelper.AddTable("s",this);
        Intent intent = getIntent();
        Uri data = intent.getData();
        String action = intent.getAction();
        String type = intent.getType();
        intent.getExtras();

        String sharedText = intent.getStringExtra(Intent.EXTRA_STREAM);
//        File file = new File(data.getPath());
//        FileOutputStream fos = null;
//        try {
//            fos = new FileOutputStream(file);
//            Toast.makeText(this, "file found", Toast.LENGTH_SHORT).show();
//        } catch (FileNotFoundException e) {
//            Toast.makeText(this, "not file found", Toast.LENGTH_SHORT).show();
//
//            e.printStackTrace();
//        }
        Toast.makeText(this, type+"\n"+intent.getExtras(), Toast.LENGTH_SHORT).show();

    }
}
