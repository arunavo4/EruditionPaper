package com.firebase.ui.auth.ui.intro;

import android.content.Context;
import android.graphics.pdf.PdfDocument;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class IntroAdapter extends FragmentPagerAdapter {
    private final int PAGE_COUNT = 3;
    private Context context;

    public IntroAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return PageOne.newInstance();
            case 1:
                return PageTwo.newInstance();
            case 2:
                return PageThree.newInstance();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

}
