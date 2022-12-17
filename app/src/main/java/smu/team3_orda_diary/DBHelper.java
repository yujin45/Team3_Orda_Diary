package smu.team3_orda_diary;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class DBHelper extends SQLiteOpenHelper
{
    private static final int DB_VERSION = 1;
    public static final String DB_NAME = "orda.db";

    public DBHelper(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    //데이터베이스가 생성되었을 때의 상태값을 이야기하는 함수
    public void onCreate(SQLiteDatabase db) {
        //데이터 베이스가 생성이 될 때 호출
        //데이터베이스 -> 테이블 -> 칼럼 -> 값
        db.execSQL("CREATE TABLE IF NOT EXISTS Todolist (id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT NOT NULL, content TEXT NOT NULL,writeDate TEXT NOT NULL ) "); //테이블이 존재하지 않는 경우

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
        Cursor cursor = db.rawQuery("SELECT * FROM TodoList WHERE writeDate='"+change+"' ORDER BY writeDate DESC", null);
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
    // 이거 함수 이름 소문자 시작으로 바꿔주세요

    //INSERT 문 (할일 목록을 DB에 넣는다)
    public void InsertTodo(String _title, String _content, String _writeDate){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("INSERT INTO TodoList (title, content, writeDate) VALUES ('"+_title+"','"+ _content +"','"+ _writeDate +"');");
    }

    //UPDATE 문 (할일 목록을 수정한다.)
    public void UpdateTodo(String _title, String _content, String _writeDate , String _beforeDate){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("UPDATE TodoList SET title='"+_title+"', content='"+_content+"', writeDate= '"+_writeDate+"' WHERE writeDate='"+_beforeDate+"' ");
    }

    //DELETE 문 (할일 목록을 제거한다.)
    public void deleteTodo( String _beforeDate ){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM TodoList WHERE writeDate = '"+_beforeDate+"'");
    }
}
