package smu.team3_orda_diary;

import android.app.AlertDialog;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class TodoCustomAdapter extends RecyclerView.Adapter<TodoCustomAdapter.ViewHolder> {
    private ArrayList<TodoItem> mtodoItems;
    private Context mContext;
    private DBHelper mDBHelper;

    public TodoCustomAdapter(ArrayList<TodoItem> mtodoItems, Context mContext) {
        this.mtodoItems = mtodoItems;
        this.mContext = mContext;
        mDBHelper = new DBHelper(mContext);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tv_title.setText(mtodoItems.get(position).getTitle());
        holder.tv_content.setText(mtodoItems.get(position).getContent());
        holder.tv_writeDate.setText(mtodoItems.get(position).getWriteDate());


    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView tv_title;
        private TextView tv_content;
        private TextView tv_writeDate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_title = itemView.findViewById(R.id.tv_title);
            tv_content = itemView.findViewById(R.id.tv_content);
            tv_writeDate = itemView.findViewById(R.id.tv_writeDate);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int curPos = getAdapterPosition(); // 현재 리스트 클릭한 아이템 위치
                    TodoItem todoItem = mtodoItems.get(curPos);

                    String[] strChoiceItems = {"수정하기", "삭제하기"};
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setTitle("원하는 작업을 선택해주세요");


                }
            });

        }
    }
}