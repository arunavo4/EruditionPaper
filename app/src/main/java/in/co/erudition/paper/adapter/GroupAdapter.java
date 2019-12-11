package in.co.erudition.paper.adapter;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.Keep;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import in.co.erudition.paper.R;
import in.co.erudition.paper.data.model.Paper;
import in.co.erudition.paper.data.model.PaperGroup;

/**
 * Created by Arunavo Ray on 09-06-2018.
 */
@Keep
public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.ViewHolder> {

    private QuestionAdapter mAdapter[];
    private List<PaperGroup> paperGroups;
    private Context mContext;
    private Intent mIntent;
    private GroupAdapter.GroupItemListener mItemListener;
    private RecyclerView.RecycledViewPool viewPool;

    public GroupAdapter(Context context, List<PaperGroup> mPapers, Intent intent, GroupAdapter.GroupItemListener itemListener) {
        mContext = context;
        paperGroups = mPapers;
        mIntent = intent;
        mItemListener = itemListener;

        viewPool = new RecyclerView.RecycledViewPool();     //Optimize the nested recycler view
    }

    @Override
    public GroupAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View mCardView = inflater.inflate(R.layout.recycler_view_ques_new, parent, false);

        // Return a new holder instance
        GroupAdapter.ViewHolder viewHolder = new GroupAdapter.ViewHolder(mCardView);

        /**
         * RecyclerView.setRecycledViewPool(RecycledViewPool) allows you to set a custom view pool
         * to your recyclerView.
         * So now as all the inner RecyclerViews have the same view pool, it can use each other’s scraped views.
         */
        viewHolder.recyclerView.setRecycledViewPool(viewPool);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        PaperGroup group = paperGroups.get(holder.getAdapterPosition());
        TextView group_tv = holder.mGroupTV;
        RecyclerView mRecyclerView = holder.recyclerView;
        View nav_space = holder.spacer;
        View ad_space = holder.ad_spacer;

        // Set item views based on your views and data model
        try {
            group_tv.setText(group.getGroupName());

            int pos = holder.getAdapterPosition();
            //initialize the recycler view              //group.getPaperQuestion()
            mAdapter[pos] = new QuestionAdapter(mContext, paperGroups, pos, mIntent, new QuestionAdapter.QuestionItemListener() {
                @Override
                public void onQuesClick(String id) {
                    Toast.makeText(mContext, "Post id is" + id, Toast.LENGTH_SHORT).show();
                }
            });

            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(mContext);
            mRecyclerView.setLayoutManager(layoutManager);
            mRecyclerView.setAdapter(mAdapter[pos]);
            mRecyclerView.setHasFixedSize(true);
            mRecyclerView.setItemAnimator(new DefaultItemAnimator());
            Log.d("GroupAdapter", "done adapter" + String.valueOf(holder.getAdapterPosition()));

            if (nav_space.getVisibility() == View.VISIBLE || ad_space.getVisibility() == View.VISIBLE) {
//                nav_space.setVisibility(View.GONE);
                ad_space.setVisibility(View.GONE);
            }

            if (getItemCount() - 1 == holder.getAdapterPosition()) {
                Log.d("Nav Spacer", "inflated");
//                nav_space.setVisibility(View.VISIBLE);      //TODO: Turn it on when there is no ads
                ad_space.setVisibility(View.VISIBLE);
            }

            //fixed content no need to update adapter
//            mAdapter[pos].updateQues(group.getPaperQuestion());
        } catch (NullPointerException | IllegalArgumentException | IndexOutOfBoundsException e) {
            Log.e("Exception", e.toString());
        }
    }

    @Override
    public int getItemCount() {
        return paperGroups.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView mGroupTV;
        public RecyclerView recyclerView;
        public ImageView button_info;
        public View ad_spacer;
        public View spacer;

        public ViewHolder(View itemView) {
            super(itemView);
            mGroupTV = (TextView) itemView.findViewById(R.id.group_tv);
            recyclerView = (RecyclerView) itemView.findViewById(R.id.recycler_view_questions);
            button_info = (ImageView) itemView.findViewById(R.id.btn_info);
            spacer = (View) itemView.findViewById(R.id.nav_spacer);
            ad_spacer = (View) itemView.findViewById(R.id.nav_spacer_ad);

            button_info.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Log.d("Adapter Position:", String.valueOf(getAdapterPosition()));
            PaperGroup paperGroup = getGroup(getAdapterPosition());
            Log.d("Group:", String.valueOf(paperGroup.getGroupName()));
            //TODO: Need to transfer intent data and start new activity
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                showDialogInfo(paperGroup);
            }

        }
    }

    public void updateGroups(Paper papers) {
        paperGroups = papers.getPaperGroup();
        Log.d("GroupAdapter", "No of groups :" + String.valueOf(paperGroups.size()));
        mAdapter = new QuestionAdapter[paperGroups.size()];
        notifyDataSetChanged();
    }

    private PaperGroup getGroup(int adapterPosition) {
        return paperGroups.get(adapterPosition);
    }

    public interface GroupItemListener {
        void onGroupClick(String id);
    }

    /**
     * Method to inflate the dialog and show it.
     */
    private void showDialogInfo(PaperGroup group) {

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_info, null);

        Animation view_anim = AnimationUtils.loadAnimation(mContext, R.anim.zoom_in);
        view.startAnimation(view_anim);

        //set all the details
        TextView g_tv = (TextView) view.findViewById(R.id.dialogue_group_tv);
        TextView g_desc_tv_1 = (TextView) view.findViewById(R.id.dialogue_group_desc_1);
        TextView g_desc_tv_2 = (TextView) view.findViewById(R.id.dialogue_group_desc_2);
        Button btn_contd = (Button) view.findViewById(R.id.btn_cont);

        //Set the views and make them visible if its not
        if (!group.getGroupName().contentEquals(" ")) {
            if (g_tv.getVisibility() == View.GONE)
                g_tv.setVisibility(View.VISIBLE);
            g_tv.setText(group.getGroupName());
        }
        if (!group.getGroupDesc1().contentEquals(" ")) {
            if (g_desc_tv_1.getVisibility() == View.GONE)
                g_desc_tv_1.setVisibility(View.VISIBLE);
            g_desc_tv_1.setText(group.getGroupDesc1());
        }
        if (!group.getGroupDesc2().contentEquals(" ")) {
            if (g_desc_tv_2.getVisibility() == View.GONE)
                g_desc_tv_2.setVisibility(View.VISIBLE);
            g_desc_tv_2.setText(group.getGroupDesc2());
        }

        builder.setView(view);
        final AlertDialog alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(true);
        alertDialog.show();

        btn_contd.setOnClickListener(v -> {
            //cancel the dialogue
            if (alertDialog.isShowing()) {
                alertDialog.cancel();
            }
        });
    }
}
