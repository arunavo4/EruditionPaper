package in.co.erudition.paper.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.List;

import in.co.erudition.paper.R;
import in.co.erudition.paper.activitiy.PaperActivity;
import in.co.erudition.paper.data.model.UniversityCourse;
import in.co.erudition.paper.data.model.UniversityFull;
import in.co.erudition.paper.data.model.UniversitySemester;
import in.co.erudition.paper.data.model.UniversityStream;
import in.co.erudition.paper.data.model.UniversitySubject;

/**
 * Created by Arunavo Ray on 01-04-2018.
 */

public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.ViewHolder>{

    private List<UniversityFull> mUniversities;
    private List<UniversityCourse> mUniversityCourse;
    private List<UniversityStream> mUniversityStream;
    private List<UniversitySemester> mUniversitySemester;
    private List<UniversitySubject> mUniversitySubject;

    private List<UniversityCourse> mUniversityCourseFiltered;
    private List<UniversityStream> mUniversityStreamFiltered;
    private List<UniversitySemester> mUniversitySemesterFiltered;
    private List<UniversitySubject> mUniversitySubjectFiltered;

    private Context mContext;
    private CourseAdapter.CourseItemListener mItemListener;
    private TextView mChooseTV;
    private CollapsingToolbarLayout mTopTV;
    private String[] NameStr;
    private int selector;

    private Intent intent;

    public CourseAdapter(Context context, List<UniversityFull> universities, TextView textView, CollapsingToolbarLayout collapsingToolbarLayout, CourseAdapter.CourseItemListener itemListener){
        mContext = context;
        mUniversities = universities;
        mItemListener = itemListener;
        mChooseTV = textView;
        mTopTV = collapsingToolbarLayout;
//        mImageViewEmpty = imageView;

        mUniversityCourse = new ArrayList<UniversityCourse>();
        mUniversityStream = new ArrayList<UniversityStream>();
        mUniversitySemester = new ArrayList<UniversitySemester>();
        mUniversitySubject = new ArrayList<UniversitySubject>();

        mUniversityCourseFiltered = new ArrayList<UniversityCourse>();
        mUniversityStreamFiltered = new ArrayList<UniversityStream>();
        mUniversitySemesterFiltered = new ArrayList<UniversitySemester>();
        mUniversitySubjectFiltered = new ArrayList<UniversitySubject>();

        NameStr = new String[4];
        selector = 0;
        intent = new Intent(mContext, PaperActivity.class);
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
        UniversityFull mUniversityFull = mUniversities.get(0);
        ImageView mImgView = holder.mImageView;
        TextView mCodeTV = holder.mCodeTV;
        TextView mCodeFullTV = holder.mCodeFullTV;
        View nav_space = holder.spacer;

        String mImgStr="#";
        String mCodeStr="Code 001";
        String mCodeFullStr="Full Subject Name";

        try{
            switch (selector){
                case 0: UniversityCourse universityCourse = mUniversityCourseFiltered.get(holder.getAdapterPosition());
                        mImgStr = universityCourse.getCourseImageM();
                        mCodeStr = universityCourse.getCourseName();
                        mCodeFullStr = universityCourse.getCourseFullName();
                        break;

                case 1: UniversityStream universityStream = mUniversityStreamFiltered.get(holder.getAdapterPosition());
                        mImgStr = universityStream.getStreamImageM();
                        mCodeStr = universityStream.getStreamName();
                        mCodeFullStr = universityStream.getStreamFullName();
                        break;

                case 2: UniversitySemester universitySemester = mUniversitySemesterFiltered.get(holder.getAdapterPosition());
                        mImgStr = universitySemester.getSemesterImageM();
                        mCodeStr = universitySemester.getSemesterName();
                        mCodeFullStr = universitySemester.getSemesterFullName();
                        break;

                case 3: UniversitySubject universitySubject = mUniversitySubjectFiltered.get(holder.getAdapterPosition());
                        mImgStr = universitySubject.getSubjectImageM();
                        mCodeStr = universitySubject.getSubjectName();
                        mCodeFullStr = universitySubject.getSubjectFullName();
                        break;
                default: selector=0;
            }

            if(!mImgStr.contentEquals("#")) {
                Glide
                        .with(mContext)
                        .load(mImgStr)
                        .apply(RequestOptions.placeholderOf(R.drawable.ic_bg_layer))
                        .thumbnail(0.1f)
                        .into(mImgView);
//                mImgView.setForeground(ContextCompat.getDrawable(mContext,R.drawable.ic_foreground));
            }
            mCodeTV.setText(mCodeStr);

            if (selector == 2){
                mCodeFullTV.setText(mUniversityStreamFiltered.get(holder.getAdapterPosition()).getStreamFullName());
            }else{
                mCodeFullTV.setText(mCodeFullStr);
            }

            if (nav_space.getVisibility() == View.VISIBLE){
                nav_space.setVisibility(View.GONE);
            }

            if (getItemCount()-1 == holder.getAdapterPosition()){
                Log.d("Nav Spacer","inflated");
                nav_space.setVisibility(View.VISIBLE);
            }
        }
        catch (NullPointerException | IllegalArgumentException | IndexOutOfBoundsException e){
            Log.e("Exception",e.toString());}
    }

    @Override
    public int getItemCount() {

        switch (selector){
            case 0: return mUniversityCourseFiltered.size();

            case 1: return mUniversityStreamFiltered.size();

            case 2: return mUniversitySemesterFiltered.size();

            case 3: return mUniversitySubjectFiltered.size();
        }
        return mUniversities.size();
    }


    private void filter(int selector){
        int len;
        switch(selector){
            case 0: len = mUniversityCourse.size();
                    mUniversityCourseFiltered.clear();
                    for(int i=0;i<len;i++){
                        if(mUniversityCourse.get(i).getStatus().contentEquals("Active")){
                            mUniversityCourseFiltered.add(mUniversityCourse.get(i));
                        }
                    }
                    break;

            case 1: len = mUniversityStream.size();
                    mUniversityStreamFiltered.clear();
                    for(int i=0;i<len;i++){
                        if(mUniversityStream.get(i).getStatus().contentEquals("Active")){
                            mUniversityStreamFiltered.add(mUniversityStream.get(i));
                        }
                    }
                    break;

            case 2: len = mUniversitySemester.size();
                    mUniversitySemesterFiltered.clear();
                    for(int i=0;i<len;i++){
                        if(mUniversitySemester.get(i).getStatus().contentEquals("Active")){
                            mUniversitySemesterFiltered.add(mUniversitySemester.get(i));
                        }
                    }
                    break;

            case 3: len = mUniversitySubject.size();
                    mUniversitySubjectFiltered.clear();
                    for(int i=0;i<len;i++){
                        if(mUniversitySubject.get(i).getStatus().contentEquals("Active")){
                            mUniversitySubjectFiltered.add(mUniversitySubject.get(i));
                        }
                    }
                    break;
        }

    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ImageView mImageView;
        public TextView mCodeTV;
        public TextView mCodeFullTV;
        public View spacer;

        public ViewHolder(View itemView) {
            super(itemView);
            mImageView = (ImageView) itemView.findViewById(R.id.course_thumbnail_img_view);
            mCodeTV = (TextView) itemView.findViewById(R.id.tv_code);
            mCodeFullTV = (TextView) itemView.findViewById(R.id.tv_code_full);
            spacer = (View) itemView.findViewById(R.id.nav_spacer_2);

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

            int position = getAdapterPosition();
            if(position != RecyclerView.NO_POSITION) {
               switch (selector){
                   case 0:  mUniversityStream = mUniversityCourseFiltered.get(getAdapterPosition()).getUniversityStream();
                            selector+=1;
                            filter(selector);
                            NameStr[selector] = mUniversityCourseFiltered.get(position).getCourseFullName();
                            intent.putExtra("CourseActivity.EXTRA_Course_Key",mUniversityCourseFiltered.get(position).getKey());
                            notifyDataSetChanged();
                            break;

                   case 1:  mUniversitySemester = mUniversityStreamFiltered.get(getAdapterPosition()).getUniversitySemester();
                            selector+=1;
                            filter(selector);
                            NameStr[selector] = mUniversityStreamFiltered.get(position).getStreamFullName();
                            intent.putExtra("CourseActivity.EXTRA_Stream_Key",mUniversityStreamFiltered.get(position).getKey());
                            notifyDataSetChanged();
                            break;

                   case 2:  mUniversitySubject = mUniversitySemesterFiltered.get(getAdapterPosition()).getUniversitySubject();
                            selector+=1;
                            filter(selector);
                            NameStr[selector] = mUniversitySemesterFiltered.get(position).getSemesterFullName();
                            intent.putExtra("CourseActivity.EXTRA_Semester_Key",mUniversitySemesterFiltered.get(position).getKey());
                            notifyDataSetChanged();
                            break;

                   case 3:
                            intent.putExtra("CourseActivity.EXTRA_Subject_NAME",mUniversitySubjectFiltered.get(position).getSubjectName());
                            intent.putExtra("CourseActivity.EXTRA_Subject_FULL_NAME",mUniversitySubjectFiltered.get(position).getSubjectFullName());
                            intent.putExtra("CourseActivity.EXTRA_University_Key",mUniversities.get(0).getKey());
                            intent.putExtra("CourseActivity.EXTRA_Subject_Key",mUniversitySubjectFiltered.get(position).getKey());
                            intent.putExtra("FROM","Course");
                            mContext.startActivity(intent);
               }
               setTextView(selector);
            }
        }
    }

    public void updateUniversitiesFull(List<UniversityFull> Universities) {
        mUniversities = Universities;
        mUniversityCourse = mUniversities.get(0).getUniversityCourse();
        filter(selector);
        NameStr[0] = mUniversities.get(0).getUniversityFullName();
        notifyDataSetChanged();
    }

    public int getSelector(){
        return selector;
    }

    public void setSelectorOnBackPressed(){
        selector-=1;
        setTextView(selector);
        notifyDataSetChanged();
    }

    private void setTextView(int selector){
        String StaticStr = "Choose your ";
        String Choice[] = {"Course","Stream","Semester","Subject"};
        String Str = StaticStr + Choice[selector];
        mChooseTV.setText(Str);
        mTopTV.setTitle(NameStr[selector]);
    }

    //TODO: Before using these methods change to Filtered
    private UniversityFull getUniversityFull(int adapterPosition) {
        return mUniversities.get(adapterPosition);
    }

    private UniversityCourse getUniversityCourse(int adapterPosition){
        return mUniversityCourse.get(adapterPosition);
    }

    private UniversityStream getUniversityStream(int adapterPosition){
        return mUniversityStream.get(adapterPosition);
    }

    private UniversitySemester getUniversitySemester(int adapterPosition){
        return mUniversitySemester.get(adapterPosition);
    }

    private UniversitySubject getUniversitySubject(int adapterPosition){
        return mUniversitySubject.get(adapterPosition);
    }

    public interface CourseItemListener {
        void onUniversityClick(String id);
    }
}
