package smu.team3_orda_diary.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import smu.team3_orda_diary.R;
import smu.team3_orda_diary.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private Animation fadeAnimation, slideAnimation;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fadeAnimation = AnimationUtils.loadAnimation(requireContext(), R.anim.image_fade_in_out);
        slideAnimation = AnimationUtils.loadAnimation(requireContext(), R.anim.text_slide_up);
    }

    @Override
    public void onResume() {
        super.onResume();
        binding.imageViewMain.startAnimation(slideAnimation);
        binding.textViewTitle.startAnimation(slideAnimation);
        binding.textViewSubTitle.startAnimation(slideAnimation);
        binding.textViewInfo.startAnimation(fadeAnimation);
    }

    @Override
    public void onPause() {
        super.onPause();
        binding.imageViewMain.clearAnimation();
        binding.textViewTitle.clearAnimation();
        binding.textViewSubTitle.clearAnimation();
        binding.textViewInfo.clearAnimation();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
