package in.co.erudition.paper.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.Keep;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.webkit.SafeBrowsingResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import in.co.erudition.paper.R;
import in.co.erudition.paper.activity.AnswerActivity;
import in.co.erudition.paper.activity.AnswerActivityNew;
import in.co.erudition.paper.data.model.PaperGroup;
import in.co.erudition.paper.data.model.PaperQuestion;
import in.co.erudition.paper.data.model.QuestionAnswer;
import in.co.erudition.paper.misc.NestedScrollWebView;
import in.co.erudition.paper.util.PreferenceUtils;

/**
 * Created by Arunavo Ray on 09-06-2018.
 */
@Keep
public class QuestionAdapter extends RecyclerView.Adapter<QuestionAdapter.ViewHolder> {
    private List<PaperGroup> paperGroups;
    private List<PaperQuestion> paperQuestions;
    private ArrayList<QuestionAnswer> data;
    private int group_pos;
    private Context mContext;
    private Intent mIntent;
    private StringBuilder str;
    private StringBuilder css_js;
    private QuestionAdapter.QuestionItemListener mItemListener;

    public QuestionAdapter(Context context, List<PaperGroup> mPapers, int pos, Intent intent, QuestionAdapter.QuestionItemListener itemListener) {
        mContext = context;
        paperQuestions = mPapers.get(pos).getPaperQuestion();
        group_pos = pos;
        mIntent = intent;
        paperGroups = mPapers;
        mItemListener = itemListener;
        data = new ArrayList<QuestionAnswer>();

        /*
         * https://www.erudition.co.in/resources/public/css/prism.css
         * https://www.erudition.co.in/resources/public/js/prism.js
         */
        str = new StringBuilder("<html>");
        str.append(PreferenceUtils.getCssHead());

        css_js = str;
        css_js.append(PreferenceUtils.getJsHead());
        css_js.append("</head><body>\n");

        str.append("</head><body>\n");

        Log.d("QuestionAdapter", "Total Ques:" + String.valueOf(paperQuestions.size()));
    }

    @Override
    public QuestionAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View mCardView = inflater.inflate(R.layout.card_question, parent, false);

        // Return a new holder instance
        QuestionAdapter.ViewHolder viewHolder = new QuestionAdapter.ViewHolder(mCardView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final QuestionAdapter.ViewHolder holder, int position) {
        PaperQuestion ques = paperQuestions.get(holder.getAdapterPosition());
        final NestedScrollWebView q_tv = holder.ques_tv;
        TextView m_tv = holder.marks_tv;
//        TextView q_no_tv = holder.ques_no_tv;
        TextView r_tv = holder.repeated_tv;

        // Set item views based on your views and data model
        try {
            //q_tv.loadData(getHtmlData(ques.getQuestion()), "text/html", null);
            if (ques.getJavascript().contentEquals("10") || ques.getJavascript().contentEquals("11")) {
                q_tv.loadDataWithBaseURL("file:///android_asset/", getHtmlDataWithJs(ques.getQuestion(),ques.getQuestionNo()), "text/html", "UTF-8", null);
            }else {
                q_tv.loadDataWithBaseURL("file:///android_asset/", getHtmlData(ques.getQuestion(),ques.getQuestionNo()), "text/html", "UTF-8", null);
            }
            m_tv.setText(ques.getMarks());

            //override javascript
            q_tv.getSettings().setJavaScriptEnabled(ques.getJavascript().contentEquals("10") || ques.getJavascript().contentEquals("11"));

//            q_no_tv.setText(ques.getQuestionNo() + ".");
            r_tv.setText(ques.getRepeat());


        } catch (NullPointerException | IllegalArgumentException | IndexOutOfBoundsException e) {
            Log.e("Exception", e.toString());
        }
    }

    private String getHtmlData(String data, String no) {
        data = data.substring(3);
        return str.toString() + "<p><strong>" + no + ". </strong>" + data + "</body>\n</html>";
    }
    private String getHtmlDataWithJs(String data, String no) {
        data = data.substring(3);
        return css_js.toString() + "<p><strong>" + no + ". </strong>" + data + "</body>\n</html>";
    }

    @Override
    public int getItemCount() {
        return paperQuestions.size();
    }

    @SuppressLint("SetJavaScriptEnabled")
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public NestedScrollWebView ques_tv;
        public TextView marks_tv;
        public TextView repeated_tv;
//        public TextView ques_no_tv;

        public ViewHolder(final View itemView) {
            super(itemView);

            ques_tv = (NestedScrollWebView) itemView.findViewById(R.id.question_tv);
            marks_tv = (TextView) itemView.findViewById(R.id.marks_tv);
//            ques_no_tv = (TextView) itemView.findViewById(R.id.ques_num);
            repeated_tv = (TextView) itemView.findViewById(R.id.ques_repeat);

            //optimizations
            ques_tv.getSettings().setJavaScriptEnabled(PreferenceUtils.getJS());
            ques_tv.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
            ques_tv.getSettings().setAppCacheEnabled(false);
            ques_tv.setLayerType(View.LAYER_TYPE_HARDWARE,null);
            ques_tv.setOnLongClickListener(v -> true);
            ques_tv.setLongClickable(false);

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
            PaperQuestion paperQuestion = getQues(getAdapterPosition());
            Log.d("Ques No:", String.valueOf(paperQuestion.getQuestionCode()));

            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                //Now call the function to parcel the whole data and pass it along.
                parcelData();
                Intent intent = new Intent(mContext, AnswerActivityNew.class);
                intent.putExtra("QUESTION_ADAPTER.parcelData", data);
                intent.putExtras(mIntent);
                int pos = Integer.parseInt(paperQuestion.getCode()) - 1;
                pos = getQues_pos(group_pos, pos);
                intent.putExtra("QUESTION_ADAPTER.position", pos);
                intent.putExtra("QUESTION_ADAPTER.group_name",data.get(pos).getGroupName());
                mContext.startActivity(intent);
            }
        }
    }

    //Turn this off for better performance.
//    public void updateQues(List<PaperQuestion> Papers) {
//        paperQuestions = Papers;
//        Log.d("QuestionAdapter","Total Ques:" + String.valueOf(paperQuestions.size()));
//        notifyDataSetChanged();
//    }

    private void parcelData() {
        try {
            //Clear the existing data
            data.clear();
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
    }

    private PaperQuestion getQues(int adapterPosition) {
        return paperQuestions.get(adapterPosition);
    }

    private int getQues_pos(int group_no, int ques_no) {
        int pos = 0;
        Log.d("Question group no:", String.valueOf(group_no));
        for (int i = 0; i < group_no; i++) {
            pos += paperGroups.get(i).getPaperQuestion().size();
        }
        pos += ques_no;
        Log.d("Question pos:", String.valueOf(pos));
        return pos;
    }

    public interface QuestionItemListener {
        void onQuesClick(String id);
    }
}
