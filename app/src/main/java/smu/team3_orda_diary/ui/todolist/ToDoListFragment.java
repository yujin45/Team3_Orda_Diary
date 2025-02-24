package smu.team3_orda_diary.ui.todolist;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Locale;

import smu.team3_orda_diary.R;
import smu.team3_orda_diary.database.DBHelper;
import smu.team3_orda_diary.databinding.FragmentToDoListBinding;
import smu.team3_orda_diary.model.TodoItem;

public class ToDoListFragment extends Fragment {

    private FragmentToDoListBinding binding;
    private DBHelper dbHelper;
    private TodoCustomAdapter todoAdapter;
    private ArrayList<TodoItem> todoItems;
    private String currentTime;

    private static final String DATE_PATTERN_DB = "yyyy-MM-dd";
    private static final String DATE_PATTERN_UI = "yyyy년 MM월 dd일";
    private static final String CURRENT_TIME_FORMAT = "%d-%02d-%02d";
    private static final String TODO_TODAY_FORMAT = "%d년 %d월 %d일";


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentToDoListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        dbHelper = new DBHelper(requireContext());
        todoItems = new ArrayList<>();
        todoAdapter = new TodoCustomAdapter(todoItems, requireContext());

        binding.rvTodo.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvTodo.setAdapter(todoAdapter);

        DateTimeFormatter dbFormatter = DateTimeFormatter.ofPattern(DATE_PATTERN_DB, Locale.getDefault());
        DateTimeFormatter uiFormatter = DateTimeFormatter.ofPattern(DATE_PATTERN_UI, Locale.getDefault());

        LocalDate now = LocalDate.now();
        currentTime = now.format(dbFormatter);
        binding.todoToday.setText(now.format(uiFormatter));

        binding.todoCalendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int year, int month, int dayOfMonth) {
                month += 1;
                currentTime = String.format(Locale.getDefault(), CURRENT_TIME_FORMAT, year, month, dayOfMonth);
                binding.todoToday.setText(String.format(Locale.getDefault(), TODO_TODAY_FORMAT, year, month, dayOfMonth));
                loadRecentDB(currentTime);
            }
        });

        binding.todoBtnWrite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddTodoDialog();
            }
        });

        loadRecentDB(currentTime);
    }

    private void showAddTodoDialog() {
        Dialog dialog = new Dialog(requireContext(), android.R.style.Theme_Material_Light_Dialog);
        dialog.setContentView(R.layout.dialog_edit_todo);

        EditText etTitle = dialog.findViewById(R.id.et_title);
        EditText etContent = dialog.findViewById(R.id.et_content);
        Button btnOk = dialog.findViewById(R.id.todo_dialog_btn_ok);

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = etTitle.getText().toString();
                String content = etContent.getText().toString();

                dbHelper.insertTodo(title, content, currentTime);
                dialog.dismiss();
                Toast.makeText(requireContext(), getString(R.string.toast_todo_added), Toast.LENGTH_LONG).show();
                loadRecentDB(currentTime);
            }
        });

        dialog.show();
    }

    private void loadRecentDB(String writeDate) {
        todoItems.clear();
        todoItems.addAll(dbHelper.getTodoList(writeDate));

        if (todoItems.isEmpty()) {
            binding.rvTodo.setVisibility(View.GONE);
        } else {
            binding.rvTodo.setVisibility(View.VISIBLE);
        }

        todoAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
