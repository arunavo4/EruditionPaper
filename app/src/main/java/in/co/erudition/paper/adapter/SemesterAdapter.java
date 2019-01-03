package in.co.erudition.paper.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.request.RequestOptions;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import in.co.erudition.paper.R;
import in.co.erudition.paper.activity.SelectionActivity;
import in.co.erudition.paper.data.model.BoardSubject;
import in.co.erudition.paper.data.model.UniversityCourse;
import in.co.erudition.paper.util.GlideApp;
import in.co.erudition.paper.util.PreferenceUtils;

public class SemesterAdapter extends RecyclerView.Adapter<SemesterAdapter.ViewHolder> {
    private List<BoardSubject> boardSubjects;
    private SemesterAdapter.SubjectItemListener mItemListener;
    private Context mContext;
    private Intent intent;
    private String paramsStore[];

    public SemesterAdapter(Context mContext, List<BoardSubject> boardSubjects, SemesterAdapter.SubjectItemListener mItemListener) {
        this.boardSubjects = boardSubjects;
        this.mContext = mContext;
        this.mItemListener = mItemListener;

        paramsStore = new String[4];
        intent = new Intent(mContext, SelectionActivity.class);
    }

    @NonNull
    @Override
    public SemesterAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View mCardView = inflater.inflate(R.layout.card_semester_new, parent, false);

        // Return a new holder instance
        SemesterAdapter.ViewHolder viewHolder = new SemesterAdapter.ViewHolder(mCardView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull SemesterAdapter.ViewHolder holder, int position) {
        ImageView mImgView = holder.mImageView;
        TextView mCodeTV = holder.mCodeTV;
        TextView mCodeFullTV = holder.mCodeFullTV;
        ImageView mBanner = holder.banner;

        try {
            BoardSubject subject = boardSubjects.get(holder.getAdapterPosition());
            String mImgStr = subject.getLogo();
            String mCodeStr = subject.getName();
            String mCodeFullStr = subject.getFullName();
            String state = subject.getState();

            if (!mImgStr.contentEquals("#")) {
                GlideApp
                        .with(mContext)
                        .load(mImgStr)
                        .apply(RequestOptions.placeholderOf(R.drawable.ic_foreground_40_alpha))
                        .thumbnail(0.1f)
                        .into(mImgView);
    //                mImgView.setForeground(ContextCompat.getDrawable(mContext,R.drawable.ic_foreground));
            }
            mCodeTV.setText(mCodeStr);
            mCodeFullTV.setText(mCodeFullStr);

            //Turn Inactive image into GreyScale
            ColorMatrix colorMatrix = new ColorMatrix();
            ColorMatrixColorFilter colorFilter = new ColorMatrixColorFilter(colorMatrix);
            colorMatrix.setSaturation(0);
            ColorMatrixColorFilter bw_filter = new ColorMatrixColorFilter(colorMatrix);

            if (state!=null) {
                if (state.contentEquals("Not Active")) {
                    mImgView.setColorFilter(bw_filter);
                    mBanner.setVisibility(View.VISIBLE);
                }else {
                    mImgView.setColorFilter(colorFilter);
                    mBanner.setVisibility(View.INVISIBLE); }
            }else {
                mImgView.setColorFilter(colorFilter);
                mBanner.setVisibility(View.INVISIBLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return boardSubjects.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ImageView mImageView;
        public TextView mCodeTV;
        public TextView mCodeFullTV;
        public ImageView banner;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mImageView = (ImageView) itemView.findViewById(R.id.course_thumbnail_img_view);
            mCodeTV = (TextView) itemView.findViewById(R.id.tv_code);
            mCodeFullTV = (TextView) itemView.findViewById(R.id.tv_code_full);
            banner = (ImageView) itemView.findViewById(R.id.coming_soon_overlay);

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
            String state =  boardSubjects.get(position).getState();
            if (state!=null) {
                if (state.contentEquals("Not Active")) {
                    mItemListener.onSubjectClick(boardSubjects.get(position).getCode());
                } else {
                    intent.putExtra("CourseActivity.EXTRA_Subject_NAME", boardSubjects.get(position).getName());
                    intent.putExtra("CourseActivity.EXTRA_Subject_FULL_NAME", boardSubjects.get(position).getFullName());
                    setParams(boardSubjects.get(position).getCode());
                    intent.putExtra("CourseActivity.EXTRA_Subject_State", boardSubjects.get(position).getState());
                    intent.putExtra("CourseActivity.EXTRA_Subject_Syllabus",boardSubjects.get(position).getSyllabus());
                    intent.putExtra("CourseActivity.EXTRA_Subject_YearView",boardSubjects.get(position).getYearView());
                    intent.putExtra("CourseActivity.EXTRA_Subject_ChapView",boardSubjects.get(position).getChapterView());
                    intent.putExtra("CourseActivity.EXTRA_params", paramsStore);
                    intent.putExtra("FROM", "Course");
                    mContext.startActivity(intent);
                }
            }
        }
    }

    private void setParams(String code) {
        paramsStore = PreferenceUtils.getParams(code);
    }

    public void setBoardSubjects(List<BoardSubject> boardSubjects) {
        this.boardSubjects = boardSubjects;
        notifyDataSetChanged();
    }

    public interface SubjectItemListener {
        void onSubjectClick(String code);
    }
}