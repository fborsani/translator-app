package com.example.mobiletranslator.ui;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobiletranslator.LocalDataManager;
import com.example.mobiletranslator.R;
import com.example.mobiletranslator.db.LanguageItem;

import java.util.ArrayList;

public class FilesRecyclerViewAdapter extends RecyclerView.Adapter<FilesRecyclerViewAdapter.ViewHolder> {
    private final ArrayList<LanguageItem> languageList;
    private final LocalDataManager ldm;
    private final Activity fatherActivity;

    private class OnClickDelete implements View.OnClickListener{
        private final int position;

        public OnClickDelete(int position){
            super();
            this.position = position;
        }

        @Override
        public void onClick(View view) {
            ldm.deleteFile(LocalDataManager.OCR_FOLDER, languageList.get(position).getFilename(), fatherActivity);
        }
    }

    private class OnClickDownload implements View.OnClickListener{
        private final int position;

        public OnClickDownload(int position){
            super();
            this.position = position;
        }
        @Override
        public void onClick(View view) {
            ldm.downloadFileConfirmDialog(
                    languageList.get(position).getFilename(),
                    LocalDataManager.OCR_FOLDER,
                    Uri.parse(ldm.getOcrDownloadStr()+"/"+languageList.get(position).getFilename()),
                    fatherActivity);
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView languageTextView;
        private final ImageButton downloadBtn, deleteBtn;

        public ViewHolder(View itemView) {
            super(itemView);
            languageTextView = itemView.findViewById(R.id.rowLanguageText);
            downloadBtn = itemView.findViewById(R.id.rowDownloadBtn);
            deleteBtn = itemView.findViewById(R.id.rowDeleteBtn);

            }

        public void bindValues(String languageName,
                               boolean downloaded,
                               View.OnClickListener downloadEvent,
                               View.OnClickListener deleteEvent){

            languageTextView.setText(languageName);
            downloadBtn.setOnClickListener(downloadEvent);
            deleteBtn.setOnClickListener(deleteEvent);

            if(downloaded){
                downloadBtn.setVisibility(View.GONE);
                deleteBtn.setVisibility(View.VISIBLE);
            }
            else{
                downloadBtn.setVisibility(View.VISIBLE);
                deleteBtn.setVisibility(View.GONE);
            }
        }
    }

    FilesRecyclerViewAdapter(Activity fatherActivity, ArrayList<LanguageItem> languageList){
        this.languageList = languageList;
        this.fatherActivity = fatherActivity;
        ldm = new LocalDataManager(fatherActivity.getApplicationContext());
    }

    @NonNull
    @Override
    public FilesRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View contactView = inflater.inflate(R.layout.file_list_row, parent, false);
        return new ViewHolder(contactView);
    }

    @Override
    public void onBindViewHolder(FilesRecyclerViewAdapter.ViewHolder holder, int position) {
        LanguageItem currItem = languageList.get(position);
        String languageName = currItem.toString();
        boolean downloaded = ldm.checkFile(LocalDataManager.OCR_FOLDER, currItem.getFilename());
        holder.bindValues(languageName,
                downloaded,
                new OnClickDownload(position),
                new OnClickDelete(position));
    }

    @Override
    public int getItemCount() { return languageList.size(); }
}
