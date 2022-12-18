package smu.team3_orda_diary;

// DB 사용을 위한 것들
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import androidx.annotation.Nullable;
import java.util.ArrayList;

public class DBHelper extends SQLiteOpenHelper
{
    public static final int DB_VERSION = 1;
    public static final String DB_NAME = "Ordatest5.db";

    public DBHelper(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    //데이터베이스가 생성되었을 때의 상태값을 이야기하는 함수
    public void onCreate(SQLiteDatabase db) {
        //데이터 베이스가 생성이 될 때 호출
        //데이터베이스 -> 테이블 -> 칼럼 -> 값
        db.execSQL("CREATE TABLE IF NOT EXISTS TODOLIST_TB (id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT NOT NULL, content TEXT NOT NULL,writeDate TEXT NOT NULL ) ");

        // 일기 한장에 포함되는 내용들 : 구분 번호, 제목, 날짜, 기분, 사진(혹은 그림), 내용
        db.execSQL("CREATE TABLE IF NOT EXISTS DIARY_TB" +
                "(DIARY_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "TITLE TEXT NOT NULL, " +
                "DATE TEXT NOT NULL, " +
                "FEEL TEXT NOT NULL," +
                "PICTURE_URI TEXT NOT NULL," +
                "TEXT TEXT NOT NULL);");

        // 알람 관련
        db.execSQL("CREATE TABLE IF NOT EXISTS ALARM_TB (ALARM_ID INTEGER PRIMARY KEY AUTOINCREMENT,TIME TEXT NOT NULL); ");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onCreate(db);
    }
    //SELECT 문 (할일 목록을 조회한다.)
    public ArrayList<TodoItem> getTodoList(String _writeDate){
        ArrayList<TodoItem> todoItems = new ArrayList<>();
        String change = _writeDate;

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM TODOLIST_TB WHERE writeDate='"+change+"' ORDER BY writeDate DESC", null);
        if(cursor.getCount() !=0){
            // 조회는 데이터가 있을 때 내부 수행
            while (cursor.moveToNext()){
                int cursor_id = cursor.getColumnIndex("id");
                int cursor_title = cursor.getColumnIndex("title");
                int cursor_content= cursor.getColumnIndex("content");
                int cursor_writeDate = cursor.getColumnIndex("writeDate");

                int id = cursor.getInt(cursor_id);
                String title = cursor.getString(cursor_title);
                String content = cursor.getString(cursor_content);
                String writeDate = cursor.getString(cursor_writeDate);

                TodoItem todoItem = new TodoItem();
                todoItem.setId(id);
                todoItem.setTitle(title);
                todoItem.setContent(content);
                todoItem.setWriteDate(writeDate);

                todoItems.add(todoItem);
            }
        }
        cursor.close();
        return todoItems;

    }

    //INSERT 문 (할일 목록을 DB에 넣는다)
    public void insertTodo(String _title, String _content, String _writeDate){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("INSERT INTO TODOLIST_TB (title, content, writeDate) VALUES ('"+_title+"','"+ _content +"','"+ _writeDate +"');");
    }

    //UPDATE 문 (할일 목록을 수정한다.)
    public void updateTodo(String _title, String _content, String _writeDate , String _beforeDate){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("UPDATE TODOLIST_TB SET title='"+_title+"', content='"+_content+"', writeDate= '"+_writeDate+"' WHERE writeDate='"+_beforeDate+"' ");
    }

    //DELETE 문 (할일 목록을 제거한다.)
    public void deleteTodo( String _beforeDate ){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM TODOLIST_TB WHERE writeDate = '"+_beforeDate+"'");
    }

    //////// 다이어리 관련
    public void insert( String title, String date, String feel, String picture_uri, String text ) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("INSERT INTO DIARY_TB (TITLE, DATE, FEEL,PICTURE_URI,TEXT) VALUES ('"+title+"','"+ date +"','"+ feel +"','"+ picture_uri +"','"+ text +"');");

        //db.execSQL("INSERT INTO TODOLIST_TB VALUES( '" + title + "', '" + date + "', '"
        //       + feel + "', '" + picture_uri + "','" + text+"');");
        //db.close();
    }

    public void update(int diary_id, String title, String date, String feel, String picture_uri  ,String text ) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("UPDATE DIARY_TB SET TITLE = " + title
                + ", DATE = '" + date + "'"
                + ", FEEL = '" + feel +"'"
                + ", PICTURE_URI = '" + picture_uri +"'"
                + ", TEXT =  '" + text+"'"
                + " WHERE DIARY_ID = '" + diary_id + "'");
        // db.close();
    }

    public void delete(int diary_id) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE DIARY_TB WHERE DIARY_ID = '" + diary_id + "'");
        //db.close();
    }

    public ArrayList<OnePageDiary> getResult() {
        // 읽기가 가능하게 DB 열기
        SQLiteDatabase db = getReadableDatabase();
        ArrayList<OnePageDiary> onePageDiaries = new ArrayList<>();
        // DB에 있는 데이터를 쉽게 처리하기 위해 Cursor를 사용하여 테이블에 있는 모든 데이터 출력
        Cursor cursor = db.rawQuery("SELECT * FROM DIARY_TB", null);
        while (cursor.moveToNext()) {
            OnePageDiary onePageDiary = new OnePageDiary(cursor.getInt(0),
                    cursor.getString(1), cursor.getString(2),
                    cursor.getString(3), cursor.getString(4), cursor.getString(5));
            onePageDiaries.add(onePageDiary);
        }
        cursor.close();
        return onePageDiaries;
    }

    // 알람을 다시 설정
    public void updateAlarm(String time){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("UPDATE ALARM_TB SET TIME='"+time+"' ");
    }

    //INSERT 문 (할일 목록을 DB에 넣는다)
    public void insertAlarm(String time) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("INSERT INTO ALARM_TB (TIME) VALUES ('" + time + "');");
    }
        public String getAlarmTime(){
        SQLiteDatabase db = getReadableDatabase();
        String time = null;
        Cursor cursor = db.rawQuery("SELECT * FROM ALARM_TB", null);
        while (cursor.moveToNext()) {
            time = cursor.getString(1);
        }
        cursor.close();
        return time;
    }

    //DELETE 문 (할일 목록을 제거한다.)
    public void deleteAlarm( String time){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM ALARM_TB WHERE TIME = '"+time+"'");
    }

}
