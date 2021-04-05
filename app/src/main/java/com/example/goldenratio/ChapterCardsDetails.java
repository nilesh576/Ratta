package com.example.goldenratio;

public class ChapterCardsDetails {
    private String chapter_name;
    private int total_question;
    private String description;

    public ChapterCardsDetails(String chapter_name, int total_question) {
        this.chapter_name = chapter_name;
        this.total_question = total_question;
    }

    public String getChapter_name() {
        return chapter_name;
    }

    public void setChapter_name(String chapter_name) {
        this.chapter_name = chapter_name;
    }

    public int getTotal_question() {
        return total_question;
    }

    public void setTotal_question(int total_question) {
        this.total_question = total_question;
    }
}
