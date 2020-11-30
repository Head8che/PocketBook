package com.example.pocketbook.fragment;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;

import com.example.pocketbook.R;
import com.example.pocketbook.adapter.SearchStateAdapter;
import com.example.pocketbook.model.User;
import com.example.pocketbook.util.KeyboardHandler;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

/**
 * A {@link Fragment} subclass.
 * Use the {@link #newInstance(User) newInstance}  method to create an instance of this fragment.
 */
public class SearchFragment extends Fragment{

    private User currentUser;
    private ViewPager2 pager;


    /**
     * method to create a new SearchFragment instance that bundles the user information to be accessible
     * @param user current user as a User object
     * @return a new instance of SearchFragment
     */
    public static SearchFragment newInstance(User user) {
        SearchFragment searchFragment = new SearchFragment();
        Bundle args = new Bundle();
        args.putSerializable("SF_USER", user);
        searchFragment.setArguments(args);
        return searchFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            this.currentUser = (User) getArguments().getSerializable("SF_USER");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (container != null) {
            container.removeAllViews();
        }

        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View rootView, @Nullable Bundle savedInstanceState) {
        SearchStateAdapter searchStateAdapter
                = new SearchStateAdapter(this, currentUser);
        TabLayout tabLayout = rootView.findViewById(R.id.searchFragTabLayout);
        pager = rootView.findViewById(R.id.searchFragPager);
        pager.setAdapter(searchStateAdapter);

        new TabLayoutMediator(tabLayout, pager, (tab, position) -> {
            if(position == 0)  tab.setText("All");
            else tab.setText("Non-Exchange");
        }).attach();

        if (getActivity() != null) {

            SearchManager searchManager = (SearchManager) getActivity()
                    .getSystemService(Context.SEARCH_SERVICE);
            SearchView searchView = rootView.findViewById(R.id.searchView);

            // Assumes current activity is the searchable activity
            searchView.setIconifiedByDefault(false); // Do not iconify the widget; expand it

            KeyboardHandler keyboardHandler = new KeyboardHandler(rootView, getActivity());
            keyboardHandler.hideViewOnKeyboardUp(R.id.bottomNavigationView);

            searchView.setSearchableInfo(searchManager
                    .getSearchableInfo(getActivity().getComponentName()));

            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextChange(String newText) {
                    // calling the update query of the active fragment
                    // and updating the Firebase query
                    FragmentManager fm = getChildFragmentManager();

                    // android automatically assigns tags to newly created fragments
                    SearchMainFragment f = (SearchMainFragment)
                            fm.findFragmentByTag("f" + pager.getCurrentItem());

                    if (f != null)
                        f.updateQuery(newText);
                    return true;
                }

                @Override
                public boolean onQueryTextSubmit(String query) {
                    Log.i("onQueryTextSubmit", query);
                    return true;
                }
            });
        }

    }


    @Override
    public void onDetach() {
        super.onDetach();
        }
}