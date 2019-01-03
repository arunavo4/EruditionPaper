package com.firebase.ui.auth.ui.intro;


import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.firebase.ui.auth.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class PageOne extends Fragment {


    public static PageOne newInstance() {
        // Required empty public constructor
        return new PageOne();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fui_fragment_page_one, container, false);

        ImageView img = (ImageView) view.findViewById(R.id.img_one);
        Drawable drawable = (Drawable) img.getDrawable();

        Matrix matrix = new Matrix();
        float dx = ConverterUtils.convertDpToPx(getContext(),getResources().getInteger(R.integer.fui_one_dx));
        float dy = ConverterUtils.convertDpToPx(getContext(),getResources().getInteger(R.integer.fui_one_dy));
        matrix.postTranslate(dx,dy);
        img.setImageMatrix(matrix);

        return view;
    }

}
