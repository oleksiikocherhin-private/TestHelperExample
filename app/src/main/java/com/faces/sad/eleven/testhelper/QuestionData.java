package com.faces.sad.eleven.testhelper;

import android.util.Log;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Random;

public class QuestionData {
    enum QuestionType {TXTQST, QSZ}
    ArrayList<String> temporaryData;
    public int rightAnswerInThisQuestion;
    boolean validQuestion = true;
    QuestionType type;
    ArrayList<String> questionBody;
    ArrayList<String> answers;
    ArrayList<String> rightAnswers;

    public QuestionData (QuestionType _type){
        temporaryData = new ArrayList<>();
        questionBody = new ArrayList<>();
        answers = new ArrayList<>();
        rightAnswers = new ArrayList<>();
        type =  _type;
    }

    public void buildQuestion (){
        if (type == QuestionType.TXTQST){
            if (temporaryData != null && temporaryData.size() > 0) {
                createQuestionText();
                validateQuestion();
                for (int i = 0; i < questionBody.size(); i++) {
                    Log.e("Question", questionBody.get(i));
                }
                if (MainActivity.preferences.getBoolean("MixAnswers", false)) {
                    mixAnswers();
                }
        } else {Log.e("InvalidQuestion", "Question is invalid");}
        }


    }


    private void createQuestionText(){
        for (int i = 0; i < temporaryData.size(); i++){
            String temp = temporaryData.get(i);
            if (temp.charAt(0) == '-' || temp.charAt(0) == '+'){
                createQuestionAnswers(i);
                break;
            }else{
                questionBody.add(temp);
            }
        }
    }

    private void createQuestionAnswers (Integer lastLine){
        String previousQuestionType = null;
        for (int i = lastLine; i < temporaryData.size(); i++){
            String temp = temporaryData.get(i);
            if (temp.charAt(0) == '-'){
                answers.add(temp);
                previousQuestionType = "wrong";
            }else if (temp.charAt(0) == '+'){
                answers.add(temp);
                rightAnswers.add(temp);
                previousQuestionType = "right";
            }else{
                if (previousQuestionType == "right"){
                    String previousAAnswer = answers.get(answers.size()-1);
                    String previousRAnswer = rightAnswers.get(rightAnswers.size()-1);
                    int prevA = answers.size()-1;
                    int prevR = rightAnswers.size()-1;
                    answers.set(prevA, previousAAnswer + " " + temp);
                    rightAnswers.set(prevR, previousRAnswer + " " + temp);

                }else if (previousQuestionType == "wrong"){
                    String previousAAnswer = answers.get(answers.size()-1);
                    int prevA = answers.size()-1;
                    answers.set(prevA, previousAAnswer + " " + temp);
                }
            }
        }
    }


    private void validateQuestion(){
        if (answers.size() == 0 || rightAnswers.size() == 0){
            validQuestion = false;
        }
        rightAnswerInThisQuestion = rightAnswers.size();
    }

    private void mixAnswers () {
        Random rand = new Random();
        for (int i = 0; i < answers.size(); i++){
            int tempInt = rand.nextInt(answers.size());
            String temp = answers.get(i);
            answers.set(i, answers.get(tempInt));
            answers.set(tempInt, temp);
        }
    }


}
