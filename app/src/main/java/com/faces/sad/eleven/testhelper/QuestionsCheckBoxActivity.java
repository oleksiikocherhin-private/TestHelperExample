package com.faces.sad.eleven.testhelper;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class QuestionsCheckBoxActivity extends AppCompatActivity {

    float testScore = 0.0f;
    int asks = 0;
    Integer limitOfAsks;
    int qNumber = 0;

    QuestionData currentQuestion;

    TextView currentQuestionText;
    TextView currentQuestionNumber;
    TextView rightAnswerCount;
    TextView allQuestionCount;

    int rightAnswer = Color.rgb(0, 255, 0);
    int wrongAnswer = Color.rgb (255, 0, 0);

    ArrayList<CheckBox> checkBoxes = new ArrayList<>();
    ArrayList<CheckBox> checksB = new ArrayList<>();

    int DELAY_PERIOD = (int) MainActivity.preferences.getFloat("DelayedPeriod", 1000);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_check_box);

        Intent qIntent = getIntent();

        currentQuestionText = findViewById(R.id.question_text_check);
        currentQuestionNumber = findViewById(R.id.current_number_text_check);
        rightAnswerCount = findViewById(R.id.question_percents_text_check);
        allQuestionCount = findViewById(R.id.question_count_text_check);

        changeQuestion();
        limitOfAsks = currentQuestion.rightAnswerInThisQuestion;

    }

    void changeQuestion(){
        checksB.clear();
        if (qNumber < MainActivity.dataBase.size()) {
            currentQuestion = MainActivity.dataBase.get(qNumber);
            if (currentQuestion != null) {
                currentQuestionText.setText(currentQuestion.questionBody.toString());

                createAnswersCheckBoxes((LinearLayout) findViewById(R.id.check_boxes_layout));
                setActualStatus();
                if ((currentQuestion.rightAnswerInThisQuestion > 1) && MainActivity.preferences.getBoolean("ShowMultiToast", false)){
                    Toast toast = Toast.makeText(getApplicationContext(),
                            "Несколько ответов", Toast.LENGTH_SHORT);
                    toast.show();
                }
            } else {
            }
        }else {
            Intent fin = new Intent(this, ResultActivity.class);
            //fin.putExtra("Question_Count", MainActivity.dataBase.size());
            fin.putExtra("Result", String.format("%.2f", ((testScore * 100) / qNumber)));
            startActivity(fin);
            finish();
        }

    }

    void setActualStatus (){
        String tempQN = getText(R.string.current_question_number) + " " + (qNumber + 1);
        String tempQCount = getText(R.string.question_pull) + " " + MainActivity.dataBase.size();
        String tempQPercent = getText(R.string.right_percent) + " " + String.format("%.2f", ((testScore * 100) / qNumber)) + "%";
        allQuestionCount.setText(tempQCount);
        currentQuestionNumber.setText(tempQN);
        rightAnswerCount.setText(tempQPercent);
        asks = 0;
    }

    void createAnswersCheckBoxes(LinearLayout ll){
        ll.removeAllViews();
        checkBoxes.clear();
        for (int i = 0; i < currentQuestion.answers.size(); i++){
            CheckBox nCheckBox = new CheckBox(this);
            if (MainActivity.preferences.getBoolean("ShowAnswers", false)){
                nCheckBox.setText(currentQuestion.answers.get(i));
                checkBoxes.add(nCheckBox);
            } else {
                nCheckBox.setText(currentQuestion.answers.get(i).substring(1));
                checkBoxes.add(nCheckBox);
            }

            nCheckBox.setWidth(ll.getWidth());
            nCheckBox.setOnClickListener(rightAnswerCheckBoxClickListener);
            nCheckBox.setId(i);
            ll.addView(nCheckBox, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        }
    }

    View.OnClickListener rightAnswerCheckBoxClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            CheckBox temp = (CheckBox) v;
            if (!MainActivity.preferences.getBoolean("MultiChecking", true)){
                if (checksB.size() < currentQuestion.rightAnswerInThisQuestion && !checksB.contains(temp)){
                    checksB.add(temp);
                    if (checksB.size() == currentQuestion.rightAnswerInThisQuestion){
                        for (int i =0; i < checkBoxes.size(); i++){
                            if (!checksB.contains(checkBoxes.get(i))){
                                checkBoxes.get(i).setEnabled(false);
                            }

                        }
                    }
                } else if (checksB.size() <= currentQuestion.rightAnswerInThisQuestion && checksB.contains(temp)){
                    checksB.remove(temp);
                    for (int i =0; i < checkBoxes.size(); i++){
                        if (!checksB.contains(checkBoxes.get(i))){
                            checkBoxes.get(i).setEnabled(true);
                        }

                    }
                }


            }

        }

    };

    public void nextQuestion(View v) {

        float rightA = 0;
        float wrongA = 0;
        for (int i = 0; i < checkBoxes.size(); i++) {
            if (checkBoxes.get(i).isChecked() && currentQuestion.rightAnswers.contains(currentQuestion.answers.get(checkBoxes.get(i).getId()))) {
                rightA++;
                //testScore += 1f / currentQuestion.rightAnswerInThisQuestion;
            } else if (checkBoxes.get(i).isChecked() && !currentQuestion.rightAnswers.contains(currentQuestion.answers.get(checkBoxes.get(i).getId()))){
                wrongA++;

                if (MainActivity.preferences.getBoolean("SaveWrongQuestions", false)){
                    if (!MainActivity.wrongQuestions.contains(currentQuestion)){
                        MainActivity.wrongQuestions.add(currentQuestion);
                        Log.e("WRONG", "This question is wrong");
                    }
                }

            }

        }

        if (rightA > 0 && wrongA == 0){
            testScore += rightA / currentQuestion.rightAnswerInThisQuestion;
        } else if (rightA > 0 && wrongA > 0) {
            testScore += rightA / (currentQuestion.rightAnswerInThisQuestion + wrongA);
        }

        if (rightA == 0){
            if (MainActivity.preferences.getBoolean("SaveWrongQuestions", false)){
                if (!MainActivity.wrongQuestions.contains(currentQuestion)){
                    MainActivity.wrongQuestions.add(currentQuestion);
                    Log.e("WRONG", "This question is wrong");
                }
            }
        }

        qNumber++;
        if (MainActivity.preferences.getBoolean("ShowFailed", false)){
            showRightAnswers();
        }


        v.postDelayed(new Runnable() {
            public void run() {
                changeQuestion();
            }
        }, DELAY_PERIOD);
    }


    void showRightAnswers (){
        for (int i =0; i < checkBoxes.size(); i++){
            if (currentQuestion.rightAnswers.contains(currentQuestion.answers.get(checkBoxes.get(i).getId()))) {
                checkBoxes.get(i).setBackgroundColor(rightAnswer);
            } else {checkBoxes.get(i).setBackgroundColor(wrongAnswer);}
        }
    }

}
