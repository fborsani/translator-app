package com.example.mobiletranslator.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.mobiletranslator.FileUtility;
import com.example.mobiletranslator.ImageParser;
import com.example.mobiletranslator.R;


public class FragmentOriginalText extends Fragment {
    private final ActivityResultLauncher<Intent> retrieveImageActivityResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    Uri uri = result.getData().getData();
                    ImageParser ip = new ImageParser(getView().getContext(),"eng");
                    EditText textField = (EditText) getView().findViewById(R.id.textInputField);
                    textField.setText(ip.parseUri(uri));
                    ip.recycle();
                }
            });

    private final ActivityResultLauncher<Intent> takePictureActivityResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                }
            });

    private final ActivityResultLauncher<Intent> retrieveTextFromFileActivityResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    EditText textField = (EditText) getView().findViewById(R.id.textInputField);
                    textField.setText(FileUtility.readFile(result.getData().getData(), getActivity().getContentResolver()));
                }
            });


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_original_text, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button galleryBtn = getView().findViewById(R.id.openGalleryBtn);
        Button takePictureBtn =  getView().findViewById(R.id.takePictureBtn);
        Button readFromFileBtn = getView().findViewById(R.id.openFileBtn);

        galleryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                retrieveImageActivityResult.launch(FileUtility.createIntentGetImage());
            }
        });

        readFromFileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                retrieveTextFromFileActivityResult.launch(FileUtility.createIntentGetText());
            }
        });

        takePictureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                //intent.putExtra(MediaStore.EXTRA_OUTPUT, imgUri);
                takePictureActivityResult.launch(intent);
            }
        });
    }
}