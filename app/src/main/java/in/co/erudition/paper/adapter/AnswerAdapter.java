package in.co.erudition.paper.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.widget.TextView;

import java.util.List;

import androidx.annotation.Keep;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;
import in.co.erudition.paper.R;
import in.co.erudition.paper.data.model.Paper;
import in.co.erudition.paper.data.model.PaperGroup;
import in.co.erudition.paper.data.model.PaperQuestion;
import in.co.erudition.paper.data.model.QuestionAnswer;
import in.co.erudition.paper.misc.NestedScrollWebView;
import in.co.erudition.paper.util.PreferenceUtils;

/**
 * Created by Arunavo Ray on 13-06-2018.
 */
@Keep
public class AnswerAdapter extends RecyclerView.Adapter<AnswerAdapter.ViewHolder> {

    private List<QuestionAnswer> data;
    private StringBuilder str;
    private StringBuilder css_js;
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


        css_js = new StringBuilder("<html>");

        css_js.append("<head>\n    <link rel=\"stylesheet\" href=\"prism.css\"> <link rel=\"stylesheet\" href=\"font.css\">\n");
        css_js.append("    <style>\n body{font-size:14px;font-family:'Source Sans Pro',sans-serif}p{margin-top:0;margin-bottom:.4rem}");
        css_js.append("   img {height: auto!important;  width: 100%!important; overflow-x: auto!important; overflow-y: hidden!important;\n");
        css_js.append("            border: none!important;\n max-width: fit-content;\n vertical-align: middle;\n");
        css_js.append("        }\n  table { width: 100%!important; background-color: transparent; border-spacing: 0; border-collapse: collapse;}\n");
        css_js.append("    </style>\n <script src=\"prism.js\"></script>\n</head>");
        css_js.append("<body>\n");

        str = new StringBuilder("<html>");

        str.append("<head>\n<link rel=\"stylesheet\" href=\"font.css\">\n");
        str.append("    <style>\n body{font-size:14px;font-family:'Source Sans Pro',sans-serif}p{margin-top:0;margin-bottom:.4rem}");
        str.append("   img {height: auto!important;  width: 100%!important; overflow-x: auto!important; overflow-y: hidden!important;\n");
        str.append("            border: none!important;\n max-width: fit-content;\n vertical-align: middle;\n");
        str.append("        }\n  table { width: 100%!important; background-color: transparent; border-spacing: 0; border-collapse: collapse;}\n");
        str.append("    </style>\n</head>");
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
        NestedScrollWebView question_tv = holder.ques_tv;
        NestedScrollWebView answer_tv = holder.ans_tv;
        TextView mark_tv = holder.marks_tv;
        TextView ques_num_tv = holder.ques_no_tv;
        View ad_space = holder.ad_spacer;

        try {
            mToolbar.setTitle(questionAnswer.getGroupName());
            //q_tv.loadData(getHtmlData(ques.getQuestion()), "text/html", null);
            if (questionAnswer.getJavascript().contentEquals("10") || questionAnswer.getJavascript().contentEquals("11")) {
                question_tv.loadDataWithBaseURL("file:///android_asset/", getHtmlDataWithJs(questionAnswer.getQuestion()), "text/html", "UTF-8", null);
            } else {
                question_tv.loadDataWithBaseURL("file:///android_asset/", getHtmlData(questionAnswer.getQuestion()), "text/html", "UTF-8", null);
            }

            if (questionAnswer.getJavascript().contentEquals("01") || questionAnswer.getJavascript().contentEquals("11")) {
                answer_tv.loadDataWithBaseURL("file:///android_asset/", getHtmlDataWithJs(questionAnswer.getAnswer()), "text/html", "UTF-8", null);
            } else {
                answer_tv.loadDataWithBaseURL("file:///android_asset/", getHtmlData(questionAnswer.getAnswer()), "text/html", "UTF-8", null);
            }

            //Override JavaScript
            question_tv.getSettings().setJavaScriptEnabled(questionAnswer.getJavascript().contentEquals("10") || questionAnswer.getJavascript().contentEquals("11"));
            answer_tv.getSettings().setJavaScriptEnabled(questionAnswer.getJavascript().contentEquals("01") || questionAnswer.getJavascript().contentEquals("11"));

            mark_tv.setText(questionAnswer.getMarks());
            ques_num_tv.setText(questionAnswer.getQuestionNo() + ".");

            ad_space.setVisibility(View.VISIBLE);

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

    private String getHtmlDataWithJs(String data) {
        return css_js.toString() + data + "</body>\n</html>";
    }

    @SuppressLint("SetJavaScriptEnabled")
    public class ViewHolder extends RecyclerView.ViewHolder {
        public NestedScrollWebView ques_tv;
        public NestedScrollWebView ans_tv;
        public TextView marks_tv;
        public TextView ques_no_tv;
        public View ad_spacer;

        public ViewHolder(View itemView) {
            super(itemView);

            ques_tv = (NestedScrollWebView) itemView.findViewById(R.id.question_tv);
            ans_tv = (NestedScrollWebView) itemView.findViewById(R.id.answer_tv);

            marks_tv = (TextView) itemView.findViewById(R.id.marks_tv);
            ques_no_tv = (TextView) itemView.findViewById(R.id.ques_num);

            ad_spacer = (View) itemView.findViewById(R.id.nav_spacer_ad);

            //Setup Zoom Controls
            ques_tv.getSettings().setSupportZoom(true);
            ques_tv.getSettings().setBuiltInZoomControls(true);
            ques_tv.getSettings().setDisplayZoomControls(false);

            ans_tv.getSettings().setSupportZoom(true);
            ans_tv.getSettings().setBuiltInZoomControls(true);
            ans_tv.getSettings().setDisplayZoomControls(false);

            ques_tv.getSettings().setJavaScriptEnabled(PreferenceUtils.getJS());
            ques_tv.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
            ques_tv.getSettings().setAppCacheEnabled(false);
            ques_tv.setLayerType(View.LAYER_TYPE_HARDWARE, null);
            ques_tv.setOnLongClickListener(v -> true);
            ques_tv.setLongClickable(false);

            ans_tv.getSettings().setJavaScriptEnabled(PreferenceUtils.getJS());
            ans_tv.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
            ans_tv.getSettings().setAppCacheEnabled(false);
            ans_tv.setLayerType(View.LAYER_TYPE_HARDWARE, null);
            ans_tv.setOnLongClickListener(v -> true);
            ans_tv.setLongClickable(false);

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
