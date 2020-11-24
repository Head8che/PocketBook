package com.example.pocketbook.adapter;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;


import com.example.pocketbook.fragment.OwnerFragment;
import com.example.pocketbook.model.User;


import java.util.ArrayList;
import java.util.List;

public class ProfilePageAdapter extends FragmentPagerAdapter {

    @StringRes
    private final int numOfTabs;
    private User currentUser;


    public ProfilePageAdapter(FragmentManager fm, int numOfTabs, User currentUser) {
        super(fm);
        this.numOfTabs = numOfTabs;
        this.currentUser = currentUser;
    }

    @Override
    public Fragment getItem(int position) {
        Log.e("Position", String.valueOf(position));
        switch (position) {
            case 0:
                OwnerFragment nextFrag = OwnerFragment.newInstance(this.currentUser);
                Bundle bundle = new Bundle();
                bundle.putSerializable("PF_USER", this.currentUser);
                nextFrag.setArguments(bundle);
                return nextFrag;
            case 1:
                OwnerFragment nextFrag1 = OwnerFragment.newInstance(this.currentUser);
                Bundle bundle1 = new Bundle();
                bundle1.putSerializable("PF_USER", this.currentUser);
                nextFrag1.setArguments(bundle1);
                return nextFrag1;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return numOfTabs;
    }
}
