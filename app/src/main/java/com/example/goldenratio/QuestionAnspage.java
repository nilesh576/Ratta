package com.example.goldenratio;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Base64;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

public class QuestionAnspage extends AppCompatActivity {
    Button backbtn,nextbtn,deletebtn,hash_share_btn;
    RadioGroup option_grp;
    RadioButton Arb,Brb,Crb,Drb;
    TextView questiontextview;
    String table_name;
    int offest_by;
    Switch aSwitch;
    String  ques;
    String ans;
    int back;
    ClipboardManager clipboard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_anspage);
        offest_by = 0;
        back=0;
        clipboard = (ClipboardManager) getSystemService(QuestionAnspage.this.CLIPBOARD_SERVICE);
        aSwitch = findViewById(R.id.switch1);
        backbtn = findViewById(R.id.buttonback);
        nextbtn = findViewById(R.id.buttonnext);
        deletebtn = findViewById(R.id.buttondel);
        option_grp = findViewById(R.id.optionsgroup);
        hash_share_btn = findViewById(R.id.share_btn);
        questiontextview = findViewById(R.id.textView);
        Arb = findViewById(R.id.optiona);
        Brb = findViewById(R.id.optionb);
        Crb = findViewById(R.id.optionc);
        Drb = findViewById(R.id.optiond);
        Intent i = getIntent();
        aSwitch.setChecked(true);
        table_name = i.getStringExtra("chaptername");

        changeQuestion(offest_by);

        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(aSwitch.isChecked()){
                    DataBaseHelper db = new DataBaseHelper(QuestionAnspage.this);
                    int size = db.getSize(table_name);
                    Random a = new Random();
                    offest_by =  a.nextInt(size);
                }else{
                    offest_by--;
                }
                changeQuestion(offest_by);
            }
        });
        nextbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(aSwitch.isChecked()){
                    DataBaseHelper db = new DataBaseHelper(QuestionAnspage.this);
                    int size = db.getSize(table_name);
                    Random a = new Random();
                    offest_by =  a.nextInt(size);
                }else{
                    offest_by++;
                }
                changeQuestion(offest_by);
            }
        });

        deletebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DataBaseHelper db = new DataBaseHelper(QuestionAnspage.this);

                Dialog dialog = new Dialog(v.getContext());
                dialog.setContentView(R.layout.pop_for_final_confirmation_for_delete_request_layout);
                TextView textView =  dialog.findViewById(R.id.deletechapettextview);
                textView.setText("Delete This Question?");
                Button yesbutton = dialog.findViewById(R.id.yes);
                Button nobutton = dialog.findViewById(R.id.no);
                dialog.show();

                yesbutton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        db.delete_row_in_table(table_name,ques);
                        offest_by--;
                        changeQuestion(offest_by);
                        dialog.dismiss();
                        Toast.makeText(v.getContext(),""+ques+" has been delete ",Toast.LENGTH_SHORT).show();
                    }
                });
                nobutton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        return;
                    }
                });



            }
        });

        option_grp.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId==-1){
                    return ;
                }

                group.getCheckedRadioButtonId();
                RadioButton ansrb = findViewById(checkedId);
                Arb.setBackgroundColor( getResources().getColor(R.color.black));
                Arb.setTextColor(getResources().getColor(R.color.white));

                Brb.setBackgroundColor( getResources().getColor(R.color.black));
                Brb.setTextColor(getResources().getColor(R.color.white));

                Crb.setBackgroundColor( getResources().getColor(R.color.black));
                Crb.setTextColor(getResources().getColor(R.color.white));

                Drb.setBackgroundColor( getResources().getColor(R.color.black));
                Drb.setTextColor(getResources().getColor(R.color.white));

                ansrb.setBackgroundColor( getResources().getColor(R.color.red));
                switch (ans){
                    case "1":
                        Arb.setBackgroundColor( getResources().getColor(R.color.green));
                        break;
                    case "2":
                        Brb.setBackgroundColor( getResources().getColor(R.color.green));
                        break;
                    case "3":
                        Crb.setBackgroundColor( getResources().getColor(R.color.green));
                        break;
                    case "4":
                        Drb.setBackgroundColor( getResources().getColor(R.color.green));
                        break;
                }
            }
        });
        hash_share_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String optionToBeSelected = "11";

                switch (ans){
                    case "1":{
                        optionToBeSelected = "11";
                        break;
                    }
                    case "2":{
                        optionToBeSelected = "12";
                        break;
                    }
                    case "3":{
                        optionToBeSelected = "13";
                        break;
                    }
                    case "4":{
                        optionToBeSelected = "14";
                        break;
                    }
                }
                String beforeCryption = questiontextview.getText().toString()+"---"+Arb.getText().toString()+"---"+Brb.getText().toString()+"---"
                        +Crb.getText().toString()+"---"+Drb.getText().toString()+"---"+optionToBeSelected;
                String encrypted = "";
                try {
                    encrypted = msgToHash(beforeCryption,"Ratta");
                } catch (Exception e){

                }
//                ClipData clip = ClipData.newPlainText("simple text", msgToHash(beforeCryption,"Ratta"));
//                clipboard.setPrimaryClip(clip);
//                Toast.makeText(QuestionAnspage.this,"failed"+beforeCryption,Toast.LENGTH_SHORT).show();
                ApplicationInfo api = getApplicationContext().getApplicationInfo();
                String apkpath = api.sourceDir;
                Intent intent = new Intent(Intent.ACTION_SEND);

                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, encrypted);
                startActivity(Intent.createChooser(intent,"shareVia"));
                return;


            }
        });
    }
    String msgToHash(String msg, String key) throws UnsupportedEncodingException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        if(key==null){
            key = "";
        }
        if(msg==null){
            msg = "";
        }
        SecretKeySpec secretKeySpec = generateKey(key);
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE,secretKeySpec);
        byte[] encVal = cipher.doFinal(msg.getBytes());
        String hash = Base64.encodeToString(encVal,Base64.DEFAULT);
        return hash;
    }

    SecretKeySpec generateKey(String passwaord) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        final MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
        byte[] bytes = passwaord.getBytes("UTF-8");
        messageDigest.update(bytes,0,bytes.length);
        byte[] key = messageDigest.digest();
        SecretKeySpec secretKeySpec = new SecretKeySpec(key,"AES");
        return  secretKeySpec;
    }

    public  void changeQuestion(int offset){
        DataBaseHelper db = new DataBaseHelper(QuestionAnspage.this);
        int size = db.getSize(table_name);
        if(size==0 || offset>=size|| offset<0){
            Intent resultIntent = new Intent();
            resultIntent.putExtra("result","it happend");
            setResult(RESULT_OK,resultIntent);
            MainActivity.change(db.is_there_table());
            db.close();
            finish();
        }
        else {
            option_grp.clearCheck();

            String[] arr = db.get_question(table_name, offset);
            ques = arr[0];
            questiontextview.setText(arr[0]);

            Arb.setText(arr[1]);
            Arb.setBackgroundColor( getResources().getColor(R.color.black));

            Brb.setText(arr[2]);
            Brb.setBackgroundColor( getResources().getColor(R.color.black));

            Crb.setText(arr[3]);
            Crb.setBackgroundColor( getResources().getColor(R.color.black));

            Drb.setText(arr[4]);
            Drb.setBackgroundColor( getResources().getColor(R.color.black));

            ans = arr[5];

        }
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
                Toast.makeText(QuestionAnspage.this,"press again to go back",Toast.LENGTH_SHORT).show();
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


}