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

import smu.team3_orda_diary.model.MapMemoItem;
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
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL("DELETE FROM MAPMEMO_TB");
        db.close();
    }

    @After
    public void tearDown() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL("DELETE FROM TODOLIST_TB");
        db.execSQL("DELETE FROM DIARY_TB");
        db.execSQL("DELETE FROM ALARM_TB");
        db.execSQL("DELETE FROM MAPMEMO_TB");
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

        String newTitle = "Updated Todo";
        dbHelper.updateTodo(todoId, newTitle, content, writeDate, writeDate);
        todoList = dbHelper.getTodoList(writeDate);
        assertEquals(newTitle, todoList.get(0).getTitle());

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

    @Test
    public void testInsertAndGetMapMemo() {
        String title = "Test Location";
        String content = "Favorite cafe";
        double latitude = 37.5665;
        double longitude = 126.9780;

        dbHelper.insertMapMemo(title, content, latitude, longitude);
        ArrayList<MapMemoItem> mapMemos = dbHelper.getAllMapMemos();

        assertEquals(1, mapMemos.size());
        assertEquals(title, mapMemos.get(0).getTitle());
        assertEquals(content, mapMemos.get(0).getContent());
        assertEquals(latitude, mapMemos.get(0).getLatitude(), 0.0001);
        assertEquals(longitude, mapMemos.get(0).getLongitude(), 0.0001);
    }

    @Test
    public void testDeleteMapMemo() {
        String title = "Test Location";
        String content = "Favorite cafe";
        double latitude = 37.5665;
        double longitude = 126.9780;

        dbHelper.insertMapMemo(title, content, latitude, longitude);
        ArrayList<MapMemoItem> mapMemos = dbHelper.getAllMapMemos();
        assertTrue(mapMemos.size() > 0);

        int memoId = mapMemos.get(0).getId();
        dbHelper.deleteMapMemo(memoId);
        mapMemos = dbHelper.getAllMapMemos();
        assertTrue(mapMemos.isEmpty());
    }

    @Test
    public void testSQLInjectionUpdateTodo() {
        dbHelper.insertTodo("Todo A", "Content A", "2025-03-01");
        dbHelper.insertTodo("Todo B", "Content B", "2025-03-01");
        dbHelper.insertTodo("Todo C", "Content C", "2025-03-01");

        ArrayList<TodoItem> todoList = dbHelper.getTodoList("2025-03-01");
        assertEquals(3, todoList.size());

        int targetTodoId = todoList.get(0).getId();

        // SQL Injection 시도 - 모든 데이터를 업데이트하도록 WHERE 조건 변경
        String injectionTitle = "Injected Title', writeDate='2025-03-02' WHERE id=id OR 1=1 --";

        dbHelper.updateTodo(targetTodoId, injectionTitle, "Injected Content", "2025-03-02", "2025-03-01");

        ArrayList<TodoItem> manipulatedList = dbHelper.getTodoList("2025-03-02");

        // Injection이 성공했다면 모든 To-Do의 날짜가 변경됨 (원래 1개만 변경되어야 함)
        assertTrue("SQL Injection should have updated multiple rows unexpectedly!", manipulatedList.size() > 1);
    }

}
