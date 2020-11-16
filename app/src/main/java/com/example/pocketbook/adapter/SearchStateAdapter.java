package com.example.pocketbook.adapter;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.pocketbook.fragment.SearchMainFragment;

public class SearchStateAdapter extends FragmentStateAdapter {
    public SearchStateAdapter(Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        // Return a NEW fragment instance in createFragment(int)
        // New Fragment displaying books(all or owned)

        Fragment f = new SearchMainFragment();

        Bundle args = new Bundle();
        if (position == 0) // all books
            args.putInt("type", 0);
        else
            args.putInt("type", 1); // owned books
        f.setArguments(args);
        return f;
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
