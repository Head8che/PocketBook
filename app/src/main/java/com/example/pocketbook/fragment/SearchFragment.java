package com.example.pocketbook.fragment;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.pocketbook.R;
import com.example.pocketbook.adapter.LinearBookAdapter;
import com.example.pocketbook.adapter.SearchStateAdapter;
import com.example.pocketbook.model.Book;
import com.example.pocketbook.model.User;
import com.example.pocketbook.util.KeyboardHandler;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.Objects;

public class SearchFragment extends Fragment{

    private static final String TAG = "SearchFragment";
    private static final int LIMIT = 20;

    private FirebaseFirestore mFirestore;
    private Query mQuery;
    private RecyclerView mBooksRecycler;
    private LinearBookAdapter mAdapter;

    private User currentUser;

    private SearchView searchView;

    private TabLayout tabLayout;
    private ViewPager2 pager;
    private SearchStateAdapter searchStateAdapter;


    /**
     * Search fragment instance that bundles the user information to be accessible
     * @param user
     * @return
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
        View v = inflater.inflate(R.layout.fragment_search, container, false);

        return v;
    }

    @Override
    public void onViewCreated(@NonNull View rootView, @Nullable Bundle savedInstanceState) {
        searchStateAdapter = new SearchStateAdapter(this, currentUser);
        tabLayout = rootView.findViewById(R.id.searchFragTabLayout);
        pager = rootView.findViewById(R.id.searchFragPager);
        pager.setAdapter(searchStateAdapter);

        new TabLayoutMediator(tabLayout, pager, (tab, position) -> {
            if(position == 0)  tab.setText("All");
            else tab.setText("Non-Exchange");
        }).attach();

        SearchManager searchManager = (SearchManager) getActivity()
                .getSystemService(Context.SEARCH_SERVICE);
        searchView = rootView.findViewById(R.id.searchView);

        // Assumes current activity is the searchable activity
        searchView.setIconifiedByDefault(false); // Do not iconify the widget; expand it by default

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
                Fragment f = (SearchMainFragment)
                        fm.findFragmentByTag("f"+ pager.getCurrentItem());

                if(f != null)
                    ((SearchMainFragment) f).updateQuery(newText);
                return true;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.i("onQueryTextSubmit", query);
                // TODO: implement this
                // TODO: no reloading but conclusion
                return true;
            }
        });

    }


    @Override
    public void onDetach() {
        super.onDetach();
        }
}