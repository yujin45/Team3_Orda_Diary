package smu.team3_orda_diary;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

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
    public TodoCustomAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View holder = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_todo_list,parent,false);
        return new ViewHolder(holder);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tv_title.setText(mtodoItems.get(position).getTitle());
        holder.tv_content.setText(mtodoItems.get(position).getContent());
        holder.tv_writeDate.setText(mtodoItems.get(position).getWriteDate());


    }

    @Override
    public int getItemCount() {

        return mtodoItems.size();
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
                    int curPos = getAbsoluteAdapterPosition(); // 현재 리스트 클릭한 아이템 위치
                    TodoItem todoItem = mtodoItems.get(curPos);

                    String[] strChoiceItems = {"수정하기", "삭제하기"};
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setTitle("원하는 작업을 선택해주세요");
                    builder.setItems(strChoiceItems, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int position) {
                            if(position == 0){
                                //수정하기
                                //팝업창 띄우기
                                Dialog dialog = new Dialog(mContext, android.R.style.Theme_Material_Light_Dialog);
                                dialog.setContentView(R.layout.dialog_edit_todo);
                                EditText et_title = dialog.findViewById(R.id.et_title);
                                EditText et_content = dialog.findViewById(R.id.et_content);
                                et_title.setText(todoItem.getTitle().toString());
                                et_content.setText(todoItem.getContent().toString());
                                Button btn_ok = dialog.findViewById(R.id.todo_dialog_btn_ok);
                                btn_ok.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        //update database
                                        String title = et_title.getText().toString();
                                        String content = et_content.getText().toString();
                                        String currentTime = new SimpleDateFormat("yyyy-MM-dd MM:mm:ss").format(new Date()).toString(); // 현재 시간 연월일시분초 받아오기
                                        String beforeTime = todoItem.getWriteDate();

                                        mDBHelper.UpdateTodo(title, content, currentTime, beforeTime);

                                        //update UI
                                        todoItem.setTitle(title);
                                        todoItem.setContent(content);
                                        todoItem.setWriteDate(currentTime);
                                        notifyItemChanged(curPos, todoItem);
                                        dialog.dismiss();
                                        Toast.makeText(mContext, "목록 수정이 완료되었습니다.", Toast.LENGTH_LONG).show();
                                    }
                                });
                                dialog.show();
                            }
                            else if(position == 1){
                                //삭제하기
                                String beforeTime = todoItem.getWriteDate();
                                mDBHelper.deleteTodo(beforeTime);
                                //delete UI
                                mtodoItems.remove(curPos);
                                notifyItemRemoved(curPos);
                                Toast.makeText(mContext, "목록 삭제가 완료되었습니다.", Toast.LENGTH_LONG).show();

                            }
                        }
                    });
                    builder.show();

                }
            });

        }
    }
    public void addItem (TodoItem _item){
        mtodoItems.add(0,_item);
        notifyItemInserted(0);
    }
    public void selectItem (TodoItem _item){
        mtodoItems.add(0,_item);
        notifyItemInserted(0);
    }
}