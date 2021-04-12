package com.example.goldenratio;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.StrictMode;
import android.util.Base64;
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
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

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
            File myFile;
            InputStream fIn = null;

            try {
                selectedFile = selectedFile.substring(selectedFile.lastIndexOf("/storage"));
                myFile = new File(selectedFile);
                fIn = new FileInputStream( myFile );

            }catch (Exception e){
                //if file is opened from other apps rather than file explorer
                Uri text_file_data_other_apps = (Uri) intent.getParcelableExtra(Intent.EXTRA_TEXT);
                try {
                    fIn = getContentResolver().openInputStream(text_file_data_other_apps);
                } catch (FileNotFoundException fileNotFoundException) {
                    fileNotFoundException.printStackTrace();
                }
            }


            try {
                BufferedReader myReader = new BufferedReader( new InputStreamReader(fIn));
                String aDataRow = "";
                String aBuffer = "";

                while ((aDataRow = myReader.readLine()) != null) {
                    aBuffer+= aDataRow + "\n";
                }

                String unHashed = hashTomsg(aBuffer,"questionShared");
                String[] data = unHashed.split("---new---");
                String chapter_name = unHashed.split("---new---")[0];
                DataBaseHelper db = new DataBaseHelper(MainActivity.this);

                try {

                    db.AddTable(chapter_name,MainActivity.this);
                }catch (Exception e){
                    Toast.makeText(this, chapter_name+" already exits, rename or delete exitsing chapter", Toast.LENGTH_SHORT).show();
                    db.close();
                    return;
                }

                String[] question_info;
                QuestionModel questionModel;
                for(int i = 1; i<data.length;i++){
                    question_info = data[i].split("-----");
                    questionModel = new QuestionModel(question_info[0],question_info[1],question_info[2],question_info[3],question_info[4],Integer.parseInt(question_info[5]));
                    db.add_question_to_table(chapter_name,questionModel);
                }
                db.close();
                adapter.notifyDataSetChanged();
                Toast.makeText(this, chapter_name+" chpater has been added to the book \n if already exitsted question would get mergerd", Toast.LENGTH_SHORT).show();
                myReader.close();
            }
            catch (Exception e) {
                Toast.makeText(getBaseContext(), e.getMessage(),Toast.LENGTH_SHORT).show();
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
    private String hashTomsg(String hash, String key) throws UnsupportedEncodingException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        if(hash==null){
            hash = "";
        }
        if(key==null){
            key = "";
        }
        SecretKeySpec secretKeySpec = generateKey(key);
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE,secretKeySpec);
        byte[] decodedValue = Base64.decode(hash,Base64.DEFAULT);
        byte[] decValue = cipher.doFinal(decodedValue);
        String decrypted_msg = new String(decValue);
        return decrypted_msg;
    }
    private SecretKeySpec generateKey(String passwaord) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        final MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
        byte[] bytes = passwaord.getBytes("UTF-8");
        messageDigest.update(bytes,0,bytes.length);
        byte[] key = messageDigest.digest();
        SecretKeySpec secretKeySpec = new SecretKeySpec(key,"AES");
        return  secretKeySpec;
    }
}