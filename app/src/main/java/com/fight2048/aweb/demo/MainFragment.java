package com.fight2048.aweb.demo;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.fight2048.aweb.WebActivity;
import com.fight2048.aweb.WebFragment;
import com.fight2048.aweb.demo.databinding.FragmentMainBinding;

public class MainFragment extends Fragment {
    private FragmentMainBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentMainBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.btActivity.setOnClickListener(v -> {
            String url = binding.et.getText().toString();
            WebActivity.start(getActivity(), TextUtils.isEmpty(url) ? "https://www.baidu.com" : url);
        });

        binding.btFragment.setOnClickListener(v -> {
            String url = binding.et.getText().toString();
            NavHostFragment.findNavController(MainFragment.this)
                    .navigate(R.id.WebFragment, WebFragment.bundle(TextUtils.isEmpty(url) ? "https://www.baidu.com" : url));
        });
    }
}
