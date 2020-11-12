package com.example.pocketbook.fragment;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
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
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;

import java.util.Objects;

public class ViewMyBookFragment extends Fragment {

    private Book book;
    private User currentUser;

    public ViewMyBookFragment() {
        // Required empty public constructor
    }

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
        View rootView = inflater.inflate(R.layout.fragment_view_my_book, container, false);

        // Inflate the layout for this fragment
        TabLayout tabLayout = rootView.findViewById(R.id.viewMyBookFragTabLayout);
        TabItem bookTab = rootView.findViewById(R.id.viewMyBookFragBookTab);
        TabItem requestsTab = rootView.findViewById(R.id.viewMyBookFragRequestsTab);
        ViewPager viewPager = rootView.findViewById(R.id.viewMyBookFragViewPager);

        Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.viewMyBookFragToolbar);
        ImageView backButton = (ImageView) rootView.findViewById(R.id.viewMyBookFragBackBtn);
        TextView deleteButton = (TextView) rootView.findViewById(R.id.viewMyBookFragDeleteBtn);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog diaBox = AskOption();
                diaBox.show();
            }
        });

        ViewMyBookPagerAdapter viewMyBookPagerAdapter =
                new ViewMyBookPagerAdapter(getChildFragmentManager(), tabLayout.getTabCount(), book/*, catalogue*/);

        viewPager.setAdapter(viewMyBookPagerAdapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        return rootView;
    }

    private AlertDialog AskOption() {
        return new AlertDialog.Builder(getContext())
                .setMessage("Delete this book?")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        //your deleting code
                        dialog.dismiss();
//                        catalogue.removeBook(book);
                        Objects.requireNonNull(getActivity()).onBackPressed();
                    }

                })
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
    }
}