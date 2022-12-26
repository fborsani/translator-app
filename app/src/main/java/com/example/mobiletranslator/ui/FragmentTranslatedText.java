package com.example.mobiletranslator.ui;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.mobiletranslator.R;

public class FragmentTranslatedText extends Fragment {

    public FragmentTranslatedText(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getParentFragmentManager().setFragmentResultListener("translationFragment", this, (requestKey, result) -> {
            TextView translatedField = getView().findViewById(R.id.translatedTextField);
            translatedField.setText(result.getString("translatedText"));
        });
        return inflater.inflate(R.layout.fragment_translated_text, container, false);
    }
}