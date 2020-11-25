package com.example.pocketbook.adapter;

import android.os.Bundle;

import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;


import com.example.pocketbook.fragment.BorrowerFragment;
import com.example.pocketbook.fragment.OwnerFragment;
import com.example.pocketbook.model.User;

public class ProfilePageAdapter extends FragmentPagerAdapter {

    @StringRes
    private int numOfTabs;
    private User currentUser;


    public ProfilePageAdapter(FragmentManager fm, int numOfTabs, User currentUser) {
        super(fm);
        this.numOfTabs = numOfTabs;
        this.currentUser = currentUser;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                OwnerFragment ownerFrag = OwnerFragment.newInstance(this.currentUser);
                Bundle ownerBundle = new Bundle();
                ownerBundle.putSerializable("PF_USER", this.currentUser);
                ownerFrag.setArguments(ownerBundle);
                return ownerFrag;
            case 1:
                BorrowerFragment borrowerFrag = BorrowerFragment.newInstance(this.currentUser);
                Bundle borrowerBundle = new Bundle();
                borrowerBundle.putSerializable("PF_USER", this.currentUser);
                borrowerFrag.setArguments(borrowerBundle);
                return borrowerFrag;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return numOfTabs;
    }
}
