package com.example.pocketbook.adapter;

import android.os.Bundle;

import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;

import com.example.pocketbook.R;
import com.example.pocketbook.fragment.ViewMyBookFragment;
import com.example.pocketbook.model.Book;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.pocketbook.fragment.ViewMyBookBookFragment;
import com.example.pocketbook.fragment.ViewMyBookRequestsFragment;

public class ViewMyBookPagerAdapter extends FragmentPagerAdapter {

    @StringRes
    private int numOfTabs;
    private Book book;

    public ViewMyBookPagerAdapter(FragmentManager fm, int numOfTabs, Book book) {
        super(fm);
        this.numOfTabs = numOfTabs;
        this.book = book;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                ViewMyBookBookFragment nextFrag = ViewMyBookBookFragment.newInstance(this.book);
                Bundle bundle = new Bundle();
                bundle.putSerializable("VMBPA_BOOK", this.book);
                nextFrag.setArguments(bundle);
                return nextFrag;
            case 1:
                ViewMyBookRequestsFragment requestsFrag = ViewMyBookRequestsFragment.newInstance(this.book);
                Bundle requestsBundle = new Bundle();
                requestsBundle.putSerializable("VMBPA_BOOK", this.book);
                requestsFrag.setArguments(requestsBundle);
                return requestsFrag;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return numOfTabs;
    }
}