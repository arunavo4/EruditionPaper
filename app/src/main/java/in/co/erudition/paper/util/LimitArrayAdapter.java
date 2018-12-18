package in.co.erudition.paper.util;

import android.content.Context;
import android.widget.ArrayAdapter;

import java.util.List;

import androidx.annotation.NonNull;

public class LimitArrayAdapter<T> extends ArrayAdapter<String> {
    private int count;

    public LimitArrayAdapter(@NonNull Context context, int resource, @NonNull List<String> objects, int count) {
        super(context, resource, objects);
        if (count>objects.size()){
            this.count = objects.size();
        }else {
            this.count = count;
        }
    }

    public LimitArrayAdapter(@NonNull Context context, int resource, @NonNull String[] objects, int count) {
        super(context, resource, objects);
        if (count>objects.length){
            this.count = objects.length;
        }else {
            this.count = count;
        }
    }

    @Override
    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

}
