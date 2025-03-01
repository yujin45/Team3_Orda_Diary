package smu.team3_orda_diary.ui.diary;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import smu.team3_orda_diary.database.DBHelper;

public class DiaryWritingViewModelFactory implements ViewModelProvider.Factory {
    private final DBHelper dbHelper;

    public DiaryWritingViewModelFactory(DBHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(DiaryWritingViewModel.class)) {
            return (T) new DiaryWritingViewModel(dbHelper);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
