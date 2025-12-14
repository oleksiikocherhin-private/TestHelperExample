package com.faces.sad.eleven.testhelper;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class SettingsActivity extends AppCompatActivity {

    Switch mixQuestions; //Переменная для switch Перемешивать вопросы
    Switch mixAnswers; //Переменная для switch Перемешивать ответы
    Switch showAnswers; //Переменная для switch Показывать правильные ответы ( + или - )
    Switch showResultOfTest; //Переменная для switch Показывать ответы в самом тесте
    Switch multiCheck; //Переменная для switch Можно ли выбирать несколько ответов если правильный только один
    Switch showWrongQuestions; //Переменная для switch Показывать ли вопросы в которых были совершены ошибки после тестирования

    Button defButton; //Переменная для кнопки сброса настроек на стандартные
    Button confirmButton; //Переменная для кнопки принятия изменений

    EditText questionPullSize; //Переменная для поля ввода кол-ва вопросов
    EditText delayedPeriod; //Переменная для ввода времени задержки
    EditText textSize; //переменная для ввода размера текста

    Switch developerButton; //Кнопка для функции работы с qsz файлами

    CheckBox defaultTestWindow; //ЧекБокс для выбора типа интерфейса в вопросе
    CheckBox classicTestWindow; //ЧекБокс для выбора типа интерфейса в вопросе

    int texSize = 14;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        initializeVariables(); //Инициализируем переменные
        loadPreferences(); //Загружаем ранее установленные параметры

    }

    void initializeVariables(){

        mixQuestions = findViewById(R.id.mix_question_switch);
        mixAnswers = findViewById(R.id.mix_answer_switch);;
        showAnswers = findViewById(R.id.show_answers_switch);
        showResultOfTest = findViewById(R.id.show_failed_questions_switch);
        multiCheck = findViewById(R.id.multi_checking_switch);
        showWrongQuestions = findViewById(R.id.show_wrong_questions_switch);

        defButton = findViewById(R.id.default_settings_button);
        confirmButton = findViewById(R.id.confirm_settings_button);

        questionPullSize = findViewById(R.id.question_pull_size_field);
        delayedPeriod = findViewById(R.id.delayed_period_filed);
        textSize = findViewById(R.id.text_size_field);

        developerButton = findViewById(R.id.developer_mode_switch);

        classicTestWindow = findViewById(R.id.classic_type_check);
        defaultTestWindow = findViewById(R.id.default_type_check);

        setTextSizes();

    }

    void loadPreferences(){
        mixQuestions.setChecked(MainActivity.preferences.getBoolean("MixQuestions", true));
        mixAnswers.setChecked(MainActivity.preferences.getBoolean("MixAnswers", true));
        showAnswers.setChecked(MainActivity.preferences.getBoolean("ShowAnswers", true));
        showResultOfTest.setChecked(MainActivity.preferences.getBoolean("ShowFailed", true));
        developerButton.setChecked(MainActivity.preferences.getBoolean("DeveloperMode", false));
        multiCheck.setChecked(MainActivity.preferences.getBoolean("MultiChecking", false));
        showWrongQuestions.setChecked(MainActivity.preferences.getBoolean("SaveWrongQuestions", false));

        questionPullSize.setText("" + MainActivity.preferences.getInt("QuestionPullSize", 0));
        delayedPeriod.setText("" + MainActivity.preferences.getFloat("DelayedPeriod", 1000f) / 1000f);
        textSize.setText("" + MainActivity.preferences.getInt("TextSize", 14));

        texSize = MainActivity.preferences.getInt("TextSize", 14);

        if (MainActivity.preferences.getInt("WindowType", 1) == 1){
            defaultTestWindow.setChecked(true);
            classicTestWindow.setChecked(false);
        } else if (MainActivity.preferences.getInt("WindowType", 1) == 2){
            defaultTestWindow.setChecked(false);
            classicTestWindow.setChecked(true);
        }


    }

    public void updatePreferencesState(View v){
        SharedPreferences.Editor editor = MainActivity.preferences.edit(); //Создаем объект редактора

        //Проверяем не выбраны ли оба варианта интерфейса или выбран ли хотя бы один
        if ((defaultTestWindow.isChecked() && classicTestWindow.isChecked()) || (!defaultTestWindow.isChecked() && !classicTestWindow.isChecked())){
            Toast warninToast = Toast.makeText(getApplicationContext(),
                    "Выберите 1 из вариантов интерфейса", Toast.LENGTH_SHORT);
            warninToast.show();
            return;
        }

        //Выбираем первый тип
        if (defaultTestWindow.isChecked() && !classicTestWindow.isChecked()){
            editor.putInt("WindowType", 1);
        }

        //Выбираем второй тип
        if (!defaultTestWindow.isChecked() && classicTestWindow.isChecked()){
            editor.putInt("WindowType", 2);
        }

        //Показывать ли неправильные ответы
        if (showWrongQuestions.isChecked()){
            editor.putBoolean("SaveWrongQuestions", true);
        } else if (!showWrongQuestions.isChecked()){
            editor.putBoolean("SaveWrongQuestions", false);
        }

        //Разрешать ли выбор нескольких вариантов ответа
        if (multiCheck.isChecked()){
            editor.putBoolean("MultiChecking" , true);
        } else if (!multiCheck.isChecked()){
            editor.putBoolean("MultiChecking", false);
        }

        //Функции разработчика
        if (developerButton.isChecked()){
            editor.putBoolean("DeveloperMode", true);
        } else if (!developerButton.isChecked()){
            editor.putBoolean("DeveloperMode", false);
        }

        //Перемешивать ли вопросы
        if (mixQuestions.isChecked()){
            editor.putBoolean("MixQuestions", true);
        } else if (!mixQuestions.isChecked()){
            editor.putBoolean("MixQuestions", false);
        }

        //Перемешивать ли ответы
        if (mixAnswers.isChecked()){
            editor.putBoolean("MixAnswers", true);
        } else if (!mixAnswers.isChecked()){
            editor.putBoolean("MixAnswers", false);
        }

        //Показывать ли тип ответа
        if (showAnswers.isChecked()){
            editor.putBoolean("ShowAnswers", true);
        } else if (!showAnswers.isChecked()){
            editor.putBoolean("ShowAnswers", false);
        }

        //Показывать ли список вопросов в которых была совершена ошибка
        if (showResultOfTest.isChecked()){
            editor.putBoolean("ShowFailed", true);
        } else if (!showResultOfTest.isChecked()){
            editor.putBoolean("ShowFailed", false);
        }


        if (questionPullSize.getText().toString() != "0"){
            if (Integer.parseInt(questionPullSize.getText().toString()) < 0) {
                editor.putInt("QuestionPullSize", 0);
            } else {
                editor.putInt("QuestionPullSize", Integer.parseInt(questionPullSize.getText().toString()));
            }
        } else {
            editor.putInt("QuestionPullSize", 0);
        }

        if (delayedPeriod.getText().toString() != "1.0"){
            if ((Float.parseFloat(delayedPeriod.getText().toString()) * 1000f) < 0){
                editor.putFloat("DelayedPeriod", 1000f);
            } else {
                editor.putFloat("DelayedPeriod", Float.parseFloat(delayedPeriod.getText().toString()) * 1000f);
            }
        } else {
            editor.putFloat("DelayedPeriod", 1000f);
        }

        if (textSize.getText().toString() != "14"){
            if (Integer.parseInt(textSize.getText().toString()) < 0 || Integer.parseInt(textSize.getText().toString()) > 22){
                if (Integer.parseInt(textSize.getText().toString()) < 0){
                    editor.putInt("TextSize", 14);
                } else {
                    editor.putInt("TextSize", 22);
                }
            }
            editor.putInt("TextSize", Integer.parseInt(textSize.getText().toString()));
        } else {
            editor.putInt("TextSize", 14);
        }

        editor.apply(); //Принимаем изменения
        finish(); //Завершаем активность
    }

    //Загружаем стандартные настройки
    public void setPreferencesToDefault(View v){
        SharedPreferences.Editor editor = MainActivity.preferences.edit();


        editor.putBoolean("MixQuestions", true);
        editor.putBoolean("MixAnswers", true);
        editor.putBoolean("ShowAnswers", true);
        editor.putBoolean("ShowFailed", true);
        editor.putInt("QuestionPullSize", 0);
        editor.putFloat("DelayedPeriod", 1000f);
        editor.putInt("TextSize", 14);

        editor.apply(); //Применяем настройки
        finish(); //Завершаем активность
    }

    void setTextSizes(){
        TextView mainSet = findViewById(R.id.main_settings_text);
        mainSet.setTextSize(texSize);

        TextView howManyQuestAsk = findViewById(R.id.how_many_question_to_ask_text);
        howManyQuestAsk.setTextSize(texSize);

        TextView iterfaceT = findViewById(R.id.interface_type_text);
        iterfaceT.setTextSize(texSize);

        TextView iterfaceS = findViewById(R.id.interface_settings_text);
        iterfaceS.setTextSize(texSize);

        TextView delayT = findViewById(R.id.delay_time_text);
        delayT.setTextSize(texSize);

        TextView tSize = findViewById(R.id.text_size_text);
        tSize.setTextSize(texSize);

        mixQuestions.setTextSize(texSize);
        mixAnswers.setTextSize(texSize);
        showAnswers.setTextSize(texSize);
        showResultOfTest.setTextSize(texSize);
        multiCheck.setTextSize(texSize);
        showWrongQuestions.setTextSize(texSize);

        defButton.setTextSize(texSize);
        confirmButton.setTextSize(texSize);

        questionPullSize.setTextSize(texSize);
        delayedPeriod.setTextSize(texSize);
        textSize.setTextSize(texSize);

        developerButton.setTextSize(texSize);

        defaultTestWindow.setTextSize(texSize);
        classicTestWindow.setTextSize(texSize);


    }
}
