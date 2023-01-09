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

import com.example.mobiletranslator.AppException;
import com.example.mobiletranslator.R;
import com.example.mobiletranslator.TranslatorManager;
import com.example.mobiletranslator.UsageData;
import com.example.mobiletranslator.db.DbManager;

public class FragmentConfig extends Fragment {

    private void printUserData(TextView[] targetField, Resources resources) {
        try {
            TranslatorManager tm = new TranslatorManager(getContext());
            UsageData data = tm.getUsageStats();
            boolean isLimitExceeded = data.isLimitReached();
            boolean isLimitActive = data.isCharLimitActive();
            String keyStatus, apiPlan, usageData, percent;

            if(isLimitActive){
                String fragmentLessThanOne = resources.getString(R.string.fragment_less_than_1);
                String usageDataTemp = resources.getString(R.string.label_usage_data);

                keyStatus = isLimitExceeded ? resources.getString(R.string.label_apikey_over_limit) : resources.getString(R.string.label_apikey_valid);
                apiPlan = resources.getString(R.string.label_plan_free);
                usageData = String.format(usageDataTemp, data.getCharCount(), data.getCharLimit());
                percent = tm.getUsageStats().getCharPerc().equals("0%") ? fragmentLessThanOne : tm.getUsageStats().getCharPerc();
            }
            else{
                keyStatus = resources.getString(R.string.label_apikey_valid);
                apiPlan = resources.getString(R.string.label_plan_pro);
                usageData = resources.getString(R.string.label_usage_unlimited);
                percent = resources.getString(R.string.label_usage_unlimited);
            }

            targetField[0].setText(keyStatus);
            targetField[1].setText(apiPlan);
            targetField[2].setText(usageData);
            targetField[3].setText(percent);
            SnackBarUtility.displayMessage(getActivity(),"Key is valid",SnackBarUtility.SUCCESS);
        }
        catch(AppException e){
            SnackBarUtility.displayMessage(getActivity(),e);
        }
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
        final TextView apiStatusField = getView().findViewById(R.id.apiStatusField);
        final TextView apiPlanField = getView().findViewById(R.id.apiPlanField);
        final TextView charQuotaField = getView().findViewById(R.id.usageDataField);
        final TextView charQuotaPercField = getView().findViewById(R.id.usageDataPercField);
        final Button getUsageStatsBtn = getView().findViewById(R.id.getUsageStatsBtn);
        final Button saveApiKeyBtn = getView().findViewById(R.id.saveApiKeyBtn);

        final DbManager dbm = new DbManager(getView().getContext());
        final Resources resources = getResources();
        final TextView[] apiInfoFields = {apiStatusField, apiPlanField, charQuotaField, charQuotaPercField};

        //preload api key info fields
        if(apiKeyField.getText() != null && !apiKeyField.getText().toString().trim().isEmpty()){
            printUserData(apiInfoFields, resources);
        }

        //buttons functionality
        apiKeyField.setText(dbm.getApiKey());

        saveApiKeyBtn.setOnClickListener(vSave -> {
            dbm.setApiKey(apiKeyField.getText().toString());
            printUserData(apiInfoFields, resources);
        });

        getUsageStatsBtn.setOnClickListener(vStats -> printUserData(apiInfoFields, resources));
    }
}