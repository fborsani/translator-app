package com.example.mobiletranslator.ui;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.mobiletranslator.R;

public class FragmentTranslatedText extends Fragment {

    public FragmentTranslatedText(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_translated_text, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final EditText textField = getView().findViewById(R.id.translatedTextField);
        final Button sendToClipboardBtn = getView().findViewById(R.id.sendToClipboardBtn);
        final Button clearBtn = getView().findViewById(R.id.clearBtn);

        //Receive results from FragmentOriginalText
        getParentFragmentManager().setFragmentResultListener("translationFragment", this,
                (requestKey, result) -> textField.setText(result.getString("translatedText"))
        );

        //Buttons
        sendToClipboardBtn.setOnClickListener(onClickClipboard -> {
            String text = textField.getText().toString();
            ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Translated Text", text);
            clipboard.setPrimaryClip(clip);
            SnackBarUtility.displayMessage(getActivity(),"Saved to clipboard",SnackBarUtility.SUCCESS);
        });

        clearBtn.setOnClickListener(onClickClear -> textField.setText(""));
    }


}