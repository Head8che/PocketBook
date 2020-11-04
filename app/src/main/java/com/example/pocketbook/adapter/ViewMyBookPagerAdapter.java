package com.example.pocketbook.adapter;

import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import com.example.pocketbook.model.Book;
import com.example.pocketbook.model.BookList;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.pocketbook.fragment.ViewMyBookBookFragment;
import com.example.pocketbook.fragment.ViewMyBookRequestsFragment;

public class ViewMyBookPagerAdapter extends FragmentPagerAdapter {

    @StringRes
    private int numOfTabs;
    private Book book;
    private BookList catalogue;

    public ViewMyBookPagerAdapter(FragmentManager fm, int numOfTabs, Book book, BookList catalogue) {
        super(fm);
        this.numOfTabs = numOfTabs;
        this.book = book;
        this.catalogue = catalogue;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new ViewMyBookBookFragment(this.book, this.catalogue);
            case 1:
                return new ViewMyBookRequestsFragment(this.book);
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return numOfTabs;
    }
}