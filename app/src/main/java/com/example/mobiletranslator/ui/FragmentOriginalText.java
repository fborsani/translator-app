package com.example.mobiletranslator.ui;

import static android.content.ClipDescription.MIMETYPE_TEXT_PLAIN;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
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
import com.example.mobiletranslator.db.LanguageItem;

import java.io.IOException;
import java.util.ArrayList;

public class FragmentOriginalText extends Fragment {
    private Uri fileUri;

    private  ArrayList<LanguageItem> languageDataListIn = new ArrayList<>();
    private  ArrayList<LanguageItem> languageDataListOut = new ArrayList<>();
    private int langInIdx;
    private int langOutIdx;
    private boolean useFormal;

    private final ActivityResultLauncher<Intent> retrieveImageActivityResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                try {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent intent = result.getData();
                        LanguageItem item = languageDataListIn.get(langInIdx);
                        LocalDataManager ldm = new LocalDataManager(requireContext());

                        if(ldm.checkFile(LocalDataManager.OCR_FOLDER, item.getFilename())) {
                            ImageParser ip = new ImageParser(getContext(),item.getIsoCode3());
                            EditText textField = requireView().findViewById(R.id.textInputField);

                            if (intent != null && intent.getData() != null) {
                                textField.setText(ip.parseUri(intent.getData()));
                            } else if (fileUri != null) {
                                textField.setText(ip.parseUri(fileUri));
                            }
                        }
                        else{
                            String message = requireView().getResources().getString(R.string.dialog_confirm_download_ocr);
                            ldm.downloadFileConfirmDialog(
                                    item.getFilename(),
                                    LocalDataManager.OCR_FOLDER,
                                    ldm.getOcrDownloadUri(),
                                    requireActivity(),
                                    String.format(message,item.getName()));
                        }
                    }
                }
                catch(AppException e){
                    NotificationUtility.displayMessage(requireActivity(),e);
                }
            });

    private final ActivityResultLauncher<Intent> retrieveTextFromFileActivityResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                Intent intent = result.getData();
                try {
                    if (result.getResultCode() == Activity.RESULT_OK && intent != null) {
                        EditText textField = requireView().findViewById(R.id.textInputField);
                        textField.setText(FileUtility.readFile(intent.getData(), requireActivity().getContentResolver()));
                    }
                }
                catch(IOException e){
                    NotificationUtility.displayMessage(requireActivity(),
                            e.getMessage(),
                            NotificationUtility.snackBarStyle.ERROR);
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
        final Activity activity = requireActivity();

        final View contentView = activity.findViewById(android.R.id.content);
        final EditText textField = activity.findViewById(R.id.textInputField);
        final ImageButton galleryBtn = activity.findViewById(R.id.openGalleryBtn);
        final ImageButton takePictureBtn = view.findViewById(R.id.takePictureBtn);
        final ImageButton readFromFileBtn = view.findViewById(R.id.openFileBtn);
        final ImageButton pasteBtn = view.findViewById(R.id.pasteBtn);
        final Spinner spinnerIn = view.findViewById(R.id.languageFieldIn);
        final Spinner spinnerOut = view.findViewById(R.id.languageFieldOut);
        final CheckBox formalCheckbox = view.findViewById(R.id.checkUseFormal);
        final Button translateBtn = view.findViewById(R.id.translateBtn);
        final Button clearBtn = view.findViewById(R.id.clearinput);

        final Resources resources = view.getResources();

        //hide options when user edits the text field in order to free up space for the keyboard
        textField.setOnFocusChangeListener((arg0, onFocus) -> {
            LinearLayout optionsBlock = view.findViewById(R.id.optionsBlock);
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

        final String intentTitleGallery = resources.getString(R.string.action_pick_gallery_app);
        final String intentTitleFileManager = resources.getString(R.string.action_pick_file_manager);

        //Import functionality
        readFromFileBtn.setOnClickListener(onClickReadFile -> retrieveTextFromFileActivityResult.launch(FileUtility.createIntentGetText(intentTitleGallery)));
        galleryBtn.setOnClickListener(onClickGallery -> retrieveImageActivityResult.launch(FileUtility.createIntentGetImage(intentTitleFileManager)));
        takePictureBtn.setOnClickListener(onClickPicture -> {
            try {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                fileUri = FileUtility.createTempImageUri(view.getContext());
                intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                retrieveImageActivityResult.launch(intent);
            }
            catch(IOException e){
                NotificationUtility.displayMessage(
                        activity,
                        e.getMessage(),
                        NotificationUtility.snackBarStyle.ERROR);
            }
        });

        pasteBtn.setOnClickListener(onClickPaste -> {
            ClipboardManager clipboard = (ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE);
            if(clipboard.hasPrimaryClip() && clipboard.getPrimaryClipDescription().hasMimeType(MIMETYPE_TEXT_PLAIN)){
                ClipData.Item item = clipboard.getPrimaryClip().getItemAt(0);
                if(item.getText() != null){
                    textField.setText(item.getText().toString());
                    NotificationUtility.displayMessage(
                            activity,
                            resources.getString(R.string.action_from_clipboard_done),
                            NotificationUtility.snackBarStyle.SUCCESS);
                }
                else{
                    NotificationUtility.displayMessage(
                            activity,
                            resources.getString(R.string.action_from_clipboard_error_empty),
                            NotificationUtility.snackBarStyle.ERROR);
                }
            }
            else{
                NotificationUtility.displayMessage(
                        activity,
                        resources.getString(R.string.action_from_clipboard_error_invalid_content),
                        NotificationUtility.snackBarStyle.ERROR);
            }
        });


        //Set values for language spinners
        final DbManager dbm = new DbManager(view.getContext());
        languageDataListIn = dbm.getLanguagesIn();
        languageDataListOut = dbm.getLanguagesOut();

        ArrayAdapter<LanguageItem> adapterIn = new ArrayAdapter<>(view.getContext(), android.R.layout.simple_spinner_item, languageDataListIn);
        adapterIn.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        ArrayAdapter<LanguageItem> adapterOut = new ArrayAdapter<>(view.getContext(), android.R.layout.simple_spinner_item, languageDataListOut);
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

                CheckBox formalCheckbox = view.findViewById(R.id.checkUseFormal);
                if(languageDataListOut.get(position).isAllowFormal()){
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
                String text = textField.getText().toString();
                if(!text.trim().equals("")) {
                    String currentIsoIn = languageDataListIn.get(langInIdx).getIsoCode();
                    String currentIsoOut = languageDataListOut.get(langOutIdx).getIsoCode();
                    TranslatorManager tm = new TranslatorManager(view.getContext());
                    String translatedText = tm.translate(textField.getText().toString(), currentIsoIn, currentIsoOut, useFormal);
                    Bundle result = new Bundle();
                    result.putString("translatedText", translatedText);
                    getParentFragmentManager().setFragmentResult("translationFragment", result);
                    NotificationUtility.displayMessage(
                            activity,
                            resources.getString(R.string.action_translate_done),
                            NotificationUtility.snackBarStyle.SUCCESS);
                }
                else{
                    NotificationUtility.displayMessage(
                            activity,
                            resources.getString(R.string.action_translate_error_empty),
                            NotificationUtility.snackBarStyle.ERROR);
                }
            }
            catch(AppException e){
                NotificationUtility.displayMessage(activity, e);
            }
        });

        //Clear button
        clearBtn.setOnClickListener(onClickClear -> textField.setText(""));
    }
}