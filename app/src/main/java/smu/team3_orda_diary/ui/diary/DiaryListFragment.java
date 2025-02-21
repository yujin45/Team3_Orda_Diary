package smu.team3_orda_diary.ui.diary;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import java.util.ArrayList;

import smu.team3_orda_diary.MainActivity;
import smu.team3_orda_diary.R;
import smu.team3_orda_diary.databinding.FragmentDiaryListBinding;
import smu.team3_orda_diary.model.OnePageDiary;

public class DiaryListFragment extends Fragment {

    private FragmentDiaryListBinding binding;
    private DiaryRecyclerViewAdapter adapter;
    private ArrayList<OnePageDiary> diaryList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentDiaryListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        try {
            diaryList = MainActivity.mDBHelper.getResult();
            if (diaryList == null) {
                diaryList = new ArrayList<>();
            }
        } catch (Exception e) {
            Log.e("DiaryListFragment", "DB 가져오는 중 오류 발생", e);
            diaryList = new ArrayList<>();
        }

        adapter = new DiaryRecyclerViewAdapter(requireContext(), diaryList);
        binding.recyclerView.setAdapter(adapter);
        binding.recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));

        binding.writeDiaryButton.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_diaryListFragment_to_diaryWritingFragment);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding.recyclerView.setAdapter(null);
        binding = null;
    }
}
