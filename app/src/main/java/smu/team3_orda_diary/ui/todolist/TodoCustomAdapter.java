package smu.team3_orda_diary.ui.todolist;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import smu.team3_orda_diary.R;
import smu.team3_orda_diary.database.DBHelper;
import smu.team3_orda_diary.databinding.DialogEditTodoBinding;
import smu.team3_orda_diary.databinding.ItemTodoListBinding;
import smu.team3_orda_diary.model.TodoItem;

public class TodoCustomAdapter extends RecyclerView.Adapter<TodoCustomAdapter.ViewHolder> {
    private final Context mContext;
    private final DBHelper mDBHelper;
    private final ArrayList<TodoItem> mTodoItems;

    private static final String DATE_PATTERN_DB = "yyyy-MM-dd";

    public TodoCustomAdapter(ArrayList<TodoItem> todoItems, Context context) {
        this.mTodoItems = todoItems;
        this.mContext = context;
        mDBHelper = new DBHelper(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemTodoListBinding binding = ItemTodoListBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TodoItem todoItem = mTodoItems.get(position);

        holder.binding.tvTitle.setText(todoItem.getTitle());
        holder.binding.tvContent.setText(todoItem.getContent());
        holder.binding.tvWriteDate.setText(todoItem.getWriteDate());

        holder.itemView.setOnClickListener(view -> {
            int curPos = holder.getAdapterPosition();
            String[] options = {
                    mContext.getString(R.string.todo_options_edit),
                    mContext.getString(R.string.todo_options_delete)
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setTitle(mContext.getString(R.string.todo_dialog_title))
                    .setItems(options, (dialogInterface, which) -> {
                        if (which == 0) {
                            showEditTodoDialog(todoItem, curPos);
                        } else {
                            mDBHelper.deleteTodo(todoItem.getId());
                            mTodoItems.remove(curPos);
                            notifyItemRemoved(curPos);

                            Toast.makeText(mContext, mContext.getString(R.string.toast_todo_deleted), Toast.LENGTH_LONG).show();
                        }
                    })
                    .show();
        });
    }

    private void showEditTodoDialog(TodoItem todoItem, int position) {
        Dialog dialog = new Dialog(mContext, android.R.style.Theme_Material_Light_Dialog);
        DialogEditTodoBinding binding = DialogEditTodoBinding.inflate(LayoutInflater.from(mContext));
        dialog.setContentView(binding.getRoot());

        binding.etTitle.setText(todoItem.getTitle());
        binding.etContent.setText(todoItem.getContent());

        binding.todoDialogBtnOk.setText(mContext.getString(R.string.todo_dialog_ok));
        binding.todoDialogBtnOk.setOnClickListener(view -> {
            String title = binding.etTitle.getText().toString();
            String content = binding.etContent.getText().toString();

            String currentTime = new SimpleDateFormat(DATE_PATTERN_DB, Locale.getDefault()).format(new Date());
            String beforeTime = todoItem.getWriteDate();

            mDBHelper.updateTodo(todoItem.getId(), title, content, currentTime, beforeTime);

            todoItem.setTitle(title);
            todoItem.setContent(content);
            todoItem.setWriteDate(currentTime);
            notifyItemChanged(position);

            dialog.dismiss();

            Toast.makeText(mContext, mContext.getString(R.string.toast_todo_updated), Toast.LENGTH_LONG).show();
        });

        dialog.show();
    }

    @Override
    public int getItemCount() {
        return mTodoItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemTodoListBinding binding;

        public ViewHolder(@NonNull ItemTodoListBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
