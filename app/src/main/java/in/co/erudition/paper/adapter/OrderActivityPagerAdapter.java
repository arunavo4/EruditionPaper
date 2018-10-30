package in.co.erudition.paper.adapter;

import android.content.Context;

import androidx.annotation.Keep;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import in.co.erudition.paper.fragment.InvoicesFragment;
import in.co.erudition.paper.fragment.SubscriptionsFragment;

/**
 * Created by Arunavo Ray on 05-06-2018.
 */
@Keep
public class OrderActivityPagerAdapter extends FragmentPagerAdapter {

    private final int PAGE_COUNT = 2;
    private String tabTitles[] = new String[]{"Subscriptions", "Invoices"};
    private Context context;

    public OrderActivityPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return SubscriptionsFragment.newInstance();
            case 1:
                return InvoicesFragment.newInstance();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }

}
