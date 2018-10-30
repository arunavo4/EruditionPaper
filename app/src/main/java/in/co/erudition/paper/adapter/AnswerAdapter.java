package in.co.erudition.paper.adapter;

import android.annotation.SuppressLint;
import android.content.Context;

import androidx.annotation.Keep;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.List;

import in.co.erudition.paper.R;
import in.co.erudition.paper.data.model.Paper;
import in.co.erudition.paper.data.model.PaperGroup;
import in.co.erudition.paper.data.model.PaperQuestion;
import in.co.erudition.paper.data.model.QuestionAnswer;

/**
 * Created by Arunavo Ray on 13-06-2018.
 */
@Keep
public class AnswerAdapter extends RecyclerView.Adapter<AnswerAdapter.ViewHolder> {

    private List<QuestionAnswer> data;
    private StringBuilder str;
    private Context mContext;
    private Toolbar mToolbar;
    private GroupAdapter.GroupItemListener mItemListener;


    public AnswerAdapter(Context context, List<QuestionAnswer> parcelData, Toolbar toolbar, GroupAdapter.GroupItemListener itemListener) {
        mContext = context;
        data = parcelData;
        mItemListener = itemListener;
        mToolbar = toolbar;
        /**
         * https://www.erudition.co.in/resources/public/css/prism.css
         * https://www.erudition.co.in/resources/public/js/prism.js
         */

        str = new StringBuilder("<html>\n");

        str.append("<head>\n    <link rel=\"stylesheet\" href=\"prism.css\">\n");
        str.append("    <style>\n    img {height: auto!important;  width: 100%!important; overflow-x: auto!important; overflow-y: hidden!important;\n");
        str.append("            border: none!important;\n max-width: fit-content;\n vertical-align: middle;\n");
        str.append("        }\n  table { width: 100%!important; background-color: transparent; border-spacing: 0; border-collapse: collapse;}\n");
        str.append("    </style>\n <script src=\"prism.js\"></script>\n</head>");
        str.append("<body>\n");
    }

    @Override
    public AnswerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View mCardView = inflater.inflate(R.layout.answer_rv, parent, false);

        // Return a new holder instance
        AnswerAdapter.ViewHolder viewHolder = new AnswerAdapter.ViewHolder(mCardView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(AnswerAdapter.ViewHolder holder, int position) {
        QuestionAnswer questionAnswer = data.get(holder.getAdapterPosition());
        WebView question_tv = holder.ques_tv;
        WebView answer_tv = holder.ans_tv;
        TextView mark_tv = holder.marks_tv;
        TextView ques_num_tv = holder.ques_no_tv;

        try {
            mToolbar.setTitle(questionAnswer.getGroupName());
            //q_tv.loadData(getHtmlData(ques.getQuestion()), "text/html", null);
            question_tv.loadDataWithBaseURL("file:///android_asset/", getHtmlData(questionAnswer.getQuestion()), "text/html", "UTF-8", null);
            answer_tv.loadDataWithBaseURL("file:///android_asset/", getHtmlData(questionAnswer.getAnswer()), "text/html", "UTF-8", null);

            mark_tv.setText(questionAnswer.getMarks());
            ques_num_tv.setText(questionAnswer.getQuestionNo() + ".");

            AdRequest adRequest = new AdRequest.Builder().build();
            holder.adView.loadAd(adRequest);

        } catch (NullPointerException | IllegalArgumentException | IndexOutOfBoundsException e) {
            Log.e("Exception", e.toString());
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    private String getHtmlData(String data) {
        return str.toString() + data + "</body>\n</html>";
    }

    @SuppressLint("SetJavaScriptEnabled")
    public class ViewHolder extends RecyclerView.ViewHolder {
        public WebView ques_tv;
        public WebView ans_tv;
        public TextView marks_tv;
        public TextView ques_no_tv;
        public AdView adView;

        public ViewHolder(View itemView) {
            super(itemView);

            ques_tv = (WebView) itemView.findViewById(R.id.question_tv);
            ans_tv = (WebView) itemView.findViewById(R.id.answer_tv);

            marks_tv = (TextView) itemView.findViewById(R.id.marks_tv);
            ques_no_tv = (TextView) itemView.findViewById(R.id.ques_num);

            //Load Ads
            adView = (AdView) itemView.findViewById(R.id.adView);

            ques_tv.getSettings().setJavaScriptEnabled(true);
            ques_tv.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    return true;
                }
            });
            ques_tv.setLongClickable(false);

            ans_tv.getSettings().setJavaScriptEnabled(true);
            ans_tv.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    return true;
                }
            });
            ans_tv.setLongClickable(false);

            ques_tv.setOnTouchListener(new View.OnTouchListener() {

                private int mTouchSlop = 150;
                private float x1;
                private float x2;

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            x1 = event.getX();
                            break;
                        case MotionEvent.ACTION_UP:
                            x2 = event.getX();
                            float deltaX = x2 - x1;

                            if (Math.abs(deltaX) > mTouchSlop) {
                                // Left to Right swipe action
                                if (x2 > x1) {
                                    Log.d("WebView", "left to right Swipe");
                                    if (ques_tv.canScrollHorizontally(1)) {
                                        Log.d("Horizontal Scroll", "left to right");
                                        return true;
                                    }
                                }

                                // Right to left swipe action
                                else {
                                    Log.d("WebView", "Right to left Swipe");
                                    if (ques_tv.canScrollHorizontally(-1)) {
                                        Log.d("Horizontal Scroll", "Right to left");
                                        return true;
                                    }
                                }

                            } else {
                                // consider as something else - a screen tap for example
                            }
                            break;
                        case MotionEvent.ACTION_MOVE:
                            Log.d("ques_tv", "Moved");
                            if (ques_tv.canScrollHorizontally(-1)) {
                                Log.d("Horizontal Scroll", "left to right");
                                return true;
                            }
                    }
                    Log.d("ques_tv", "Touched!");
                    return false;
                }
            });
//
//            ans_tv.setOnTouchListener(new View.OnTouchListener() {
//                @Override
//                public boolean onTouch(View v, MotionEvent event) {
//                    Log.d("ans_tv","Touched!");
//                    return false;
//                }
//            });
        }
    }

    public void updateAnswers(Paper papers) {
        List<PaperGroup> paperGroups = papers.getPaperGroup();
        Log.d("GroupAdapter", "No of groups :" + String.valueOf(paperGroups.size()));

        try {
            //get the total data item count
            for (int i = 0; i < paperGroups.size(); i++) {
                PaperGroup paperGroup = paperGroups.get(i);
                List<PaperQuestion> paperQuestions = paperGroup.getPaperQuestion();
                for (int j = 0; j < paperQuestions.size(); j++) {
                    PaperQuestion paperQuestion = paperQuestions.get(j);
                    data.add(new QuestionAnswer(paperGroup, paperQuestion));
                }
            }

        } catch (NullPointerException | IllegalArgumentException | IndexOutOfBoundsException e) {
            Log.e("Exception", e.toString());
        }

        notifyDataSetChanged();
    }
}
