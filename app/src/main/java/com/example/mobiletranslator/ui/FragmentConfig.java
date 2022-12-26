package com.example.mobiletranslator.ui;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.mobiletranslator.R;
import com.example.mobiletranslator.db.DbManager;

public class FragmentConfig extends Fragment {
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

        DbManager dbm = new DbManager(getView().getContext());

        EditText apiKeyField = getView().findViewById(R.id.apiKeyField);
        apiKeyField.setText(dbm.getApiKey());

        Button saveApiKeyBtn = getView().findViewById(R.id.saveApiKeyBtn);
        saveApiKeyBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                EditText apiKeyField = getView().findViewById(R.id.apiKeyField);
                DbManager dbm = new DbManager(v.getContext());
                dbm.setApiKey(apiKeyField.getText().toString());
            }
        });
    }
}