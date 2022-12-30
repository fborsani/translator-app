package com.example.mobiletranslator.ui;

import android.content.res.Resources;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.mobiletranslator.R;
import com.example.mobiletranslator.TranslatorManager;
import com.example.mobiletranslator.UsageData;
import com.example.mobiletranslator.db.DbManager;

public class FragmentConfig extends Fragment {

    private void printUserData(TextView targetField, TranslatorManager tm, Resources resources){
        UsageData data = tm.getUsageStats();
        String percent = tm.getUsageStats().getCharPerc().equals("0%") ? resources.getString(R.string.fragment_less_than_1) : tm.getUsageStats().getCharPerc();
        String text = String.format(resources.getString(R.string.label_usage_data), percent, data.getCharCount(), data.getCharLimit());
        targetField.setText(text);
    }

    public FragmentConfig() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_config, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //list of references to layout elements
        final EditText apiKeyField = getView().findViewById(R.id.apiKeyField);
        final TextView charQuotaField = getView().findViewById(R.id.quotaCharsLeft);
        final Button getUsageStatsBtn = getView().findViewById(R.id.getUsageStatsBtn);
        final Button saveApiKeyBtn = getView().findViewById(R.id.saveApiKeyBtn);

        final DbManager dbm = new DbManager(getView().getContext());
        final TranslatorManager tm = new TranslatorManager(getView().getContext());
        final Resources resources = getResources();


        //buttons functionality
        printUserData(charQuotaField,tm,resources);
        apiKeyField.setText(dbm.getApiKey());

        saveApiKeyBtn.setOnClickListener(vSave -> {
            dbm.setApiKey(apiKeyField.getText().toString());
            printUserData(charQuotaField,tm,resources);
        });

        getUsageStatsBtn.setOnClickListener(vStats -> {printUserData(charQuotaField,tm,resources);});
    }
}