package com.firebase.ui.auth.ui.intro;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.auth.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class PageThree extends Fragment {


    public static PageThree newInstance() {
        // Required empty public constructor
        return new PageThree();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fui_fragment_page_three, container, false);
    }

}
