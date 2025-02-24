package smu.team3_orda_diary.ui.diary;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import smu.team3_orda_diary.R;
import smu.team3_orda_diary.databinding.FragmentDiarySelectedBinding;
import smu.team3_orda_diary.model.OnePageDiary;

public class DiarySelectedFragment extends Fragment {
    private FragmentDiarySelectedBinding binding;
    private OnePageDiary selectedDiary;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentDiarySelectedBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        DiarySelectedFragmentArgs args = DiarySelectedFragmentArgs.fromBundle(getArguments());
        selectedDiary = args.getSelectedDiary();

        if (selectedDiary.getPicture_uri() != null && !selectedDiary.getPicture_uri().isEmpty()) {
            binding.diaryImage.setImageURI(Uri.parse(selectedDiary.getPicture_uri()));
        } else {
            binding.diaryImage.setImageResource(R.drawable.notice_insert_image);
        }

        binding.itemTitle.textText.setText(selectedDiary.getTitle());
        binding.itemTitle.labelText.setText(getString(R.string.diary_title_label));
        binding.itemDate.textText.setText(selectedDiary.getDate());
        binding.itemDate.labelText.setText(getString(R.string.diary_date_label));
        binding.itemFeel.textText.setText(selectedDiary.getFeel());
        binding.itemFeel.labelText.setText(getString(R.string.diary_feel_label));
        binding.itemContent.setText(selectedDiary.getText());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
