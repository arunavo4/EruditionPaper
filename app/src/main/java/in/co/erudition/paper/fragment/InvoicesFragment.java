package in.co.erudition.paper.fragment;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import in.co.erudition.paper.R;

/**
 * Created by Arunavo Ray on 05-06-2018.
 */

public class InvoicesFragment extends Fragment {

    public static InvoicesFragment newInstance() {
        // Required empty public constructor
        return new InvoicesFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_invoice, container, false);
    }
}
