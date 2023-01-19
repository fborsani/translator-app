package com.example.mobiletranslator.ui;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mobiletranslator.R;
import com.google.android.material.tabs.TabLayout;

public class FragmentConfig extends Fragment {
    private static class TabsFragmentAdapter extends FragmentStateAdapter {

        public TabsFragmentAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
            super(fragmentManager, lifecycle);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            switch(position){
                case 0:
                    return new FragmentConfigApiKey();
                case 1:
                default:
                    return new FragmentConfigFiles();
            }
        }

        @Override
        public int getItemCount() {
            return 3;
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

        FragmentManager fm = requireActivity().getSupportFragmentManager();
        FragmentConfig.TabsFragmentAdapter sa = new FragmentConfig.TabsFragmentAdapter(fm, getLifecycle());
        final ViewPager2 pa = view.findViewById(R.id.pagerConfig);
        pa.setAdapter(sa);

        TabLayout tabLayout = view.findViewById(R.id.tabLayoutConfig);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) { pa.setCurrentItem(tab.getPosition()); }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        pa.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                tabLayout.selectTab(tabLayout.getTabAt(position));
            }
        });
    }
}