package in.co.erudition.paper.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import in.co.erudition.paper.R;
import in.co.erudition.paper.activity.AnswerActivity;
import in.co.erudition.paper.data.model.PaperGroup;
import in.co.erudition.paper.data.model.PaperQuestion;
import in.co.erudition.paper.data.model.QuestionAnswer;

/**
 * Created by Arunavo Ray on 09-06-2018.
 */

public class QuestionAdapter extends RecyclerView.Adapter<QuestionAdapter.ViewHolder> {
    private List<PaperGroup> paperGroups;
    private List<PaperQuestion> paperQuestions;
    private ArrayList<QuestionAnswer> data;
    private int group_pos;
    private Context mContext;
    private Intent mIntent;
    private StringBuilder str;
    private QuestionAdapter.QuestionItemListener mItemListener;

    public QuestionAdapter(Context context, List<PaperGroup> mPapers, int pos, Intent intent, QuestionAdapter.QuestionItemListener itemListener) {
        mContext = context;
        paperQuestions = mPapers.get(pos).getPaperQuestion();
        group_pos = pos;
        mIntent = intent;
        paperGroups = mPapers;
        mItemListener = itemListener;
        data = new ArrayList<QuestionAnswer>();

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
        final WebView q_tv = holder.ques_tv;
        TextView m_tv = holder.marks_tv;
        TextView q_no_tv = holder.ques_no_tv;
        TextView r_tv = holder.repeated_tv;

        // Set item views based on your views and data model
        try {
            //q_tv.loadData(getHtmlData(ques.getQuestion()), "text/html", null);
            q_tv.loadDataWithBaseURL("file:///android_asset/", getHtmlData(ques.getQuestion()), "text/html", "UTF-8", null);
            m_tv.setText(ques.getMarks());
            q_no_tv.setText(ques.getQuestionNo() + ".");
            r_tv.setText(ques.getRepeat());

        } catch (NullPointerException | IllegalArgumentException | IndexOutOfBoundsException e) {
            Log.e("Exception", e.toString());
        }
    }

    private String getHtmlData(String data) {
        return str.toString() + data + "</body>\n</html>";
    }

    @Override
    public int getItemCount() {
        return paperQuestions.size();
    }

    @SuppressLint("SetJavaScriptEnabled")
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public WebView ques_tv;
        public TextView marks_tv;
        public TextView repeated_tv;
        public TextView ques_no_tv;

        public ViewHolder(final View itemView) {
            super(itemView);

            ques_tv = (WebView) itemView.findViewById(R.id.question_tv);
            marks_tv = (TextView) itemView.findViewById(R.id.marks_tv);
            ques_no_tv = (TextView) itemView.findViewById(R.id.ques_num);
            repeated_tv = (TextView) itemView.findViewById(R.id.ques_repeat);

            ques_tv.getSettings().setJavaScriptEnabled(true);

            ques_tv.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    return true;
                }
            });
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
            PaperQuestion paperQuestion = getQues(getAdapterPosition());
            Log.d("Ques No:", String.valueOf(paperQuestion.getQuestionCode()));

            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                //Now call the function to parcel the whole data and pass it along.
                parcelData();
                Intent intent = new Intent(mContext, AnswerActivity.class);
                intent.putExtra("QUESTION_ADAPTER.parcelData", data);
                intent.putExtras(mIntent);
                int pos = Integer.parseInt(paperQuestion.getCode()) - 1;
                pos = getQues_pos(group_pos, pos);
                intent.putExtra("QUESTION_ADAPTER.position", pos);
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
