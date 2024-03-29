package com.example.mobiletranslator.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobiletranslator.R;
import com.example.mobiletranslator.db.DbManager;

public class FragmentConfigFiles extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_config_files, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final RecyclerView filesList = view.findViewById(R.id.filesList);
        final ImageButton refreshBtn = view.findViewById(R.id.refreshFilesBtn);

        final DbManager dbm = new DbManager(view.getContext());

        refreshBtn.setOnClickListener(
            viewOnClick -> {
                FilesRecyclerViewAdapter adapter = new FilesRecyclerViewAdapter(requireActivity(), dbm.getLanguagesIn());
                filesList.setAdapter(adapter);
            }
        );

        FilesRecyclerViewAdapter adapter = new FilesRecyclerViewAdapter(requireActivity(), dbm.getLanguagesIn());
        filesList.setAdapter(adapter);
        filesList.setLayoutManager(new LinearLayoutManager(getContext()));
    }
}
