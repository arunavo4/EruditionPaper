package in.co.erudition.paper.adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import com.google.android.material.appbar.CollapsingToolbarLayout;

import androidx.annotation.Keep;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.os.Build;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.List;

import in.co.erudition.paper.R;
import in.co.erudition.paper.activity.SelectionActivity;
import in.co.erudition.paper.data.model.BoardCourse;
import in.co.erudition.paper.data.model.BoardSession;
import in.co.erudition.paper.data.model.BoardSubject;
import in.co.erudition.paper.data.model.UniversityCourse;
import in.co.erudition.paper.util.GlideApp;

/**
 * Created by Arunavo Ray on 01-04-2018.
 */
@Keep
public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.ViewHolder> {

    private UniversityCourse universityCourses;
    private List<BoardCourse> boardCourses;
    private List<BoardSession> boardSessions;
    private List<BoardSubject> boardSubjects;
    private String paramsStore[];

    private Context mContext;
    private CourseAdapter.CourseItemListener mItemListener;
    private TextView mChooseTV;
    private TextView mTitleTV;
    private CollapsingToolbarLayout mTopTV;
    private String[] NameStr;
    private String[] NameStrFull;
    private int selector;
    private int state_selector;

    private Intent intent;

    public CourseAdapter(Context context, UniversityCourse universityCourseList, String array[], TextView textView, TextView title, CollapsingToolbarLayout collapsingToolbarLayout, CourseAdapter.CourseItemListener itemListener) {
        mContext = context;
        universityCourses = universityCourseList;
        mItemListener = itemListener;
        paramsStore = array;
        mChooseTV = textView;
        mTitleTV = title;
        mTopTV = collapsingToolbarLayout;

        boardCourses = new ArrayList<BoardCourse>();
        boardSessions = new ArrayList<BoardSession>();
        boardSubjects = new ArrayList<BoardSubject>();

        NameStr = new String[4];
        NameStrFull = new String[4];
        selector = 0;
        state_selector = 0;
        intent = new Intent(mContext, SelectionActivity.class);
    }

    // Involves inflating a layout from XML and returning the holder
    @Override
    public CourseAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View mCardView = inflater.inflate(R.layout.course_card_new, parent, false);

        // Return a new holder instance
        CourseAdapter.ViewHolder viewHolder = new CourseAdapter.ViewHolder(mCardView);
        return viewHolder;
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(CourseAdapter.ViewHolder holder, int position) {
        ImageView mImgView = holder.mImageView;
        TextView mCodeTV = holder.mCodeTV;
        TextView mCodeFullTV = holder.mCodeFullTV;
        ImageView mBanner = holder.banner;
        View nav_space = holder.spacer;
        View ad_space = holder.ad_spacer;

        String mImgStr = "#";
        String mCodeStr = "Code 001";
        String mCodeFullStr = "Full Subject Name";
        String state = "Active";

        try {
            switch (selector) {
                case 0:
                    BoardCourse course = boardCourses.get(holder.getAdapterPosition());
                    mImgStr = course.getLogo();
                    mCodeStr = course.getName();
                    mCodeFullStr = course.getFullName();
                    state = course.getState();
                    break;

                case 1:
                    BoardSession session = boardSessions.get(holder.getAdapterPosition());
                    mImgStr = session.getLogo();
                    mCodeStr = session.getFullName();
                    mCodeFullStr = session.getFullName();
                    state = session.getState();
                    break;

                case 2:
                    BoardSubject subject = boardSubjects.get(holder.getAdapterPosition());
                    mImgStr = subject.getLogo();
                    mCodeStr = subject.getName();
                    mCodeFullStr = subject.getFullName();
                    state = subject.getState();
                    break;
                default:
                    selector = 0;
            }

            if (!mImgStr.contentEquals("#")) {
                GlideApp
                        .with(mContext)
                        .load(mImgStr)
                        .apply(RequestOptions.placeholderOf(R.drawable.ic_foreground))
                        .thumbnail(0.1f)
                        .into(mImgView);
//                mImgView.setForeground(ContextCompat.getDrawable(mContext,R.drawable.ic_foreground));
            }
            mCodeTV.setText(mCodeStr);

            if (selector == 1) {
                mCodeFullTV.setText(mTitleTV.getText());
            } else {
                mCodeFullTV.setText(mCodeFullStr);
            }

            if (nav_space.getVisibility() == View.VISIBLE || ad_space.getVisibility() == View.VISIBLE) {
//                nav_space.setVisibility(View.GONE);
                ad_space.setVisibility(View.GONE);
            }

            if (getItemCount() - 1 == holder.getAdapterPosition()) {
                Log.d("Nav Spacer", "inflated");
//                nav_space.setVisibility(View.VISIBLE);    //TODO: Turn it on when there is no ads
                ad_space.setVisibility(View.VISIBLE);
            }

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
                mBanner.setVisibility(View.INVISIBLE); }
        } catch (NullPointerException | IllegalArgumentException | IndexOutOfBoundsException e) {
            Log.e("Exception", e.toString());
        }
    }

    @Override
    public int getItemCount() {

        switch (selector) {
            case 0:
                return boardCourses.size();

            case 1:
                return boardSessions.size();

            case 2:
                return boardSubjects.size();
        }
        return 0;
    }

    private void setParams(String Code) {
        switch (selector) {
            case 0:
                paramsStore[1] = Code;
                break;
            case 1:
                paramsStore[2] = Code;
                break;
            case 2:
                paramsStore[3] = Code;
                break;
        }
    }

    private void updateData() {
        switch (selector) {
            case 0:
                boardCourses = universityCourses.getBoardCourse();
                break;
            case 1:
                boardSessions = universityCourses.getBoardCourse().get(0).getBoardSession();
                break;
            case 2:
                boardSubjects = universityCourses.getBoardCourse().get(0).getBoardSession().get(0).getBoardSubject();
                break;
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ImageView mImageView;
        public TextView mCodeTV;
        public TextView mCodeFullTV;
        public ImageView banner;
        public View spacer;
        public View ad_spacer;

        public ViewHolder(View itemView) {
            super(itemView);
            mImageView = (ImageView) itemView.findViewById(R.id.course_thumbnail_img_view);
            mCodeTV = (TextView) itemView.findViewById(R.id.tv_code);
            mCodeFullTV = (TextView) itemView.findViewById(R.id.tv_code_full);
            banner = (ImageView) itemView.findViewById(R.id.coming_soon_overlay);
            spacer = (View) itemView.findViewById(R.id.nav_spacer_2);
            ad_spacer = (View) itemView.findViewById(R.id.nav_spacer_ad);

            itemView.setOnClickListener(this);
        }

        // Easy access to the context object in the recyclerview
        private Context getContext() {
            return mContext;
        }

        @Override
        public void onClick(View v) {
            Log.d("Adapter Position:", String.valueOf(getAdapterPosition()));
//            Log.d("UniversityId:", String.valueOf(mUniversity.getId()));
            String state = null;
            state_selector = 0;
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                switch (selector) {
                    case 0:
                        setParams(boardCourses.get(position).getCode());
                        selector += 1;
                        state = boardCourses.get(position).getState();
                        if (state!=null) {
                            if (state.contentEquals("Not Active")) {
                                state_selector = 1;
                            }
                        }
                        NameStr[selector] = boardCourses.get(position).getName();
                        NameStrFull[selector] = boardCourses.get(position).getFullName();
                        mItemListener.onUniversityClick("0");
                        break;

                    case 1:
                        setParams(boardSessions.get(position).getCode());
                        selector += 1;
                        state = boardSessions.get(position).getState();
                        if (state!=null) {
                            if (state.contentEquals("Not Active")) {
                                state_selector = 1;
                            }
                        }
                        NameStr[selector] = boardSessions.get(position).getFullName();
                        NameStrFull[selector] = boardSessions.get(position).getFullName();
                        mItemListener.onUniversityClick("0");
                        break;

                    case 2:
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
                setTextView(selector);
            }
        }
    }

    public void updateUniversitiesFull(UniversityCourse universityCourseList) {
        universityCourses = universityCourseList;
        updateData();
        NameStr[0] = universityCourses.getName();
        NameStrFull[0] = universityCourses.getFullName();
        notifyDataSetChanged();
    }

    public int getSelector() {
        return selector;
    }

    public int getState(){
        return state_selector;
    }

    public void setSelectorOnBackPressed() {
        selector -= 1;
        setTextView(selector);
        notifyDataSetChanged();
    }

    private void setTextView(int selector) {
        String StaticStr = "Choose your ";
        String Choice[] = {"Course", "Semester", "Subject"};
        String Str = StaticStr + Choice[selector];
        mChooseTV.setText(Str);
        mTopTV.setTitle(NameStr[selector]);
        mTitleTV.setText(NameStrFull[selector]);
    }

    public interface CourseItemListener {
        void onUniversityClick(String id);
    }

    private void showDialogComingSoon() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_coming_soon, null);

        Button btn_notify = (Button) view.findViewById(R.id.btn_notify);

        final Dialog dialog = new Dialog(mContext, android.R.style.Theme_DeviceDefault_Light_NoActionBar_TranslucentDecor);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(view);
        dialog.show();

        btn_notify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //retry and close dialogue
                if (dialog.isShowing()) {
                    dialog.cancel();
//                    onBackPressed();
                }
            }
        });
    }
}
