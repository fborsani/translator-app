package com.example.mobiletranslator.ui;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.example.mobiletranslator.R;

public class FragmentTranslatedText extends Fragment {

    public FragmentTranslatedText(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getParentFragmentManager().setFragmentResultListener("translationFragment", this, (requestKey, result) -> {
            EditText translatedField = getView().findViewById(R.id.translatedTextField);
            translatedField.setText(result.getString("translatedText"));
        });
        return inflater.inflate(R.layout.fragment_translated_text, container, false);
    }
}