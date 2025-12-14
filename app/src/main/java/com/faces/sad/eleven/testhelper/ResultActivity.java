package com.faces.sad.eleven.testhelper;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ResultActivity extends AppCompatActivity {

    TextView totalQuestions;
    TextView youResult;
    TextView testIsOverText;
    TextView totQuestWAText;
    TextView youResultText;

    Button showWrong;
    Button backToMain;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        Intent i = getIntent();

        initVariables();

        showResult(i);

        if (MainActivity.preferences.getBoolean("SaveWrongQuestions", false)){
            Log.e("___", "" + MainActivity.wrongQuestions.size());
        }
    }

    void initVariables(){
        totalQuestions = findViewById(R.id.total_question_asked);
        youResult = findViewById(R.id.you_result);
        showWrong = findViewById(R.id.show_wrong_questions);
        testIsOverText = findViewById(R.id.testing_is_over_text);
        totQuestWAText = findViewById(R.id.total_question_were_asked_text);
        youResultText = findViewById(R.id.you_result_is_text);
        backToMain = findViewById(R.id.back_to_main_screen_button);
    }

    void setAllTextSizes(){
        testIsOverText.setTextSize(MainActivity.preferences.getInt("TextSize", 14) * 2);
        totQuestWAText.setTextSize(MainActivity.preferences.getInt("TextSize", 14));
        youResultText.setTextSize(MainActivity.preferences.getInt("TextSize", 14));
        backToMain.setTextSize(MainActivity.preferences.getInt("TextSize", 14));
        totalQuestions.setTextSize(MainActivity.preferences.getInt("TextSize", 14));
        youResult.setTextSize(MainActivity.preferences.getInt("TextSize", 14));
        showWrong.setTextSize(MainActivity.preferences.getInt("TextSize", 14));

    }

    void showResult(Intent intent){
        setAllTextSizes();


        if (MainActivity.dataBase != null){
            String in = "" + MainActivity.dataBase.size();
            totalQuestions.setText(in);
        }

        youResult.setText(intent.getStringExtra("Result"));

        if (!MainActivity.preferences.getBoolean("SaveWrongQuestions", false)){
            showWrong.setClickable(false);
            showWrong.setVisibility(View.GONE);
        }


    }

    public void backToMainScreen(View v){
        if (MainActivity.wrongQuestions != null){
            MainActivity.wrongQuestions.clear();
        }
        finish();
    }




    public void showWrongQuestions(View v){
        Intent i = new Intent(this, WrongQuestionsActivity.class);
        startActivity(i);


        finish();
    }
}
