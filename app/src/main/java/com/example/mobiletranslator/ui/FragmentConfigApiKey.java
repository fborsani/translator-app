package com.example.mobiletranslator.ui;

import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.mobiletranslator.AppException;
import com.example.mobiletranslator.R;
import com.example.mobiletranslator.TranslatorManager;
import com.example.mobiletranslator.UsageData;
import com.example.mobiletranslator.db.DbManager;

public class FragmentConfigApiKey extends Fragment {

    private void printUserData(View view, boolean showMsg) {
        try {
            Resources resources = view.getResources();
            TranslatorManager tm = new TranslatorManager(view.getContext());
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

            TextView keyStatusField = view.findViewById(R.id.apiStatusField);
            TextView apiPlanField = view.findViewById(R.id.apiPlanField);
            TextView usageDataField = view.findViewById(R.id.usageDataField);
            TextView percentField = view.findViewById(R.id.usageDataPercField);

            keyStatusField.setText(keyStatus);
            apiPlanField.setText(apiPlan);
            usageDataField.setText(usageData);
            percentField.setText(percent);

            if(showMsg) {
                SnackBarUtility.displayMessage(getActivity(), "Key is valid", SnackBarUtility.SUCCESS);
            }
        }
        catch(AppException e){
            SnackBarUtility.displayMessage(getActivity(),e);
        }
    }

    public FragmentConfigApiKey() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_config_apikey, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //list of references to layout elements
        final EditText apiKeyField = getView().findViewById(R.id.apiKeyField);
        final Button getUsageStatsBtn = getView().findViewById(R.id.getUsageStatsBtn);
        final Button saveApiKeyBtn = getView().findViewById(R.id.saveApiKeyBtn);

        final DbManager dbm = new DbManager(getView().getContext());

        //buttons functionality
        apiKeyField.setText(dbm.getApiKey());

        saveApiKeyBtn.setOnClickListener(vSave -> {
            dbm.setApiKey(apiKeyField.getText().toString());
            printUserData(getView(), true);
        });

        getUsageStatsBtn.setOnClickListener(vStats -> printUserData(getView(), true));

        //preload api key info fields
        if(apiKeyField.getText() != null && !apiKeyField.getText().toString().trim().isEmpty()){
            printUserData(getView(), false);
        }
    }

}
