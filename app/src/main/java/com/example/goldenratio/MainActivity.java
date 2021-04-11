package com.example.goldenratio;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.StrictMode;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements DailogAddNewChapter.DialogListner {
    private MenuItem item;
    private RecyclerView recyclerView;
    private Button btn, btn2;
    static private TextView textView;
    ArrayList<ChapterCardsDetails> arr;
    static ChapterRecViewAdapter adapter;
    DataBaseHelper db;
    int back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE );
        // very very important
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());        
        
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE }, PackageManager.PERMISSION_GRANTED);

        Intent intent = getIntent();
        String action = intent.getAction();

        if(action == "android.intent.action.VIEW"){
            String selectedFile = intent.getData().getPath();
            String filename= selectedFile.substring(selectedFile.lastIndexOf("/")+1);
            selectedFile = selectedFile.substring(selectedFile.lastIndexOf(":")+1);


            try {
                File myFile = new File(selectedFile);
                FileInputStream fIn = new FileInputStream( myFile );
                BufferedReader myReader = new BufferedReader( new InputStreamReader(fIn));
//                String aDataRow = "";
//                String aBuffer = "";

//                while ((aDataRow = myReader.readLine()) != null) {
//                    aBuffer+= aDataRow + "\n";
//                }

//                Toast.makeText(this, aBuffer, Toast.LENGTH_SHORT).show();
                myReader.close();

                Toast.makeText(this, filename, Toast.LENGTH_SHORT).show();
                Toast.makeText(getBaseContext(),"Done reading file "+filename+" from sdcard",Toast.LENGTH_SHORT).show();}
            catch (Exception e) {
                Toast.makeText(getBaseContext(), e.getMessage()+"000000",Toast.LENGTH_SHORT).show();
            }

        }
        
        
        textView = findViewById(R.id.emptydbtext);
        recyclerView = findViewById(R.id.rvListOfChap);
        db = new DataBaseHelper(this);
        arr = db.is_there_table();
        back = 0;

        if (arr.isEmpty()) {
            textView.setVisibility(View.VISIBLE);
        } else {
            textView.setVisibility(View.GONE);
        }

        adapter = new ChapterRecViewAdapter(this);

        adapter.setCards(arr);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        btn = findViewById(R.id.addchapterbtn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                openDialog();
            }
        });

    }




    public void openDialog() {
        DailogAddNewChapter dailogAddNewChapter = new DailogAddNewChapter();
        dailogAddNewChapter.show(getSupportFragmentManager(), "example dailog");

    }

    @Override
    public void applychanges(String questionName) {

        db.AddTable(questionName,MainActivity.this);

        arr = db.is_there_table();
        adapter.setCards(arr);
        adapter.notifyDataSetChanged();
        if (arr.isEmpty()) {
            textView.setVisibility(View.VISIBLE);
        } else {
            textView.setVisibility(View.GONE);
        }
    }


    static public void change(ArrayList<ChapterCardsDetails>arr){
        if (arr.isEmpty()) {
            textView.setVisibility(View.VISIBLE);
        } else {
            textView.setVisibility(View.GONE);
        }
        adapter.setCards(arr);
        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode== KeyEvent.KEYCODE_BACK){
            if (back == 0) {
                CountDownTimer countDownTimer = new CountDownTimer(1000,200) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        back = 1;
                    }

                    @Override
                    public void onFinish() {
                        back = 0;
                    }

                };
                countDownTimer.start();
                Toast.makeText(MainActivity.this,"press again to go back",Toast.LENGTH_SHORT).show();
                return true;
            }
            else{
                Intent resultIntent = new Intent();
                setResult(RESULT_OK,resultIntent);
                finish();
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    public void open_on_start() {
        Intent i = getIntent();
        Uri data = i.getData();
        if (data == null)
        {
            return;
        }
        URL url;
        String startFile = "";
        try
        {
            url = new URL(data.getScheme(), data.getHost(), data.getPath());
            startFile = url.toString().replace("file:", "");
        }
        catch (Exception ex)
        {
            Toast.makeText(this, "Error:\n" + ex.getMessage().toString(), Toast.LENGTH_LONG).show();
            return;
        }
        if (startFile == null)
        {
            return;
        }
        StringBuilder text = new StringBuilder();
        boolean can = false;
        boolean sel = false;
    }

}