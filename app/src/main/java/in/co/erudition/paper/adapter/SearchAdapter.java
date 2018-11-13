package in.co.erudition.paper.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import in.co.erudition.paper.R;
import in.co.erudition.paper.activity.SingleAnswerActivity;
import in.co.erudition.paper.data.model.QuesAnsSearch;
import in.co.erudition.paper.data.model.SearchResult;
import in.co.erudition.paper.util.PreferenceUtils;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {
    private List<SearchResult> searchResults;
    private ArrayList<QuesAnsSearch> data;
    private Context mContext;
    private Intent mIntent;
    private String searchTime;
    private StringBuilder str;
    private SearchAdapter.SearchItemListener mItemListener;

    public SearchAdapter(Context context, ArrayList<SearchResult> searchResult, Intent intent, SearchAdapter.SearchItemListener itemListener) {
        mContext = context;
        searchResults = searchResult;
        mIntent = intent;
        mItemListener = itemListener;
        data = new ArrayList<QuesAnsSearch>();

        /**
         * https://www.erudition.co.in/resources/public/css/prism.css
         * https://www.erudition.co.in/resources/public/js/prism.js
         */

        str = new StringBuilder("<html>");

        str.append("<head>\n    <link rel=\"stylesheet\" href=\"prism.css\">\n");
        str.append("    <style>\n    img {height: auto!important;  width: 100%!important; overflow-x: auto!important; overflow-y: hidden!important;\n");
        str.append("            border: none!important;\n max-width: fit-content;\n vertical-align: middle;\n");
        str.append("        }\n  table { width: 100%!important; background-color: transparent; border-spacing: 0; border-collapse: collapse;}\n");
        str.append("    </style>\n <script src=\"prism.js\"></script>\n</head>");
        str.append("<body>\n");

        Log.d("QuestionAdapter", "Total Ques:" + String.valueOf(searchResults.size()));
    }

    @NonNull
    @Override
    public SearchAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View mCardView = inflater.inflate(R.layout.card_question_new, parent, false);

        // Return a new holder instance
        SearchAdapter.ViewHolder viewHolder = new SearchAdapter.ViewHolder(mCardView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull SearchAdapter.ViewHolder holder, int position) {
        SearchResult ques = searchResults.get(holder.getAdapterPosition());
        Log.d("Adapter pos", String.valueOf(holder.getAdapterPosition()));
        final WebView q_tv = holder.ques_tv;
        TextView m_tv = holder.marks_tv;
        TextView q_no_tv = holder.ques_no_tv;
        TextView r_tv = holder.repeated_tv;
        View nav_space = holder.spacer;

        // Set item views based on your views and data model
        try {
            //q_tv.loadData(getHtmlData(ques.getQuestion()), "text/html", null);
            q_tv.loadDataWithBaseURL("file:///android_asset/", getHtmlData(ques.getQuestion()), "text/html", "UTF-8", null);
            m_tv.setText(ques.getMarks());
//            q_no_tv.setText(ques.getQuestionNo() + ".");
//            r_tv.setText(ques.getRepeat());

            if (nav_space.getVisibility() == View.VISIBLE || holder.result_count.getVisibility()==View.VISIBLE) {
                nav_space.setVisibility(View.GONE);
                holder.result_count.setVisibility(View.GONE);
            }

            if (getItemCount() - 1 == holder.getAdapterPosition()) {
                Log.d("Nav Spacer", "inflated");
                nav_space.setVisibility(View.VISIBLE);    //TODO: Turn it on when there is no ads
            }

            if (holder.getAdapterPosition()==0){
                holder.result_count.setText(getSearchTime());
                holder.result_count.setVisibility(View.VISIBLE);
            }

        } catch (NullPointerException | IllegalArgumentException | IndexOutOfBoundsException e) {
            Log.e("Exception", e.toString());
        }
    }

    private String getHtmlData(String data) {
        return str.toString() + data + "</body>\n</html>";
    }

    @Override
    public int getItemCount() {
        return searchResults.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public WebView ques_tv;
        public TextView marks_tv;
        public TextView repeated_tv;
        public TextView ques_no_tv;
        public TextView result_count;
        public View spacer;

        @SuppressLint("SetJavaScriptEnabled")
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            ques_tv = (WebView) itemView.findViewById(R.id.question_tv);
            marks_tv = (TextView) itemView.findViewById(R.id.marks_tv);
            ques_no_tv = (TextView) itemView.findViewById(R.id.ques_num);
            repeated_tv = (TextView) itemView.findViewById(R.id.ques_repeat);
            spacer = (View) itemView.findViewById(R.id.nav_spacer_ad);
            result_count = (TextView) itemView.findViewById(R.id.result_count);

            //Optimizations
            ques_tv.getSettings().setJavaScriptEnabled(PreferenceUtils.getJS());
            ques_tv.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
            ques_tv.getSettings().setAppCacheEnabled(false);
            ques_tv.setLayerType(View.LAYER_TYPE_HARDWARE, null);
            ques_tv.setOnLongClickListener(v -> true);
            ques_tv.setLongClickable(false);

            //Setting click events on both webview and the card
            ques_tv.setOnTouchListener(new View.OnTouchListener() {

                private int mTouchSlop = ViewConfiguration.get(mContext).getScaledTouchSlop();
                private float startX;
                private float startY;

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            startX = event.getX();
                            startY = event.getY();
                            break;
                        case MotionEvent.ACTION_UP:
                            float endX = event.getX();
                            float endY = event.getY();
                            if (isAClick(startX, endX, startY, endY)) {
                                itemView.callOnClick();     // WE HAVE A CLICK!!
                                Log.d("WebView", "Click");
                            }
                            break;
                    }
                    Log.d("itemView", "Touched!");
                    //Let the other functions work as intended
                    return false;
                }

                private boolean isAClick(float startX, float endX, float startY, float endY) {
                    float differenceX = Math.abs(startX - endX);
                    float differenceY = Math.abs(startY - endY);
                    return !(differenceX > mTouchSlop || differenceY > mTouchSlop);
                }
            });

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Log.d("Adapter Position:", String.valueOf(getAdapterPosition()));
            SearchResult paperQuestion = getQues(getAdapterPosition());
            Log.d("Ques Code:", String.valueOf(paperQuestion.getCode()));

            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                //Now call the function to parcel the whole data and pass it along.
                parcelData(position);
                Intent intent = new Intent(mContext, SingleAnswerActivity.class);
                intent.putExtra("Search_ADAPTER.parcelData", data);
                intent.putExtra("Search_ADAPTER.position", position);
                intent.putExtras(mIntent);
                mContext.startActivity(intent);
            }
        }
    }

    private void parcelData(int pos) {
        try {
            for (int i = 0; i < searchResults.size(); i++) {
                SearchResult searchResult = getQues(i);
                data.add(new QuesAnsSearch(searchResult));
            }
            //Single mode
//            data.add(new QuesAnsSearch(getQues(pos)));

        } catch (NullPointerException | IllegalArgumentException | IndexOutOfBoundsException e) {
            Log.e("Exception", e.toString());
        }
    }

    private SearchResult getQues(int adapterPosition) {
        return searchResults.get(adapterPosition);
    }

    public void updateResults(List<SearchResult> searchResultList) {
        searchResults = searchResultList;
        notifyDataSetChanged();
    }


    public String getSearchTime() {
        return searchTime;
    }

    public void setSearchTime(String searchTime) {
        this.searchTime = searchTime;
    }

    public interface SearchItemListener {
        void onQuesClick(String id);
    }

}

