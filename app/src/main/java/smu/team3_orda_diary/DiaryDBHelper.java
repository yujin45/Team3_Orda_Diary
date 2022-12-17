package smu.team3_orda_diary;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;

// 일기장 DB관리를 위해 다양한 DB 쿼리문을 수행하는 메서드들을 만들어줄 것임
public class DiaryDBHelper extends SQLiteOpenHelper {
    static final String DATABASE_NAME = "Diary";

    public DiaryDBHelper(@Nullable Context context, int version) {
        super(context, DATABASE_NAME, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // 일기 한장에 포함되는 내용들 : 구분 번호, 제목, 날짜, 기분, 사진(혹은 그림), 내용
        db.execSQL("CREATE TABLE DIARY_TB" +
                "(DIARY_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "TITLE TEXT NOT NULL, " +
                "DATE TEXT NOT NULL, " +
                "FEEL TEXT NOT NULL," +
                "PICTURE_URI TEXT ," +
                "TEXT TEXT NOT NULL);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS DIARY_TB");
        onCreate(db);
    }

    public void insert(int diary_id, String title, String date, String feel, String picture_uri, String text ) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("INSERT INTO DIARY_TB VALUES('" + diary_id + "', '" + title + "', '" + date + "', '" + feel + "', '" + picture_uri + "','" + text+"');");
        db.close();
    }

    public void update(int diary_id, String title, String date, String feel, String picture_uri  ,String text ) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("UPDATE DIARY_TB SET TITLE = " + title
                + ", DATE = '" + date + "'"
                + ", FEEL = '" + feel +"'"
                + ", PICTURE_URI = '" + picture_uri +"'"
                + ", TEXT =  '" + text+"'"
                + " WHERE DIARY_ID = '" + diary_id + "'");
        db.close();
    }

    public void delete(String diary_id) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE DIARY_TB WHERE DIARY_ID = '" + diary_id + "'");
        db.close();
    }

    public int getRowCount(){
        // 읽기가 가능하게 DB 열기
        SQLiteDatabase db = getReadableDatabase();
        Cursor countCursor = db.rawQuery("SELECT count(DIARY_ID) FROM DIARY_TB;", null);
        countCursor.moveToFirst();
        int count =countCursor.getCount();
        return count;
    }

    public ArrayList<OnePageDiary> getResult() {
        //public String getResult() {
        /*
        * https://crazykim2.tistory.com/648
        * https://jeongkyun-it.tistory.com/186
        * https://citynetc.tistory.com/150
        * https://stackoverflow.com/questions/8884058/sqlite-is-it-possible-to-insert-a-blob-via-insert-statement
        * https://popcorn16.tistory.com/105
        *
        * */

        // 읽기가 가능하게 DB 열기
        SQLiteDatabase db = getReadableDatabase();
        //String result = "";
        String []result = new String[5];
        ArrayList<OnePageDiary> onePageDiaries = new ArrayList<>();
        // DB에 있는 데이터를 쉽게 처리하기 위해 Cursor를 사용하여 테이블에 있는 모든 데이터 출력
        Cursor cursor = db.rawQuery("SELECT * FROM DIARY_TB", null);
        while (cursor.moveToNext()) {
            OnePageDiary onePageDiary = new OnePageDiary(cursor.getInt(0),
                    cursor.getString(1), cursor.getString(2),
                    cursor.getString(3), cursor.getString(4), cursor.getString(5));
            onePageDiaries.add(onePageDiary);

        }

        return onePageDiaries;
    }
}
