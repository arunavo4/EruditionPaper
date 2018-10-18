package in.co.erudition.paper.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import in.co.erudition.paper.R;
import in.co.erudition.paper.activity.QuestionActivity;
import in.co.erudition.paper.data.model.Chapter;
import in.co.erudition.paper.data.model.Year;

/**
 * Created by Arunavo Ray on 03-04-2018.
 */

public class PaperAdapter extends RecyclerView.Adapter<PaperAdapter.ViewHolder> {

    private List<Chapter> mChapters;
    private List<Year> mYears;
    private int select;
    private Context mContext;
    private Intent mIntent;
    private PaperAdapter.PaperItemListener mItemListener;

    public PaperAdapter(Context context, List<Year> papers, List<Chapter> chapters, int s, Intent intent, PaperAdapter.PaperItemListener itemListener) {
        mContext = context;
        mChapters = chapters;
        select = s;
        mYears = papers;
        mIntent = intent;
        mItemListener = itemListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        PaperAdapter.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        switch (select) {
            case 0:
                View mCardView1 = inflater.inflate(R.layout.paper_card_new_chap, parent, false);
                viewHolder = new PaperAdapter.ViewHolder(mCardView1);
                break;
            default:
                View mCardView2 = inflater.inflate(R.layout.paper_card, parent, false);
                viewHolder = new PaperAdapter.ViewHolder(mCardView2);
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        TextView mPNameTV = holder.mNameTV;
        TextView mPNumTV = holder.mYearTV;
        View nav_space = holder.spacer;

        // Set item views based on your views and data model
        try {
            if (select == 0) {
                Chapter paper = mChapters.get(holder.getAdapterPosition());
                mPNameTV.setText(paper.getChapterFullName());
                mPNumTV.setText(paper.getChapterName());

            } else if (select == 1) {
                Year paper = mYears.get(holder.getAdapterPosition());
                String str;
                if (paper.getSolved().contentEquals("Active"))
                    str = "Solved";
                else
                    str = "Un-Solved";
                mPNameTV.setText(str);
                mPNumTV.setText(paper.getYear());
                holder.mCountTv.setText(paper.getView());
            }

            if (nav_space.getVisibility() == View.VISIBLE) {
                nav_space.setVisibility(View.GONE);
            }

            if (getItemCount() - 1 == holder.getAdapterPosition()) {
                Log.d("Nav Spacer", "inflated");
                nav_space.setVisibility(View.VISIBLE);
            }
        } catch (NullPointerException | IllegalArgumentException | IndexOutOfBoundsException e) {
            Log.e("Exception", e.toString());
        }
    }

    @Override
    public int getItemCount() {
        if (select == 0) {
            return mChapters.size();
        } else if (select == 1) {
            return mYears.size();
        }
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView mNameTV;
        public TextView mYearTV;
        public TextView mCountTv;
        public View spacer;

        public ViewHolder(View itemView) {
            super(itemView);
            mNameTV = (TextView) itemView.findViewById(R.id.tv_name);
            mYearTV = (TextView) itemView.findViewById(R.id.tv_num);
            spacer = (View) itemView.findViewById(R.id.nav_spacer);

            if (select == 1)
                mCountTv = (TextView) itemView.findViewById(R.id.counter_tv);

            itemView.setOnClickListener(this);
        }

        // Easy access to the context object in the recyclerview
        private Context getContext() {
            return mContext;
        }

        @Override
        public void onClick(View v) {
            Log.d("Adapter Position:", String.valueOf(getAdapterPosition()));

            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                Intent intent_ques = new Intent(mContext, QuestionActivity.class);
                intent_ques.putExtras(mIntent);

                if (select == 0) {
                    Chapter chap = getChapter(getAdapterPosition());
                    intent_ques.putExtra("PaperActivity.EXTRA_Year", chap.getYear());
                    intent_ques.putExtra("PaperActivity.EXTRA_Full_Name", chap.getChapterFullName());
                    intent_ques.putExtra("PaperActivity.EXTRA_Name", chap.getChapterName());
                    intent_ques.putExtra("PaperActivity.EXTRA_Paper_Code", chap.getCode());
                } else if (select == 1) {
                    Year year = getYear(getAdapterPosition());
                    intent_ques.putExtra("PaperActivity.EXTRA_Year", year.getYear());
                    intent_ques.putExtra("PaperActivity.EXTRA_Full_Name", year.getSubjectFullName());
                    intent_ques.putExtra("PaperActivity.EXTRA_Name", year.getSubjectName());
                    intent_ques.putExtra("PaperActivity.EXTRA_Paper_Code", year.getCode());
                }
                intent_ques.putExtra("PaperActivity.EXTRA_Select", select);
                mContext.startActivity(intent_ques);
            }
        }
    }

    public void updateYears(List<Year> years, int s) {
        mYears = years;
        select = s;
        notifyDataSetChanged();
    }

    public void updateChapters(List<Chapter> chapters, int s) {
        mChapters = chapters;
        select = s;
        notifyDataSetChanged();
    }

    private Year getYear(int adapterPosition) {
        return mYears.get(adapterPosition);
    }

    private Chapter getChapter(int adapterPosition) {
        return mChapters.get(adapterPosition);
    }

    public interface PaperItemListener {
        void onPaperClick(String id);
    }

}
