package com.faces.sad.eleven.testhelper;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class QuestionsButtonsActivity extends AppCompatActivity {

    float testScore = 0.0f;
    boolean asked = false;
    int asks = 0;
    Integer limitOfAsks;
    int qNumber = 0;
    int DELAY_PERIOD = (int) MainActivity.preferences.getFloat("DelayedPeriod", 1000);

    QuestionData currentQuestion;

    TextView currentQuestionText;
    TextView currentQuestionNumber;
    TextView rightAnswerCount;
    TextView allQuestionCount;

    int rightAnswer = Color.rgb(0, 255, 0);
    int wrongAnswer = Color.rgb (255, 0, 0);

    ArrayList<Button> buttons = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questions_buttons);

        Intent qIntent = getIntent();

        currentQuestionText = findViewById(R.id.question_text_field);
        currentQuestionNumber = findViewById(R.id.current_number_text);
        rightAnswerCount = findViewById(R.id.question_percents_text);
        allQuestionCount = findViewById(R.id.question_count_text);

        changeQuestion();
        limitOfAsks = currentQuestion.rightAnswerInThisQuestion;
    }


    //Выбираем вопросы из доступного пула
    void changeQuestion(){
        if (qNumber < MainActivity.dataBase.size()) {
            currentQuestion = MainActivity.dataBase.get(qNumber);
            if (currentQuestion != null) {
                currentQuestionText.setText(currentQuestion.questionBody.toString());

                createAnswersButton((LinearLayout) findViewById(R.id.answers_list));
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

    //Обновляем данные о прогрессе тестирования
    void setActualStatus (){
        String tempQN = getText(R.string.current_question_number) + " " + (qNumber + 1);
        String tempQCount = getText(R.string.question_pull) + " " + MainActivity.dataBase.size();
        String tempQPercent = getText(R.string.right_percent) + " " + String.format("%.2f", ((testScore * 100) / qNumber)) + "%";
        allQuestionCount.setText(tempQCount);
        currentQuestionNumber.setText(tempQN);
        rightAnswerCount.setText(tempQPercent);
        asked = false;
        asks = 0;
    }
    //Создаем кнопки с ответами
    void createAnswersButton(LinearLayout ll){
        ll.removeAllViews();
        buttons.clear();
        for (int i = 0; i < currentQuestion.answers.size(); i++){
            Button nButton = new Button(this);
            if (MainActivity.preferences.getBoolean("ShowAnswers", false)){
                nButton.setText(currentQuestion.answers.get(i));
                buttons.add(nButton);
            } else {
                nButton.setText(currentQuestion.answers.get(i).substring(1));
                buttons.add(nButton);
            }

            nButton.setWidth(ll.getWidth());
            nButton.setOnClickListener(rightAnswerButtonClickListener);
            nButton.setId(i);
            ll.addView(nButton, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        }
    }

    //Создаем слушатель событий для нажатия кнопок
    View.OnClickListener rightAnswerButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Button temp = (Button) v;

            if (!asked){
                if (currentQuestion.rightAnswers.contains(currentQuestion.answers.get(v.getId()))){
                    temp.setBackgroundColor(rightAnswer);
                    if (asks == 0) {
                        qNumber++;
                    }
                    testScore += 1f / currentQuestion.rightAnswerInThisQuestion;
                    temp.setClickable(false);
                    asks++;
                    if (asks == currentQuestion.rightAnswerInThisQuestion) {
                        asked = true;
                        if (MainActivity.preferences.getBoolean("ShowFailed", false)){
                            showRightAnswers();
                        }
                        v.postDelayed(new Runnable() {
                            public void run() {
                                changeQuestion();
                            }
                        }, DELAY_PERIOD);
                    }
                } else if (!currentQuestion.rightAnswers.contains(currentQuestion.answers.get(v.getId()))){
                    temp.setBackgroundColor(wrongAnswer);

                    if (MainActivity.preferences.getBoolean("SaveWrongQuestions", false)){
                        if (!MainActivity.wrongQuestions.contains(currentQuestion)){
                            MainActivity.wrongQuestions.add(currentQuestion);
                            Log.e("WRONG", "This question is wrong");
                        }
                    }

                    if (asks == 0) {
                        qNumber++;
                    }
                    asks++;
                    if (asks == currentQuestion.rightAnswerInThisQuestion) {
                        asked = true;
                        if (MainActivity.preferences.getBoolean("ShowFailed", false)){
                            showRightAnswers();
                        }
                        v.postDelayed(new Runnable() {
                            public void run() {
                                changeQuestion();
                            }
                        }, DELAY_PERIOD);
                    }
                }
            }
        }
    };

    void showRightAnswers (){
        for (int i =0; i < buttons.size(); i++){
            if (currentQuestion.rightAnswers.contains(currentQuestion.answers.get(buttons.get(i).getId())))
                buttons.get(i).setBackgroundColor(rightAnswer);
        }
    }





}
