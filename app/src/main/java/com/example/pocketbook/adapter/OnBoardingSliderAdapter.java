package com.example.pocketbook.adapter;

import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager.widget.PagerAdapter;

import com.example.pocketbook.R;

public class OnBoardingSliderAdapter extends PagerAdapter {

    /*
      CODE SOURCE:
      https://www.taimoorsikander.com/onboarding-screen-android-studio-city-guide-app-part-3/
     */

    Context context;
    LayoutInflater layoutInflater;

    public OnBoardingSliderAdapter(Context context) {
        this.context = context;
    }

    int images[] = {
             R.drawable.first_on_boarding_slide_image,
             R.drawable.second_on_boarding_slide_image,
             R.drawable.third_on_boarding_slide_image,
    };

    int headings[] = {
             R.string.firstOnBoardingSlideTitle,
             R.string.secondOnBoardingSlideTitle,
             R.string.thirdOnBoardingSlideTitle,
    };

    int descriptions[] = {
            R.string.firstOnBoardingSlideDescription,
            R.string.secondOnBoardingSlideDescription,
            R.string.thirdOnBoardingSlideDescription,
    };


    @Override
    public int getCount() {
        return headings.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return (view == (ConstraintLayout) object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.layout_slides, container, false);

        ImageView imageView = view.findViewById(R.id.slider_image);
        TextView heading = view.findViewById(R.id.slider_heading);
        TextView desc = view.findViewById(R.id.slider_desc);

        imageView.setImageResource(images[position]);
        heading.setText(headings[position]);
        desc.setText(descriptions[position]);

        container.addView(view);

        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((ConstraintLayout) object);
    }

}
