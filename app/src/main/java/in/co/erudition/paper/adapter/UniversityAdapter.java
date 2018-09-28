package in.co.erudition.paper.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

import in.co.erudition.paper.activity.CourseActivity;
import in.co.erudition.paper.R;
import in.co.erudition.paper.data.model.University;

/**
 * Created by Arunavo Ray on 31-03-2018.
 */

public class UniversityAdapter extends RecyclerView.Adapter<UniversityAdapter.ViewHolder> {

    private List<University> mUniversities;
    private Context mContext;
    private UniversityItemListener mItemListener;

    public UniversityAdapter(Context context,List<University> universities, UniversityItemListener itemListener){
        mContext = context;
        mUniversities = universities;
        mItemListener = itemListener;
    }

    // Involves inflating a layout from XML and returning the holder
    @Override
    public UniversityAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View mCardView = inflater.inflate(R.layout.university_card, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(mCardView);
        return viewHolder;
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        University mUniversity = mUniversities.get(holder.getAdapterPosition());
        ImageView mImgView = holder.mImageView;
        TextView mUNameTV = holder.mUniversityNameTV;
//        View nav_space = holder.spacer;

        try{
            Log.d("UnivesityImage",mUniversity.getLogo());
            Log.d("UnivesityName",mUniversity.getName());
        }
        catch (Exception e)
        {
            Log.e("Exception",e.toString());
        }

        // Set item views based on your views and data model
        try {
            if(!mUniversity.getLogo().contentEquals("#")) {
                Glide
                        .with(mContext)
                        .load(mUniversity.getLogo())
                        .apply(RequestOptions.placeholderOf(R.drawable.bg_white))
                        .thumbnail(0.1f)
                        .into(mImgView);
            }
            mUNameTV.setText(mUniversity.getName());

//            if (getItemCount()-1 == holder.getAdapterPosition()){
//                Log.d("Nav Spacer","inflated");
//                nav_space.setVisibility(View.VISIBLE);
//            }
        }
        catch (NullPointerException | IllegalArgumentException | IndexOutOfBoundsException e)
        {   Log.e("Exception",e.toString()); }
    }

    @Override
    public int getItemCount() {
        return mUniversities.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ImageView mImageView;
        public TextView mUniversityNameTV;
//        public View spacer;

        public ViewHolder(View itemView) {
            super(itemView);
            mImageView = (ImageView) itemView.findViewById(R.id.university_logo);
            mUniversityNameTV = (TextView) itemView.findViewById(R.id.tv_university_name);
//            spacer = (View) itemView.findViewById(R.id.nav_spacer_1);

            itemView.setOnClickListener(this);
        }

        // Easy access to the context object in the recyclerview
        private Context getContext() {
            return mContext;
        }

        @Override
        public void onClick(View v) {
            Log.d("Adapter Position:", String.valueOf(getAdapterPosition()));
            University mUniversity = getUniversity(getAdapterPosition());
            Log.d("UniversityId:", String.valueOf(mUniversity.getId()));

            int position = getAdapterPosition();
            if(position != RecyclerView.NO_POSITION) {
                Intent intent = new Intent(mContext, CourseActivity.class);
                intent.putExtra("UniversityActivity.EXTRA_PHOTO_URL", mUniversity.getLogo());
                intent.putExtra("UniversityActivity.EXTRA_University_NAME",mUniversity.getName());
                intent.putExtra("UniversityActivity.EXTRA_University_FULL_NAME",mUniversity.getFullName());
                intent.putExtra("UniversityActivity.EXTRA_BoardCode",mUniversity.getCode());
                mContext.startActivity(intent);
            }

            //Toast.makeText(mContext, "Clicked", Toast.LENGTH_SHORT).show();
        }
    }

    public void updateUniversities(List<University> Universities) {
        mUniversities = Universities;
        notifyDataSetChanged();
    }

    private University getUniversity(int adapterPosition) {
        return mUniversities.get(adapterPosition);
    }

    public interface UniversityItemListener {
        void onUniversityClick(String id);
    }

}
