package smu.team3_orda_diary;

import android.graphics.Color;

// 일기장 한장을 구성하는 객체 만듦
public class OnePageDiary {
    int diary_id; //구분용
    String title;           // 제목
    String date;            // 날짜
    String feel;            // 기분
    String picture_uri;
    String text;
    Color backgroundColor;  // 배경지 색

    public OnePageDiary(int diary_id, String title, String date, String feel, String picture_uri, String text){
        this.diary_id = diary_id;
        this.title = title;
        this.date = date;
        this.feel = feel;
        this.picture_uri  = picture_uri;
        this.text =text;

    }

    public int getDiary_id() {
        return diary_id;
    }

    public void setDiary_id(int diary_id) {
        this.diary_id = diary_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getFeel() {
        return feel;
    }

    public void setFeel(String feel) {
        this.feel = feel;
    }

    public String getPicture_uri() {
        return picture_uri;
    }

    public void setPicture_uri(String picture_uri) {
        this.picture_uri = picture_uri;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Color getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
    }
}
