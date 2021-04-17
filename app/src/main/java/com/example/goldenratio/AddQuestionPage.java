package com.example.goldenratio;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

public class AddQuestionPage extends AppCompatActivity {
    Button add_ques_btn;
    EditText opt1,opt2,opt3,opt4,question,add_hash_et;
    RadioButton rb1,rb2,rb3,rb4,ans;
    RadioGroup rg1;
    int correctAnswer;
    int back;
    Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_question_page);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE );

        context = AddQuestionPage.this;
        back=0;

        Intent resultIntent = getIntent();
        String table = resultIntent.getStringExtra("chaptername");

        opt1 = findViewById(R.id.editTextTextPersonName1);
        opt2 = findViewById(R.id.editTextTextPersonName2);
        opt3  = findViewById(R.id.editTextTextPersonName3);
        opt4 = findViewById(R.id.editTextTextPersonName4);
        question = findViewById(R.id.editTextTextPersonName);
        add_hash_et = findViewById(R.id.hash_et);
        rb1 = findViewById(R.id.radioButton);
        rb2 = findViewById(R.id.radioButton2);
        rb3 = findViewById(R.id.radioButton3);
        rb4 = findViewById(R.id.radioButton4);
        add_ques_btn = findViewById(R.id.add_button);
        rg1 = findViewById(R.id.radiogroup);

        speechToText(question);
        speechToText(opt1);
        speechToText(opt2);
        speechToText(opt3);
        speechToText(opt4);

        Intent n = getIntent();
        add_ques_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DataBaseHelper db = new DataBaseHelper(AddQuestionPage.this);

                if(rg1.getCheckedRadioButtonId()==-1|| opt1.getText().toString().equals("") || opt2.getText().toString().equals("")|| opt3.getText().toString().equals("")|| opt4.getText().toString().equals("")){
                    Toast.makeText(AddQuestionPage.this,"input all data before submitting",Toast.LENGTH_SHORT).show();
                }
                else{
                    ans = findViewById(rg1.getCheckedRadioButtonId());
                    correctAnswer = Integer.parseInt(ans.getText().toString());
                    QuestionModel q = new QuestionModel(question.getText().toString(),opt1.getText().toString(),opt2.getText().toString(),opt3.getText().toString(),opt4.getText().toString(),correctAnswer);

                    db.add_question_to_table((String) n.getCharSequenceExtra("chaptername"),  q);
                    Toast.makeText(AddQuestionPage.this,"Question has been added to the chapter\n"+table,Toast.LENGTH_SHORT).show();
                    MainActivity.change(db.is_there_table());
                }
                db.close();
            }
        });

        add_hash_et.addTextChangedListener(new TextWatcher() {
            String text;
            String unHashed;
            String[] values;
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.toString().equals("")){
                    return;
                }
                text = s.toString();
                try {
                    unHashed = hashTomsg(text,"Ratta");
                    values = unHashed.split("---");
                    if (unHashed!=null){
                        question.setText(values[0]);
                        opt1.setText(values[1]);
                        opt2.setText(values[2]);
                        opt3.setText(values[3]);
                        opt4.setText(values[4]);
                        switch (values[5]){
                            case "11":{
                                rg1.check(rb1.getId());
                                break;
                            }
                            case "12":{
                                rg1.check(rb2.getId());
                                break;
                            }
                            case "13":{
                                rg1.check(rb3.getId());
                                break;
                            }
                            case "14":{
                                rg1.check(rb4.getId());
                                break;
                            }
                        }
                    }
                } catch (Exception e){
                    Toast.makeText(context, "can't determine this input", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    String hashTomsg(String hash, String key) throws UnsupportedEncodingException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
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
        return new String(decValue);
    }
    SecretKeySpec generateKey(String passwaord) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        final MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
        byte[] bytes = passwaord.getBytes("UTF-8");
        messageDigest.update(bytes,0,bytes.length);
        byte[] key = messageDigest.digest();
        return new SecretKeySpec(key,"AES");
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode== KeyEvent.KEYCODE_BACK){
            if (back == 0) {
                CountDownTimer countDownTimer = new CountDownTimer(1000,1000) {
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
                Toast.makeText(AddQuestionPage.this,"press again to go back",Toast.LENGTH_SHORT).show();
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

    void speechToText(EditText editText){
        editText.setOnLongClickListener(new View.OnLongClickListener() {
            int resultCode;
            @Override
            public boolean onLongClick(View v) {
                try {
                    Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,"en-US");
                    resultCode = v.getId();
                    startActivityForResult(intent,resultCode);

                }catch (Exception e){
                    Toast.makeText(context, "your device does not support this service", Toast.LENGTH_SHORT).show();
                }
                return false;
            }

        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if(data != null){
                String spoken_text;
                ArrayList<String> text = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

                switch (requestCode){
                    case R.id.editTextTextPersonName:{
                        spoken_text = question.getText().toString()+" "+text.get(0);
                        question.setText(spoken_text);
                        break;
                    }
                    case R.id.editTextTextPersonName1:{
                        spoken_text = opt1.getText().toString()+" "+text.get(0);
                        opt1.setText(spoken_text);
                        break;
                    }
                    case R.id.editTextTextPersonName2:{
                        spoken_text = opt2.getText().toString()+" "+text.get(0);
                        opt2.setText(spoken_text);
                        break;
                    }
                    case R.id.editTextTextPersonName3:{
                        spoken_text = opt3.getText().toString()+" "+text.get(0);
                        opt3.setText(spoken_text);
                        break;
                    }
                    case R.id.editTextTextPersonName4:{
                        spoken_text = opt4.getText().toString()+" "+text.get(0);
                        opt4.setText(spoken_text);
                        break;
                    }
                }
            }
        }
        catch (Exception e){
            //
        }

    }
}