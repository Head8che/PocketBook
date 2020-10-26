package com.example.pocketbook.adapter;

import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.pocketbook.fragment.ViewMyBookBookFragment;
import com.example.pocketbook.fragment.ViewMyBookRequestsFragment;

public class ViewMyBookPagerAdapter extends FragmentPagerAdapter {

    @StringRes
    private int numOfTabs;

    public ViewMyBookPagerAdapter(FragmentManager fm, int numOfTabs) {
        super(fm);
        this.numOfTabs = numOfTabs;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new ViewMyBookBookFragment();
            case 1:
                return new ViewMyBookRequestsFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return numOfTabs;
    }
}