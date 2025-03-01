package smu.team3_orda_diary.database;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

import smu.team3_orda_diary.model.OnePageDiary;
import smu.team3_orda_diary.model.TodoItem;

@RunWith(AndroidJUnit4.class)
public class DBHelperTest {
    private DBHelper dbHelper;
    private Context context;

    @Before
    public void setUp() {
        context = ApplicationProvider.getApplicationContext();
        dbHelper = DBHelper.getInstance(context);
    }

    @After
    public void tearDown() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL("DELETE FROM TODOLIST_TB");
        db.execSQL("DELETE FROM DIARY_TB");
        db.execSQL("DELETE FROM ALARM_TB");
        db.close();
    }

    @Test
    public void testDatabaseCreation() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        assertNotNull(db);
        db.close();
    }

    @Test
    public void testInsertAndGetTodo() {
        String title = "Test Todo";
        String content = "This is a test task";
        String writeDate = "2025-03-01";

        dbHelper.insertTodo(title, content, writeDate);
        ArrayList<TodoItem> todoList = dbHelper.getTodoList(writeDate);

        assertEquals(1, todoList.size());
        assertEquals(title, todoList.get(0).getTitle());
        assertEquals(content, todoList.get(0).getContent());
    }

    @Test
    public void testUpdateAndDeleteTodo() {
        String title = "Test Todo";
        String content = "Test Content";
        String writeDate = "2025-03-01";

        dbHelper.insertTodo(title, content, writeDate);
        ArrayList<TodoItem> todoList = dbHelper.getTodoList(writeDate);
        int todoId = todoList.get(0).getId();

        // 수정
        String newTitle = "Updated Todo";
        dbHelper.updateTodo(todoId, newTitle, content, writeDate, writeDate);
        todoList = dbHelper.getTodoList(writeDate);
        assertEquals(newTitle, todoList.get(0).getTitle());

        // 삭제
        dbHelper.deleteTodo(todoId);
        todoList = dbHelper.getTodoList(writeDate);
        assertTrue(todoList.isEmpty());
    }

    @Test
    public void testInsertAndGetDiary() {
        String title = "Test Diary";
        String date = "2025-03-01";
        String feel = "Happy";
        String pictureUri = "test_uri";
        String text = "This is a diary entry";

        dbHelper.insertDiary(title, date, feel, pictureUri, text);
        ArrayList<OnePageDiary> diaryList = dbHelper.getDiaryList();

        assertEquals(1, diaryList.size());
        assertEquals(title, diaryList.get(0).getTitle());
        assertEquals(text, diaryList.get(0).getText());
    }

    @Test
    public void testInsertAndGetAlarm() {
        String alarmTime = "08:00 AM";

        dbHelper.insertAlarm(alarmTime);
        String retrievedTime = dbHelper.getAlarmTime();

        assertEquals(alarmTime, retrievedTime);

        dbHelper.deleteAlarm(alarmTime);
        retrievedTime = dbHelper.getAlarmTime();
        assertEquals(null, retrievedTime);
    }
}
