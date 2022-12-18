package smu.team3_orda_diary;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
// 일기를 담아둘 리스트
import java.util.ArrayList;

// 리사이클러 뷰를 사용하기 위한 어댑터: 이전 예약 과제에서 만든 것 재활용
public class DiaryRecyclerViewAdapter extends RecyclerView.Adapter {
    /*
  어댑터의 동작원리 및 순서
  1.(getItemCount) 데이터 개수를 세어 어댑터가 만들어야 할 총 아이템 개수를 얻는다.
  2.(getItemViewType)[생략가능] 현재 itemview의 viewtype을 판단한다
  3.(onCreateViewHolder)viewtype에 맞는 뷰 홀더를 생성하여 onBindViewHolder에 전달한다.
  4.(onBindViewHolder)뷰홀더와 position을 받아 postion에 맞는 데이터를 뷰홀더의 뷰들에 바인딩한다.
  */
    String TAG = "RecyclerViewAdapter";
    //리사이클러뷰에 넣을 데이터 리스트
    ArrayList<OnePageDiary> dataModels;
    Context context;
    //생성자를 통하여 데이터 리스트 context를 받음
    public DiaryRecyclerViewAdapter(Context context, ArrayList<OnePageDiary> dataModels) {
        this.dataModels = dataModels;
        this.context = context;
    }

    public int getItemCount() {
        //데이터 리스트의 크기를 전달해주어야 함
        return dataModels.size();
    }

    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder");
        //자신이 만든 itemview를 inflate한 다음 뷰홀더 생성
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.one_page_dirary_view, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(view);
        //생선된 뷰홀더를 리턴하여 onBindViewHolder에 전달한다.
        return viewHolder;
    }

    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Log.d(TAG, "onBindViewHolder");
        MyViewHolder myViewHolder = (MyViewHolder) holder;
        myViewHolder.diaryTitleTextView.setText(dataModels.get(position).getTitle());
        myViewHolder.diaryDateTextView.setText(dataModels.get(position).getDate());
        myViewHolder.diaryFeelTextView.setText(dataModels.get(position).getFeel());
        myViewHolder.diaryTextView.setText(dataModels.get(position).getText());
        String picture_uri = dataModels.get(position).getPicture_uri();
        myViewHolder.diarySavedImageView.setImageURI(Uri.parse(picture_uri));

        //▼ 리사이클러 내의 아이템 클릭시 동작하는 부분

        myViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(context, position+"번째 아이템 클릭", Toast.LENGTH_SHORT).show();
                // 인텐트로 넘겨줘야 하는 부분
                Intent intent = new Intent(myViewHolder.itemView.getContext(), DiarySelectedActivity.class);
                intent.putExtra("clickPosition", position); // position으로 array접근해서 보여주기
                /*
                intent.putExtra("title", dataModels.get(position).getTitle());
                intent.putExtra("date", dataModels.get(position).getDate());
                intent.putExtra("feel", dataModels.get(position).getFeel());
                intent.putExtra("uri", dataModels.get(position).getPicture_uri());
                intent.putExtra("text", dataModels.get(position).getText());

                 */
                ContextCompat.startActivity(myViewHolder.itemView.getContext(), intent, null);
            }
        });


    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView diaryTitleTextView, diaryDateTextView, diaryFeelTextView, diaryTextView;
        ImageView diarySavedImageView;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            diaryTitleTextView = itemView.findViewById(R.id.diaryTitleTextView);
            diaryDateTextView = itemView.findViewById(R.id.diaryDateTextView);
            diaryFeelTextView = itemView.findViewById(R.id.diaryFeelTextView);
            diaryTextView = itemView.findViewById(R.id.diaryTextView);
            diarySavedImageView= itemView.findViewById(R.id.diarySavedImageView);
        }
    }

}
