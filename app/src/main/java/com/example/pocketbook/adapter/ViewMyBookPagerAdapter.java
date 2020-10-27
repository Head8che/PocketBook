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
    private String user_email;
    private String bookID;

    public ViewMyBookPagerAdapter(FragmentManager fm, int numOfTabs, String user_email, String bookID) {
        super(fm);
        this.numOfTabs = numOfTabs;
        this.user_email = user_email;
        this.bookID = bookID;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new ViewMyBookBookFragment(this.user_email, this.bookID);
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