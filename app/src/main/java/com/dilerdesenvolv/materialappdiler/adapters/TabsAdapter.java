package com.dilerdesenvolv.materialappdiler.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ImageSpan;

import com.dilerdesenvolv.materialappdiler.R;
import com.dilerdesenvolv.materialappdiler.fragments.CarFragment;
import com.dilerdesenvolv.materialappdiler.fragments.CarLuxuryFragment;
import com.dilerdesenvolv.materialappdiler.fragments.CarOldFragment;
import com.dilerdesenvolv.materialappdiler.fragments.CarPopularFragment;
import com.dilerdesenvolv.materialappdiler.fragments.CarSportFragment;

/**
 * Created by T-Gamer on 23/07/2016.
 */
public class TabsAdapter extends FragmentStatePagerAdapter {

    private Context mContext;
    private String[] mTitles = new String[5];
    private int[] icons = new int[]{ R.drawable.car_1, R.drawable.car_1, R.drawable.car_2, R.drawable.car_3, R.drawable.car_4 };
    private int mHeightIcon = 0;

    public TabsAdapter(FragmentManager fm, Context c) {
        super(fm);

        mContext = c;
        double scale = c.getResources().getDisplayMetrics().density;
        mHeightIcon = (int) (24 * scale + 0.5f);

        mTitles[0] = mContext.getResources().getString(R.string.TODOS);
        mTitles[1] = mContext.getResources().getString(R.string.LUXO);
        mTitles[2] = mContext.getResources().getString(R.string.SPORT);
        mTitles[3] = mContext.getResources().getString(R.string.COLECIONADOR);
        mTitles[4] = mContext.getResources().getString(R.string.POPULAR);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment frag = null;

        if (position == 0) { // ALL CARS
            frag = new CarFragment();
        } else if (position == 1){ // LUXURY CAR
            frag = new CarLuxuryFragment();
        } else if (position == 2){ // SPORT CAR
            frag = new CarSportFragment();
        } else if (position == 3){ // OLD CAR
            frag = new CarOldFragment();
        } else if (position == 4){ // POPULAR CAR
            frag = new CarPopularFragment();
        }

        Bundle b = new Bundle();
        b.putInt("position", position);

        frag.setArguments(b);

        return frag;
    }

    @Override
    public int getCount() {
        return mTitles.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        // com icone
//        Drawable d = ContextCompat.getDrawable(mContext, icons[position]);
//        d.setBounds(0, 0, mHeightIcon, mHeightIcon);
//
//        ImageSpan is = new ImageSpan(d);
//        SpannableString sp = new SpannableString(" ");
//        sp.setSpan(is, 0, sp.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        return mTitles[position];
//        return sp;
    }

}
