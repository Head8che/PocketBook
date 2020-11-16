package com.example.pocketbook.fragment;

import android.app.ActionBar;
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
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.pocketbook.R;
import com.example.pocketbook.adapter.LinearBookAdapter;
import com.example.pocketbook.adapter.SearchStateAdapter;
import com.example.pocketbook.model.BookList;
import com.example.pocketbook.model.User;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class SearchFragment extends Fragment{

    private OnSearchChangeListener mCallback;

    private static final String TAG = "SearchFragment";
    private static final int LIMIT = 20;

    private FirebaseFirestore mFirestore;
    private Query mQuery;
    private RecyclerView mBooksRecycler;
    private LinearBookAdapter mAdapter;

    private User currentUser;
    private BookList catalogue;

    private DocumentSnapshot lastVisible;
    private boolean isScrolling = false;
    private boolean isLastItemReached = false;

    private SearchView searchView;

    private TabLayout tabLayout;
    private ViewPager2 pager;
    private SearchStateAdapter searchStateAdapter;

    public static SearchFragment newInstance(User user, BookList catalogue) {
        SearchFragment searchFragment = new SearchFragment();
        Bundle args = new Bundle();
        args.putSerializable("SF_USER", user);
        args.putSerializable("SF_CATALOGUE", catalogue);
        searchFragment.setArguments(args);
        return searchFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            this.currentUser = (User) getArguments().getSerializable("SF_USER");
            this.catalogue = (BookList) getArguments().getSerializable("SF_CATALOGUE");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (container != null) {
            container.removeAllViews();
        }
        View v = inflater.inflate(R.layout.fragment_search, container, false);

        return v;
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle savedInstanceState) {
        searchStateAdapter = new SearchStateAdapter(this);
        tabLayout = v.findViewById(R.id.searchFragTabLayout);
        pager = v.findViewById(R.id.searchFragPager);
        pager.setAdapter(searchStateAdapter);

        new TabLayoutMediator(tabLayout, pager, (tab, position) -> {
            if(position == 1)  tab.setText("All");
            else tab.setText("Owned");
        }).attach();

        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        searchView = v.findViewById(R.id.searchView);

        // Assumes current activity is the searchable activity
        searchView.setIconifiedByDefault(false); // Do not iconify the widget; expand it by default

        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String newText) {
                // calling the update query of the active fragment
                // and updating the Firebase query
                FragmentManager fm = getChildFragmentManager();
                Fragment f = (SearchMainFragment) fm.findFragmentByTag("f"+ pager.getCurrentItem()); // android automatically assigns tags to newly created fragments
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

    // This is the interface that the Activity will implement
    // so that this Fragment can communicate with the Activity.
    public interface OnSearchChangeListener {
        void messageFromOnSearchFrag(String text);
    }

    // This method insures that the Adapter has actually implemented our
    // listener and that it isn't null.
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnSearchChangeListener) {
            mCallback = (OnSearchChangeListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnSearchChangeListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallback = null;
    }
}