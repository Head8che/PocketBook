package com.example.pocketbook.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.pocketbook.fragment.SearchMainFragment;
import com.example.pocketbook.model.User;

public class SearchStateAdapter extends FragmentStateAdapter {
    private User user;
    public SearchStateAdapter(Fragment fragment, User user) {
        super(fragment);
        this.user = user;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        // Return a NEW fragment instance in createFragment(int)
        // New Fragment displaying books(all or owned)

//        Fragment profileFragment = ProfileFragment.newInstance(currentUser);
        Fragment f = SearchMainFragment.newInstance(user, position);

//        Bundle args = new Bundle();
//        if (position == 0) // all books
//            args.putInt("type", 0);
//        else
//            args.putInt("type", 1); // owned books
//        f.setArguments(args);
        return f;
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
