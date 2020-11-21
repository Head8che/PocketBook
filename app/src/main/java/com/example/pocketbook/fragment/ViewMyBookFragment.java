package com.example.pocketbook.fragment;

import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.pocketbook.R;
import com.example.pocketbook.adapter.ViewMyBookPagerAdapter;
import com.example.pocketbook.model.Book;
import com.example.pocketbook.model.User;
import com.example.pocketbook.util.FirebaseIntegrity;
import com.google.android.material.tabs.TabLayout;

import java.util.Objects;


/**
 * Allows users to delete a book and move between the Book and Requests tab for their books
 */
public class ViewMyBookFragment extends Fragment {

    private Book book;
    private User currentUser;

    public ViewMyBookFragment() {
        // Required empty public constructor
    }

    /**
     * create a new instance of the ViewMyBookFragment
     * @param user user object
     * @param book book object
     * @return a new instance of the ViewMyBookFragment
     */
    public static ViewMyBookFragment newInstance(User user, Book book) {
        ViewMyBookFragment fragment = new ViewMyBookFragment();
        Bundle args = new Bundle();
        args.putSerializable("VMBF_USER", user);
        args.putSerializable("VMBF_BOOK", book);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.currentUser = (User) getArguments().getSerializable("VMBF_USER");
            this.book = (Book) getArguments().getSerializable("VMBF_BOOK");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_view_my_book,
                container, false);

        // access the layout materials
        TabLayout tabLayout = rootView.findViewById(R.id.viewMyBookFragTabLayout);
        // TabItem bookTab = rootView.findViewById(R.id.viewMyBookFragBookTab);
        // TabItem requestsTab = rootView.findViewById(R.id.viewMyBookFragRequestsTab);
        ViewPager viewPager = rootView.findViewById(R.id.viewMyBookFragViewPager);
        // Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.viewMyBookFragToolbar);
        ImageView backButton = (ImageView) rootView.findViewById(R.id.viewMyBookFragBackBtn);
        TextView deleteButton = (TextView) rootView.findViewById(R.id.viewMyBookFragDeleteBtn);

        // TODO: update tab title
//        if (book.getStatus().equals("ACCEPTED")) {
//            Objects.requireNonNull(tabLayout.getTabAt(2)).setText("ACCEPTED");
//        }

        // go back when backButton is clicked
        backButton.setOnClickListener(v -> Objects.requireNonNull(getActivity()).onBackPressed());

        // show delete dialog when deleteButton is clicked
        deleteButton.setOnClickListener(v -> {
            AlertDialog diaBox = AskOption();
            diaBox.show();
        });

        // set up the adapter
        ViewMyBookPagerAdapter viewMyBookPagerAdapter =
                new ViewMyBookPagerAdapter(getChildFragmentManager(),
                        tabLayout.getTabCount(), book);

        viewPager.setAdapter(viewMyBookPagerAdapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        // handle tab selection
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}
            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        return rootView;
    }

    /**
     * delete dialog
     * @return a dialog the prompts the user to confirm deleting the book
     */
    private AlertDialog AskOption() {
        // return a new Alert Dialog for deleting the book
        return new AlertDialog.Builder(Objects.requireNonNull(getContext()))
                .setMessage("Delete this book?")
                .setPositiveButton("Delete", (dialog, whichButton) -> {
                    dialog.dismiss();  // dismiss the delete dialog

                    // delete the book
                    FirebaseIntegrity.deleteBookFirebase(book);

                    // return to previous activity
                    Objects.requireNonNull(getActivity()).onBackPressed();
                })
                .setNegativeButton("cancel", (dialog, which) -> dialog.dismiss())
                .create();
    }
}