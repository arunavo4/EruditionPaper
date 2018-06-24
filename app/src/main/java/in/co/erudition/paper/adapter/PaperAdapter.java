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
import in.co.erudition.paper.data.model.Paper;
import in.co.erudition.paper.data.model.University;
import in.co.erudition.paper.data.model.Year;

/**
 * Created by Arunavo Ray on 03-04-2018.
 */

public class PaperAdapter extends RecyclerView.Adapter<PaperAdapter.ViewHolder> {

    private List<Year> mPapers;
    private Context mContext;
    private Intent mIntent;
    private PaperAdapter.PaperItemListener mItemListener;

    public PaperAdapter(Context context, List<Year> papers, Intent intent, PaperAdapter.PaperItemListener itemListener){
        mContext = context;
        mPapers = papers;
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
        Year paper = mPapers.get(holder.getAdapterPosition());
        TextView mSNameTV = holder.mSubjectNameTV;
        TextView mSYearTV = holder.mSubjectYearTV;
        View nav_space = holder.spacer;

        // Set item views based on your views and data model
        try {
            mSNameTV.setText(paper.getSubjectFullName());
            mSYearTV.setText(paper.getYear());

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
        return mPapers.size();
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
            Year year = getPaper(getAdapterPosition());
            Log.d("YearId:", String.valueOf(year.getId()));
            Log.d("PaperStatus:", year.getStatus());

            int position = getAdapterPosition();
            if(position != RecyclerView.NO_POSITION) {
                Intent intent_ques = new Intent(mContext, QuestionActivity.class);
                intent_ques.putExtras(mIntent);
                intent_ques.putExtra("PaperActivity.EXTRA_Year",year.getYear());
                intent_ques.putExtra("PaperActivity.EXTRA_Subject_Full",year.getSubjectFullName());
                intent_ques.putExtra("PaperActivity.EXTRA_University_Full",year.getUniversityName());
                intent_ques.putExtra("PaperActivity.EXTRA_Time",year.getTimeAllotted());
                intent_ques.putExtra("PaperActivity.EXTRA_Marks",year.getFullMarks());
                intent_ques.putExtra("PaperActivity.EXTRA_Img",year.getPaperImageM());
                intent_ques.putExtra("PaperActivity.EXTRA_Subject_Name",year.getSubjectName());
                mContext.startActivity(intent_ques);
            }
        }
    }

    public void updatePapers(List<Year> Papers) {
        mPapers = Papers;
        notifyDataSetChanged();
    }

    private Year getPaper(int adapterPosition) {
        return mPapers.get(adapterPosition);
    }

    public interface PaperItemListener {
        void onPaperClick(String id);
    }

}
