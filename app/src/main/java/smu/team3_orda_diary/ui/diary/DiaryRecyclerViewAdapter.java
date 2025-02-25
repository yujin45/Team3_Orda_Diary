package smu.team3_orda_diary.ui.diary;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import smu.team3_orda_diary.R;
import smu.team3_orda_diary.databinding.OnePageDiaryViewBinding;
import smu.team3_orda_diary.model.OnePageDiary;

public class DiaryRecyclerViewAdapter extends RecyclerView.Adapter<DiaryRecyclerViewAdapter.MyViewHolder> {
    private final ArrayList<OnePageDiary> dataModels;
    private final Context context;

    public DiaryRecyclerViewAdapter(Context context, ArrayList<OnePageDiary> dataModels) {
        this.context = context;
        this.dataModels = dataModels;
    }

    @Override
    public int getItemCount() {
        return dataModels.size();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        OnePageDiaryViewBinding binding = OnePageDiaryViewBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        OnePageDiary diary = dataModels.get(position);
        holder.bind(diary);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private final OnePageDiaryViewBinding binding;

        public MyViewHolder(OnePageDiaryViewBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(OnePageDiary diary) {
            binding.diaryTitleTextView.setText(diary.getTitle());
            binding.diaryDateTextView.setText(diary.getDate());
            binding.diaryFeelTextView.setText(diary.getFeel());
            binding.diaryTextView.setText(diary.getText());

            String pictureUri = diary.getPicture_uri();
            if (pictureUri != null && !pictureUri.isEmpty()) {
                binding.diarySavedImageView.setImageURI(Uri.parse(pictureUri));
            } else {
                binding.diarySavedImageView.setImageResource(R.drawable.notice_insert_image);
            }

            binding.getRoot().setOnClickListener(v -> {
                DiaryListFragmentDirections.ActionDiaryListFragmentToDiarySelectedFragment action =
                        DiaryListFragmentDirections.actionDiaryListFragmentToDiarySelectedFragment(diary);
                Navigation.findNavController(v).navigate(action);

            });
        }
    }
}
