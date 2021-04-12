package com.example.goldenratio;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;


public class ChapterRecViewAdapter extends RecyclerView.Adapter<ChapterRecViewAdapter.Viewholder>{

    private ArrayList<ChapterCardsDetails> cards = new ArrayList<>();
    private final Context context;

    public ChapterRecViewAdapter(Context context) {
        this.context = context;
    }


    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardviewxml,parent,false);
        Viewholder holder = new Viewholder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull Viewholder holder,final int position) {
        holder.chapter.setText(cards.get(position).getChapter_name());
        holder.totalquestions.setText(""+cards.get(position).getTotal_question());

        holder.totalquestions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        holder.chapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DataBaseHelper db = new DataBaseHelper(context);
                String chapter_to_be_shared = cards.get(position).getChapter_name();

                String main_hash = db.get_all_questions_from_this_table(chapter_to_be_shared);
                if (main_hash==null){
                    Toast.makeText(context, "chapter has no questions to be shared", Toast.LENGTH_SHORT).show();
                    return;
                }


                File myExtenalFile = new File((context.getExternalFilesDir("chapters")),chapter_to_be_shared+".txt");
                final  String path = myExtenalFile.getPath();

                FileOutputStream fos = null;
                try {
                    fos = new FileOutputStream(myExtenalFile);;
                    fos.write(main_hash.getBytes());
                    fos.close();

                } catch (Exception e){
                    Toast.makeText(context, "unable to process this chapter", Toast.LENGTH_SHORT).show();
                    return;
                }
                try{
                    File myExternalFile2 = new File(context.getExternalFilesDir("chapters"),chapter_to_be_shared+".txt");
                    String x =  myExternalFile2.getAbsolutePath();
                    Toast.makeText(context, x, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("text/plain");

                    intent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://"+myExternalFile2));

                    context.startActivity(Intent.createChooser(intent,"shareVia"));

                }catch (Exception e){
                    Toast.makeText(context, "something went wrong", Toast.LENGTH_SHORT).show();
                }

            }
        });

        holder.add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,AddQuestionPage.class);
                String chapterNameConstForIntent = cards.get(position).getChapter_name();
                intent.putExtra("chaptername",chapterNameConstForIntent);
                intent.putExtra("reqForChange",1);
                context.startActivity(intent);
            }
        });

        holder.del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DataBaseHelper db = new DataBaseHelper(context);
                String chapter_to_be_rename = cards.get(position).getChapter_name();


                Dialog dialog = new Dialog(v.getContext());
                dialog.setContentView(R.layout.pop_for_final_confirmation_for_delete_request_layout);
                TextView textView =  dialog.findViewById(R.id.deletechapettextview);
                try {
                    textView.setText("Delete \n"+chapter_to_be_rename.substring(0,30)+"...");

                }
                catch (Exception e){
                    textView.setText("Delete \n"+chapter_to_be_rename);
                }
                Button yesbutton = dialog.findViewById(R.id.yes);
                Button nobutton = dialog.findViewById(R.id.no);
                dialog.show();

                yesbutton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        db.delete_table(chapter_to_be_rename);
                        MainActivity.change(db.is_there_table());
                        db.close();
                        dialog.dismiss();
                        Toast.makeText(v.getContext(),""+chapter_to_be_rename+" has been delete ",Toast.LENGTH_SHORT).show();
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

        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DataBaseHelper db= new DataBaseHelper(context);
                int size = db.getSize(cards.get(position).getChapter_name());
                if(size>0){
                    Intent intent = new Intent(context,QuestionAnspage.class);
                    String chapterNameConstForIntent = cards.get(position).getChapter_name();

                    intent.putExtra("chaptername",chapterNameConstForIntent);
//                intent.putExtra("reqForADD",1);
                    intent.putExtra("reqForChange",1);
                    context.startActivity(intent);
                }
                else{
                    Toast.makeText(context,"no question in the chapter\n add more question to this chapter to start practicing",Toast.LENGTH_SHORT).show();
                }
            }
        });

        holder.linearLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                DataBaseHelper db = new DataBaseHelper(context);
                String chapter_to_be_rename = cards.get(position).getChapter_name();


                Dialog dialog = new Dialog(v.getContext());
                dialog.setContentView(R.layout.rename_pop_up);
                EditText editText =  dialog.findViewById(R.id.new_name_et);

//                try {
//                    textView.setText("Delete \n"+chapter_to_be_rename.substring(0,30)+"...");
//
//                }
//                catch (Exception e){
//                    textView.setText("Delete \n"+chapter_to_be_rename);
//                }
                Button yesbutton = dialog.findViewById(R.id.yes);
                Button nobutton = dialog.findViewById(R.id.no);
                dialog.show();

                yesbutton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try{
                            db.rename_table(chapter_to_be_rename,editText.getText().toString());
                            MainActivity.change(db.is_there_table());
                            db.close();
                            dialog.dismiss();
                            Toast.makeText(v.getContext(),""+chapter_to_be_rename+" has been renamed to "+editText.getText().toString(),Toast.LENGTH_SHORT).show();
                        }
                        catch (Exception e){
                            Toast.makeText(v.getContext(),"invalid name ",Toast.LENGTH_SHORT).show();
                        }

                    }
                });
                nobutton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        return;
                    }
                });
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return cards.size();
    }

    public void setCards(ArrayList<ChapterCardsDetails> cards) {
        this.cards = cards;
        notifyDataSetChanged();
    }

    public static class Viewholder extends RecyclerView.ViewHolder{

        private final TextView chapter;
        private final TextView totalquestions;
        private final Button del;
        private final Button add;
//        private final Button update;
        private final LinearLayout linearLayout;

        public Viewholder(@NonNull View itemView) {
            super(itemView);
            chapter = itemView.findViewById(R.id.cheptername);
            totalquestions = itemView.findViewById(R.id.totalquestion);
            del =  itemView.findViewById(R.id.deleteques);
            add =  itemView.findViewById(R.id.addques);
//            update = itemView.findViewById(R.id.updateChapNamebtn);
            linearLayout = itemView.findViewById(R.id.cardlayout);
        }
    }



}
