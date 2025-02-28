package smu.team3_orda_diary.ui.diary;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import smu.team3_orda_diary.database.DBHelper;
import smu.team3_orda_diary.model.OnePageDiary;

public class DiaryViewModel extends AndroidViewModel {

    private final MutableLiveData<List<OnePageDiary>> diaryList = new MutableLiveData<>();
    private final DBHelper dbHelper;

    public DiaryViewModel(@NonNull Application application) {
        super(application);
        dbHelper = new DBHelper(application);
        loadDiaries(); // 초기 데이터 로드
    }

    public LiveData<List<OnePageDiary>> getDiaries() {
        return diaryList;
    }

    public void loadDiaries() {
        List<OnePageDiary> diaries = dbHelper.getDiaryList();
        diaryList.setValue(diaries);
    }

    public void addDiary(OnePageDiary diary) {
        dbHelper.insertDiary(diary.getTitle(), diary.getDate(), diary.getFeel(), diary.getPicture_uri(), diary.getText());
        loadDiaries(); // 변경 후 다시 로드
    }

    public void updateDiary(OnePageDiary diary) {
        dbHelper.updateDiary(diary.getDiary_id(), diary.getTitle(), diary.getDate(), diary.getFeel(), diary.getPicture_uri(), diary.getText());
        loadDiaries(); // 변경 후 다시 로드
    }

    public void deleteDiary(int diaryId) {
        dbHelper.deleteDiary(diaryId);
        loadDiaries(); // 변경 후 다시 로드
    }
}
