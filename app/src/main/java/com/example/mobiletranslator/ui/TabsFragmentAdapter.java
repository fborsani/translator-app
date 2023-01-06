package com.example.mobiletranslator.ui;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class TabsFragmentAdapter extends FragmentStateAdapter {

    public TabsFragmentAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch(position){
            case 0:
                return new FragmentOriginalText();
            case 1:
                return new FragmentTranslatedText();
            default:
                return new FragmentConfig();
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}