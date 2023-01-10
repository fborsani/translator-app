package com.example.mobiletranslator.ui;

import static android.content.ClipDescription.MIMETYPE_TEXT_PLAIN;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.example.mobiletranslator.AppException;
import com.example.mobiletranslator.FileUtility;
import com.example.mobiletranslator.ImageParser;
import com.example.mobiletranslator.LocalDataManager;
import com.example.mobiletranslator.R;
import com.example.mobiletranslator.TranslatorManager;
import com.example.mobiletranslator.db.DbManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;


public class FragmentOriginalText extends Fragment {
    private Uri fileUri;

    private final ArrayList<HashMap<String,Object>> languageDataListIn = new ArrayList<>();
    private final ArrayList<HashMap<String,Object>> languageDataListOut = new ArrayList<>();
    private int langInIdx;
    private int langOutIdx;
    private boolean useFormal;

    private final ActivityResultLauncher<Intent> retrieveImageActivityResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                try {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent intent = result.getData();
                        String langIso3 = Objects.requireNonNull(languageDataListIn.get(langInIdx).get(DbManager.LANG_PARAM_ISO3)).toString();
                        LocalDataManager ldm = new LocalDataManager(getContext());

                        if(ldm.checkOcrFile(langIso3)) {
                            ImageParser ip = new ImageParser(getContext(),langIso3);
                            EditText textField = getView().findViewById(R.id.textInputField);

                            if (intent != null && intent.getData() != null) {
                                textField.setText(ip.parseUri(intent.getData()));
                            } else if (fileUri != null) {
                                textField.setText(ip.parseUri(fileUri));
                            }
                        }
                        else{
                            ldm.downloadOcrFile(langIso3);
                            SnackBarUtility.displayMessage(getActivity(), "Local file not found. Downloading", SnackBarUtility.INFO);
                        }
                    }
                }
                catch(AppException e){
                    SnackBarUtility.displayMessage(getActivity(),e);
                }
            });

    private final ActivityResultLauncher<Intent> retrieveTextFromFileActivityResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                Intent intent = result.getData();
                try {
                    if (result.getResultCode() == Activity.RESULT_OK && intent != null) {
                        EditText textField = getView().findViewById(R.id.textInputField);
                        textField.setText(FileUtility.readFile(intent.getData(), getActivity().getContentResolver()));
                    }
                }
                catch(IOException | NullPointerException e){
                    SnackBarUtility.displayMessage(getActivity(),e.getMessage(), SnackBarUtility.ERROR);
                }
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

        //list of references to layout items
        final View contentView = getActivity().findViewById(android.R.id.content);
        final EditText textField = getView().findViewById(R.id.textInputField);
        final ImageButton galleryBtn = getView().findViewById(R.id.openGalleryBtn);
        final ImageButton takePictureBtn = getView().findViewById(R.id.takePictureBtn);
        final ImageButton readFromFileBtn = getView().findViewById(R.id.openFileBtn);
        final ImageButton pasteBtn = getView().findViewById(R.id.pasteBtn);
        final Spinner spinnerIn = getView().findViewById(R.id.languageFieldIn);
        final Spinner spinnerOut = getView().findViewById(R.id.languageFieldOut);
        final CheckBox formalCheckbox = getView().findViewById(R.id.checkUseFormal);
        final Button translateBtn = getView().findViewById(R.id.translateBtn);
        final Button clearBtn = getView().findViewById(R.id.clearinput);

        //hide options when user edits the text field in order to free up space for the keyboard
        textField.setOnFocusChangeListener((arg0, onFocus) -> {
            LinearLayout optionsBlock = getView().findViewById(R.id.optionsBlock);
            if (onFocus) {
                optionsBlock.setVisibility(View.GONE);

                //detect if keyboard is closed by attaching a layoutListener to the view and listening for changes in window size
                contentView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    private int mPreviousHeight;

                    @Override
                    public void onGlobalLayout() {
                        int newHeight = contentView.getHeight();
                        if (mPreviousHeight != 0 && mPreviousHeight < newHeight) {
                            optionsBlock.setVisibility(View.VISIBLE);
                            textField.clearFocus();
                            contentView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        }
                        mPreviousHeight = newHeight;
                    }
                });
            }
        });

        //Import functionality
        readFromFileBtn.setOnClickListener(onClickReadFile -> retrieveTextFromFileActivityResult.launch(FileUtility.createIntentGetText()));
        galleryBtn.setOnClickListener(onClickGallery -> retrieveImageActivityResult.launch(FileUtility.createIntentGetImage()));
        takePictureBtn.setOnClickListener(onClickPicture -> {
            try {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                fileUri = FileUtility.createTempImageUri(getView().getContext());
                intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                retrieveImageActivityResult.launch(intent);
            }
            catch(IOException e){
                SnackBarUtility.displayMessage(getActivity(), e.getMessage(), SnackBarUtility.ERROR);
            }
        });

        pasteBtn.setOnClickListener(onClickPaste -> {
            ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
            if(clipboard.hasPrimaryClip() && clipboard.getPrimaryClipDescription().hasMimeType(MIMETYPE_TEXT_PLAIN)){
                ClipData.Item item = clipboard.getPrimaryClip().getItemAt(0);
                if(item.getText() != null){
                    textField.setText(item.getText().toString());
                    SnackBarUtility.displayMessage(getActivity(),"Text pasted",SnackBarUtility.SUCCESS);
                }
                else{
                    SnackBarUtility.displayMessage(getActivity(),"Clipboard content is not plain text",SnackBarUtility.ERROR);
                }
            }
            else{
                SnackBarUtility.displayMessage(getActivity(),"Nothing to copy",SnackBarUtility.ERROR);
            }
        });


        //Set values for language spinners
        final DbManager dbm = new DbManager(getView().getContext());
        ArrayList<String> labelsIn = dbm.getLanguagesIn(languageDataListIn);
        ArrayList<String> labelsOut = dbm.getLanguagesOut(languageDataListOut);

        ArrayAdapter<String> adapterIn = new ArrayAdapter<>(getView().getContext(), android.R.layout.simple_spinner_item, labelsIn);
        adapterIn.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        ArrayAdapter<String> adapterOut = new ArrayAdapter<>(getView().getContext(), android.R.layout.simple_spinner_item, labelsOut);
        adapterOut.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerIn.setAdapter(adapterIn);
        spinnerOut.setAdapter(adapterOut);

        spinnerIn.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                langInIdx = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {}
        });

        spinnerOut.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                langOutIdx = position;
                int showFormalCheckbox = (Integer) Objects.requireNonNull(languageDataListOut.get(position).get(DbManager.LANG_PARAM_FORMAL));

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

        //Formal translation checkbox visibility logic
        formalCheckbox.setVisibility(View.GONE);
        formalCheckbox.setOnCheckedChangeListener((compoundButton, checked) -> useFormal = checked);

        //Translate button
        translateBtn.setOnClickListener(onClickTranslate -> {
            try {
                String currentIsoIn = Objects.requireNonNull(languageDataListIn.get(langInIdx).get(DbManager.LANG_PARAM_ISO)).toString();
                String currentIsoOut = Objects.requireNonNull(languageDataListOut.get(langOutIdx).get(DbManager.LANG_PARAM_ISO)).toString();
                TranslatorManager tm = new TranslatorManager(getView().getContext());
                String translatedText = tm.translate(textField.getText().toString(), currentIsoIn, currentIsoOut, useFormal);
                Bundle result = new Bundle();
                result.putString("translatedText", translatedText);
                getParentFragmentManager().setFragmentResult("translationFragment", result);
                SnackBarUtility.displayMessage(getActivity(),"Translation completed",SnackBarUtility.SUCCESS);
            }
            catch(AppException e){
                SnackBarUtility.displayMessage(getActivity(), e);
            }
        });

        //Clear button
        clearBtn.setOnClickListener(onClickClear -> textField.setText(""));
    }
}