package in.co.erudition.paper.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import in.co.erudition.paper.R;
import in.co.erudition.paper.activitiy.QuestionActivity;
import in.co.erudition.paper.data.model.Chapter;
import in.co.erudition.paper.data.model.Paper;
import in.co.erudition.paper.data.model.University;
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

    public PaperAdapter(Context context, List<Year> papers, List<Chapter> chapters, int s, Intent intent, PaperAdapter.PaperItemListener itemListener){
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
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View mCardView = inflater.inflate(R.layout.paper_card, parent, false);

        // Return a new holder instance
        PaperAdapter.ViewHolder viewHolder = new PaperAdapter.ViewHolder(mCardView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        TextView mSNameTV = holder.mSubjectNameTV;
        TextView mSYearTV = holder.mSubjectYearTV;
        View nav_space = holder.spacer;

        // Set item views based on your views and data model
        try {
            if (select==0){
                Chapter paper = mChapters.get(holder.getAdapterPosition());
                mSNameTV.setText(paper.getChapterFullName());
                mSYearTV.setText(paper.getChapterName());

            }else if (select==1){
                Year paper = mYears.get(holder.getAdapterPosition());
                mSNameTV.setText(paper.getSubjectFullName());
                mSYearTV.setText(paper.getYear());
            }

            if (nav_space.getVisibility() == View.VISIBLE){
                nav_space.setVisibility(View.GONE);
            }

            if (getItemCount()-1 == holder.getAdapterPosition()){
                Log.d("Nav Spacer","inflated");
                nav_space.setVisibility(View.VISIBLE);
            }
        }
        catch (NullPointerException | IllegalArgumentException | IndexOutOfBoundsException e)
        {   Log.e("Exception",e.toString()); }
    }

    @Override
    public int getItemCount() {
        if (select==0){
            return mChapters.size();
        }else if (select==1){
            return mYears.size();
        }
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView mSubjectNameTV;
        public TextView mSubjectYearTV;
        public View spacer;

        public ViewHolder(View itemView) {
            super(itemView);
            mSubjectNameTV = (TextView) itemView.findViewById(R.id.tv_subject_name);
            mSubjectYearTV = (TextView) itemView.findViewById(R.id.tv_year);
            spacer = (View) itemView.findViewById(R.id.nav_spacer);

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
            if(position != RecyclerView.NO_POSITION) {
                Intent intent_ques = new Intent(mContext, QuestionActivity.class);
                intent_ques.putExtras(mIntent);

                if (select==0){
                    Chapter chap = getChapter(getAdapterPosition());
                    intent_ques.putExtra("PaperActivity.EXTRA_Year",chap.getYear());
                    intent_ques.putExtra("PaperActivity.EXTRA_Full_Name",chap.getChapterFullName());
                    intent_ques.putExtra("PaperActivity.EXTRA_Name",chap.getChapterName());
                    intent_ques.putExtra("PaperActivity.EXTRA_Paper_Code",chap.getCode());
                }
                else if (select==1){
                    Year year = getYear(getAdapterPosition());
                    intent_ques.putExtra("PaperActivity.EXTRA_Year",year.getYear());
                    intent_ques.putExtra("PaperActivity.EXTRA_Full_Name",year.getSubjectFullName());
                    intent_ques.putExtra("PaperActivity.EXTRA_Name",year.getSubjectName());
                    intent_ques.putExtra("PaperActivity.EXTRA_Paper_Code",year.getCode());
                }
                mContext.startActivity(intent_ques);
            }
        }
    }

    public void updateYears(List<Year> years,int s) {
        mYears = years;
        select = s;
        notifyDataSetChanged();
    }

    public void updateChapters(List<Chapter> chapters,int s){
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
