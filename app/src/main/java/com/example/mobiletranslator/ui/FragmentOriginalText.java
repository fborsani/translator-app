package com.example.mobiletranslator.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;

import com.example.mobiletranslator.FileUtility;
import com.example.mobiletranslator.ImageParser;
import com.example.mobiletranslator.R;
import com.example.mobiletranslator.TranslatorManager;
import com.example.mobiletranslator.db.DbManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;


public class FragmentOriginalText extends Fragment {
    private Uri fileUri;

    private final ArrayList<HashMap<String,Object>> languageDataList = new ArrayList<>();
    private String currentIsoIn;
    private String currentIsoOut;
    private boolean useFormal;

    private final ActivityResultLauncher<Intent> retrieveImageActivityResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                Uri uri = result.getData().getData();
                ImageParser ip = new ImageParser(getView().getContext(),"eng");
                EditText textField = getView().findViewById(R.id.textInputField);
                textField.setText(ip.parseUri(uri));
                ip.recycle();
            });

    private final ActivityResultLauncher<Intent> takePictureActivityResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    ImageParser ip = new ImageParser(getView().getContext(),"eng"); //TODO: set language from user input
                    EditText textField = getView().findViewById(R.id.textInputField);
                    textField.setText(ip.parseUri(fileUri));
                    ip.recycle();
                }
            });

    private final ActivityResultLauncher<Intent> retrieveTextFromFileActivityResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                EditText textField = getView().findViewById(R.id.textInputField);
                textField.setText(FileUtility.readFile(result.getData().getData(), getActivity().getContentResolver()));
            });

    public FragmentOriginalText(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_original_text, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ImageButton galleryBtn = getView().findViewById(R.id.openGalleryBtn);
        ImageButton takePictureBtn = getView().findViewById(R.id.takePictureBtn);
        ImageButton readFromFileBtn = getView().findViewById(R.id.openFileBtn);
        CheckBox checkFormal = getView().findViewById(R.id.checkUseFormal);

        checkFormal.setVisibility(View.GONE);

        galleryBtn.setOnClickListener(onClickGallery -> retrieveImageActivityResult.launch(FileUtility.createIntentGetImage()));

        readFromFileBtn.setOnClickListener(onClickReadFile -> retrieveTextFromFileActivityResult.launch(FileUtility.createIntentGetText()));

        takePictureBtn.setOnClickListener(onClickPicture -> {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            fileUri = FileUtility.createTempImageUri(getView().getContext());
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
            takePictureActivityResult.launch(intent);
        });

        //Set values for language spinners
        final DbManager dbm = new DbManager(getView().getContext());
        Spinner spinnerIn = getView().findViewById(R.id.languageFieldIn);
        Spinner spinnerOut = getView().findViewById(R.id.languageFieldOut);
        ArrayList<String> labels = dbm.getLanguages(languageDataList);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getView().getContext(), android.R.layout.simple_spinner_item, labels);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerIn.setAdapter(adapter);
        spinnerOut.setAdapter(adapter);

        spinnerIn.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                currentIsoIn = Objects.requireNonNull(languageDataList.get(position).get(DbManager.LANG_PARAM_ISO)).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {}
        });

        spinnerOut.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                currentIsoOut = Objects.requireNonNull(languageDataList.get(position).get(DbManager.LANG_PARAM_ISO)).toString();
                int showFormalCheckbox = (Integer) Objects.requireNonNull(languageDataList.get(position).get(DbManager.LANG_PARAM_FORMAL));

                CheckBox formalCheckbox = getView().findViewById(R.id.checkUseFormal);
                if(showFormalCheckbox == 1){
                    formalCheckbox.setVisibility(View.VISIBLE);
                }
                else{
                    formalCheckbox.setChecked(false);
                    formalCheckbox.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {}
        });

        //Formal translation checkbox
        CheckBox formalCheckbox = getView().findViewById(R.id.checkUseFormal);
        formalCheckbox.setOnCheckedChangeListener((compoundButton, checked) -> useFormal = checked);

        //Translate button
        Button translateBtn = getView().findViewById(R.id.translateBtn);
        translateBtn.setOnClickListener(onClickTranslate -> {
            TranslatorManager tm = new TranslatorManager(getView().getContext());
            EditText textField = getView().findViewById(R.id.textInputField);
            String translatedText = tm.translate(textField.getText().toString(),currentIsoIn, currentIsoOut, useFormal);
            Bundle result = new Bundle();
            result.putString("translatedText",translatedText);
            getParentFragmentManager().setFragmentResult("translationFragment",result);
        });
    }
}