package com.example.goldenratio;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Base64;
import android.util.Log;
import android.widget.EdgeEffect;
import android.widget.Toast;

import androidx.annotation.Nullable;

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

public class DataBaseHelper extends SQLiteOpenHelper {

    public static final String COLUMN_ID = "ID";
    public static final String QUESTION = "QUESTION";
    public static final String OPTION_A = "OPTION_A";
    public static final String OPTION_B = "OPTION_B";
    public static final String OPTION_C = "OPTION_C";
    public static final String OPTION_D = "OPTION_D";
    public static final String CORRECT_ANS = "CORRECT_ANS";
    Context context;
    ClipboardManager clipboard;
    public DataBaseHelper(@Nullable Context c) {
        super(c, "AllQuestion.db", null , 1);
        context  = c;
        clipboard = (ClipboardManager) context.getSystemService(context.CLIPBOARD_SERVICE);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public boolean AddTable(String table_name, Context context){
        SQLiteDatabase db = this.getWritableDatabase();
        table_name = table_name.replace(" ","");
        try {
            String CreateTableStatement = "create table if not exists "+table_name+" ( "+COLUMN_ID+" " + "INTEGER PRIMARY KEY AUTOINCREMENT, "+QUESTION+" TEXT UNIQUE, "+OPTION_A+" TEXT," + ""+OPTION_B+" TEXT, "+OPTION_C+" TEXT, "+OPTION_D+" TEXT, "+CORRECT_ANS+" INTEGER )";
            db.execSQL(CreateTableStatement);
            db.close();
            return true;
        } catch (Exception e){
            Toast.makeText(context, "Invalid Name \n chapter can't be added", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    public ArrayList<ChapterCardsDetails> is_there_table(){
        ArrayList<ChapterCardsDetails> arr = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("select name from sqlite_master where type = 'table' and name not like 'sqlite_%' and name not like 'android_%';",null);
        if(!cursor.moveToFirst())
        {
            db.close();
            return arr;
        }
        cursor.moveToFirst();

        for(int i = 0; i<cursor.getCount(); i++){
            String NAME = cursor.getString(0);
            Long ans =  DatabaseUtils.queryNumEntries(db, NAME);
            arr.add(new ChapterCardsDetails(NAME,  ans.intValue())  );
            cursor.moveToNext();
        }
        cursor.close();
        return arr;
    }

    public void delete_table(String table_name){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE "+table_name+" ;");
        db.close();
    }

    public void rename_table(String prv, String New){
        SQLiteDatabase db = this.getWritableDatabase();

        try{
            db.execSQL(    "ALTER TABLE "+ prv +" RENAME TO " + New +";");
            db.close();
        }catch (Exception e){
            db.close();
            Toast.makeText(context, "invalid name", Toast.LENGTH_SHORT).show();
        }

    }

    public void delete_row_in_table(String table, String row){
        SQLiteDatabase db = this.getWritableDatabase();
        String[] a = {row};
        db.delete(table,"QUESTION = ?",a);
        db.close();
    }

    public String[] get_question(String table, int offset){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor =  db.rawQuery("select * from "+table+" LIMIT 1 OFFSET "+offset,null);
        cursor.moveToFirst();
        String[] ans = new String[]{cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5), ""+cursor.getInt(6)};
        db.close();
        return ans;
    }
    public  String get_all_questions_from_this_table(String table){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor =  db.rawQuery("select * from "+table,null);
        cursor.moveToFirst();
        String mainHash = table;
        try {

            mainHash = mainHash + "---new---" + cursor.getString(1) + "-----" + cursor.getString(2) + "-----" + cursor.getString(3) + "-----" + cursor.getString(4) + "-----" + cursor.getString(5) + "-----" + cursor.getInt(6);
            while (cursor.moveToNext()) {
                mainHash = mainHash + "---new---" + cursor.getString(1) + "-----" + cursor.getString(2) + "-----" + cursor.getString(3) + "-----" + cursor.getString(4) + "-----" + cursor.getString(5) + "-----" + cursor.getInt(6);
            }
        }catch (Exception e){
            return null;
        }

       db.close();
        try {
            mainHash = msgToHash(mainHash,"questionShared");
            return mainHash;

        } catch (Exception e){
            Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
        }
        return null;
    }

    public int getSize(String table){
        SQLiteDatabase db = this.getReadableDatabase();
        Long ans =  DatabaseUtils.queryNumEntries(db, table);
        db.close();
        return ans.intValue();
    }

    public void add_question_to_table(String table_name,QuestionModel q){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        QuestionModel questionModel = q;
        cv.put(QUESTION,questionModel.getQuestion());
        cv.put(OPTION_A,questionModel.getOptionA());
        cv.put(OPTION_B,questionModel.getOptionB());
        cv.put(OPTION_C,questionModel.getOptionC());
        cv.put(OPTION_D,questionModel.getOptionD());
        cv.put(CORRECT_ANS,questionModel.getCorrectAns());

        db.insert(table_name, null, cv);
        db.close();
    }


    private String msgToHash(String msg, String key) throws UnsupportedEncodingException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
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
    private SecretKeySpec generateKey(String passwaord) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        final MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
        byte[] bytes = passwaord.getBytes("UTF-8");
        messageDigest.update(bytes,0,bytes.length);
        byte[] key = messageDigest.digest();
        SecretKeySpec secretKeySpec = new SecretKeySpec(key,"AES");
        return  secretKeySpec;
    }


}
