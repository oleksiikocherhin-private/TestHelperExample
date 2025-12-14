package com.faces.sad.eleven.testhelper;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class WrongQuestionsActivity extends AppCompatActivity {

    LinearLayout questionsNumberLayout;
    LinearLayout answersLayout;
    TextView wrongQuestionText;
    ArrayList<QuestionData> temporaryList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wrong_questions);
        Intent i = getIntent();
        temporaryList = new ArrayList<>();

        questionsNumberLayout = findViewById(R.id.question_numbers_layout);
        wrongQuestionText = findViewById(R.id.wrong_question_text);
        answersLayout = findViewById(R.id.answers_layout);




        temporaryList.addAll(MainActivity.wrongQuestions);
        Log.e("()()", ""+temporaryList.size());
        drawQuestionNumbersButton();
    }


    void drawQuestionNumbersButton(){
        LinearLayout.LayoutParams buttonParam = new LinearLayout.LayoutParams
                (LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        questionsNumberLayout.removeAllViews();
        for (int i = 0; i < temporaryList.size(); i++){
            Button b = new Button(this);
            String str = "" + (i + 1);
            b.setText(str);
            b.setId(1000 + i);
            b.setTextSize(MainActivity.preferences.getInt("TextSize", 14));
            b.setOnClickListener(answersButtonCheck);

            questionsNumberLayout.addView(b, buttonParam);
        }

        drawQuestion(0);

    }

    public void backToMainScreenWrong(View v){
        finish();
    }


    void drawQuestion(int number){
        QuestionData temp = temporaryList.get(number);
        answersLayout.removeAllViews();
        wrongQuestionText.setText(temp.questionBody.toString());

        for (int i = 0; i < temp.answers.size(); i++){
            if (MainActivity.preferences.getInt("WindowType", 1) == 1){
                Button nButton = new Button(this);
                nButton.setText(temp.answers.get(i));
                answersLayout.addView(nButton);

            } else if (MainActivity.preferences.getInt("WindowType", 1) == 2){
                CheckBox box = new CheckBox(this);
                box.setText(temp.answers.get(i));
                answersLayout.addView(box);
            }



        }


    }

    View.OnClickListener answersButtonCheck = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Button temp = (Button) v;
            int id = temp.getId();
            drawQuestion(id - 1000);

        }
    };

}
