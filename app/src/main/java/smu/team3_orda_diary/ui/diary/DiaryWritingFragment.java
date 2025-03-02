package smu.team3_orda_diary.ui.diary;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import smu.team3_orda_diary.R;
import smu.team3_orda_diary.database.DBHelper;
import smu.team3_orda_diary.databinding.FragmentDiaryWritingBinding;


public class DiaryWritingFragment extends Fragment {
    private DBHelper mDBHelper;
    private FragmentDiaryWritingBinding binding;
    private DiaryWritingViewModel diaryViewModel;
    private Calendar diaryCalendar;
    private Uri imageUri;
    private SpeechRecognizer speechRecognizer;
    private Intent recordIntent;
    private boolean recording = false;
    private String[] feelings;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDBHelper = DBHelper.getInstance(requireContext());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_diary_writing, container, false);
        binding.setLifecycleOwner(this); // LiveData 업데이트 감지

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        DiaryWritingViewModelFactory factory = new DiaryWritingViewModelFactory(mDBHelper);
        diaryViewModel = new ViewModelProvider(this, factory).get(DiaryWritingViewModel.class);
        binding.setViewModel(diaryViewModel);
        feelings = getResources().getStringArray(R.array.diary_feelings);

        diaryCalendar = Calendar.getInstance();
        updateDateText();

        DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(), datePickerListener,
                diaryCalendar.get(Calendar.YEAR),
                diaryCalendar.get(Calendar.MONTH),
                diaryCalendar.get(Calendar.DAY_OF_MONTH));

        binding.changeDateButton.setOnClickListener(v -> datePickerDialog.show());
        binding.insertPictureButton.setOnClickListener(v -> pickImageFromGallery());
        binding.insertCameraButton.setOnClickListener(v -> checkCameraPermissionAndTakePicture());
        binding.feelButton.setOnClickListener(v -> showFeelingsDialog());
        binding.recordButton.setOnClickListener(v -> toggleSpeechRecognition());
        binding.saveButton.setOnClickListener(v -> saveDiary());
    }

    private void updateDateText() {
        String formattedDate = String.format("%d년 %d월 %d일",
                diaryCalendar.get(Calendar.YEAR),
                diaryCalendar.get(Calendar.MONTH) + 1,
                diaryCalendar.get(Calendar.DAY_OF_MONTH));
        diaryViewModel.date.setValue(formattedDate);
    }

    private final DatePickerDialog.OnDateSetListener datePickerListener = (view, year, month, dayOfMonth) -> {
        diaryCalendar.set(year, month, dayOfMonth);
        updateDateText();
    };

    private final ActivityResultLauncher<String> pickImageLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    try {
                        Bitmap bitmap = loadBitmapFromUri(uri);
                        imageUri = saveImageToInternalStorage(bitmap);
                        diaryViewModel.imagePath.setValue(imageUri.toString());
                        Log.d("DiaryWriting", imageUri.toString());
                    } catch (IOException e) {
                        Toast.makeText(requireContext(), getString(R.string.diary_write_no_image), Toast.LENGTH_SHORT).show();
                    }
                }
            });

    private void pickImageFromGallery() {
        pickImageLauncher.launch("image/*");
    }

    private void checkCameraPermissionAndTakePicture() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA);
        } else {
            takePicture();
        }
    }

    private final ActivityResultLauncher<String> requestCameraPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    takePicture();
                } else {
                    Toast.makeText(requireContext(), R.string.permission_camera_denied, Toast.LENGTH_SHORT).show();
                }
            });

    private final ActivityResultLauncher<Intent> takePictureLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == getActivity().RESULT_OK) {
                    diaryViewModel.imagePath.setValue(imageUri.toString());
                } else {
                    Toast.makeText(requireContext(), R.string.camera_not_available, Toast.LENGTH_SHORT).show();
                }
            });

    private void takePicture() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(requireActivity().getPackageManager()) != null) {
            File photoFile;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Toast.makeText(requireContext(), R.string.camera_not_available, Toast.LENGTH_SHORT).show();
                return;
            }
            if (photoFile != null) {
                imageUri = FileProvider.getUriForFile(requireContext(),
                        requireActivity().getPackageName() + ".fileprovider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                takePictureLauncher.launch(takePictureIntent);
            }
        } else {
            Toast.makeText(requireContext(), R.string.camera_not_available, Toast.LENGTH_SHORT).show();
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File storageDir = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return File.createTempFile("DIARY_" + timeStamp, ".jpg", storageDir);
    }

    private Uri saveImageToInternalStorage(Bitmap bitmap) throws IOException {
        String fileName = "DIARY_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".jpg";
        File file = new File(requireContext().getFilesDir(), fileName);
        FileOutputStream fos = new FileOutputStream(file);
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);
        fos.close();
        return Uri.fromFile(file);
    }

    private void showFeelingsDialog() {
        AlertDialog alertDialog = new AlertDialog.Builder(requireContext())
                .setTitle(R.string.diary_write_feeling)
                .setItems(feelings, (dialog, which) -> diaryViewModel.feeling.setValue(feelings[which]))
                .create();
        alertDialog.show();
    }

    private void saveDiary() {
        if (diaryViewModel.addDiary()) {
            Toast.makeText(requireContext(), R.string.diarywritingfragment_done, Toast.LENGTH_SHORT).show();
            NavHostFragment.findNavController(this).navigate(R.id.action_diaryWritingFragment_to_diaryListFragment);
        } else {
            Toast.makeText(requireContext(), R.string.diarywritingfragment_notice_write_all_feild, Toast.LENGTH_SHORT).show();
        }
    }

    private Bitmap loadBitmapFromUri(Uri uri) throws IOException {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            return ImageDecoder.decodeBitmap(ImageDecoder.createSource(requireContext().getContentResolver(), uri));
        } else {
            return MediaStore.Images.Media.getBitmap(requireContext().getContentResolver(), uri);
        }
    }

    private void toggleSpeechRecognition() {
        if (!recording) {
            startRecord();
            Toast.makeText(requireContext(), R.string.stt_start_notice, Toast.LENGTH_SHORT).show();
        } else {
            stopRecord();
        }
    }

    private void startRecord() {
        if (speechRecognizer == null) {
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(requireContext());
        }

        if (recordIntent == null) {
            recordIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            recordIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR");
        }

        recording = true;
        binding.recordButton.setText(getString(R.string.record_stop));
        speechRecognizer.setRecognitionListener(listener);
        speechRecognizer.startListening(recordIntent);
    }

    private void stopRecord() {
        recording = false;
        binding.recordButton.setText(getString(R.string.record_start));
        speechRecognizer.stopListening();
        Toast.makeText(requireContext(), getString(R.string.record_done), Toast.LENGTH_SHORT).show();
    }


    private final RecognitionListener listener = new RecognitionListener() {
        @Override
        public void onReadyForSpeech(Bundle bundle) {
        }

        @Override
        public void onBeginningOfSpeech() {
        }

        @Override
        public void onRmsChanged(float v) {
        }

        @Override
        public void onBufferReceived(byte[] bytes) {
        }

        @Override
        public void onEndOfSpeech() {
        }

        @Override
        public void onError(int error) {
        }

        @Override
        public void onResults(Bundle bundle) {
            ArrayList<String> matches = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            if (matches != null && !matches.isEmpty()) {
                String originText = diaryViewModel.content.getValue();
                StringBuilder newText = new StringBuilder(originText);

                for (String match : matches) {
                    newText.append(match).append(" ");
                }

                diaryViewModel.content.setValue(newText.toString());
            }

            if (recording) {
                speechRecognizer.startListening(recordIntent);
            }
        }

        @Override
        public void onPartialResults(Bundle bundle) {
        }

        @Override
        public void onEvent(int i, Bundle bundle) {
        }
    };
}
