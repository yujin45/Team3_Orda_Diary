package smu.team3_orda_diary.ui.diary;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import smu.team3_orda_diary.R;
import smu.team3_orda_diary.database.DBHelper;

public class DiaryWritingViewModel extends ViewModel {

    private static final String DEFAULT_FEELING = "기분 선택";
    private static final String DEFAULT_IMAGE_PATH = "android.resource://smu.team3_orda_diary/" + R.drawable.notice_insert_image;

    public DBHelper dbHelper;
    public MutableLiveData<String> title = new MutableLiveData<>("");
    public MutableLiveData<String> date = new MutableLiveData<>("");
    public MutableLiveData<String> feeling = new MutableLiveData<>(DEFAULT_FEELING);
    public MutableLiveData<String> content = new MutableLiveData<>("");
    public MutableLiveData<String> imagePath = new MutableLiveData<>(DEFAULT_IMAGE_PATH);

    public DiaryWritingViewModel(DBHelper dbHelper) {
        this.dbHelper = dbHelper;
        this.imagePath.setValue(DEFAULT_IMAGE_PATH);
    }

    public boolean addDiary() {
        String titleValue = title.getValue();
        String dateValue = date.getValue();
        String feelingValue = feeling.getValue();
        String contentValue = content.getValue();
        String imagePathValue = imagePath.getValue();

        if (titleValue.isEmpty() || dateValue.isEmpty() ||
                feelingValue.equals(DEFAULT_FEELING) || contentValue.isEmpty() ||
                imagePathValue.equals(DEFAULT_IMAGE_PATH)) {
            return false;
        }

        dbHelper.insertDiary(titleValue, dateValue, feelingValue, imagePathValue, contentValue);
        return true;
    }
}
