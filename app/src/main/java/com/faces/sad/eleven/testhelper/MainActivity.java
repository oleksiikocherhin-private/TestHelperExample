package com.faces.sad.eleven.testhelper;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    int PERMISSION_STATUS; //Переменная отображающая состояние разрешения на запись файлов на внешний носитель 0 -false 1-true
    int REQUEST_CODE_PERMISSION_WRITE; //Переменная отображающая состояние разрешения на запись файлов на внешний носитель 0 -false 1-true
    public static SharedPreferences preferences; //Переменная для файла настроек
    public static File [] activeFolders; //Переменная для списка папок
    LinearLayout mainLayout; //Ссылка на главный лайаут

    ArrayList<Switch> selectTestSwitch; //Список выбранных папок
    ArrayList<File> selectTest; //Список выбранных тестов

    public static ArrayList<QuestionData> dataBase; //Список вопросов
    public static ArrayList<QuestionData> wrongQuestions; //Список вопросов с неправильными ответами

    int texSize = 14; //Размер текстов

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        preferences = getSharedPreferences("AppPreferences", Context.MODE_PRIVATE); //Инициализируем файл настроек
        texSize = preferences.getInt("TextSize", 14); //Устанавливаем размер текста
        setAllSizes();
        mainLayout = findViewById(R.id.folder_list); //Создаем ссылку на главный лайаут
        selectTestSwitch = new ArrayList<>(); //Инициализируем список кнопок
        selectTest = new ArrayList<>(); //Создаем список кнопок для выбранных тестов
        getPermission(); //Запрашиваем разрешения на изменения файла
        if (PERMISSION_STATUS == PackageManager.PERMISSION_GRANTED){
            initializeFolderList(); //Инициализируем список папок
        }

    }

    void setAllSizes(){
        Button folButton = findViewById(R.id.add_new_folder); //Находим кнопку
        folButton.setTextSize(texSize); //Устанавливаем размер
        Button prefButton = findViewById(R.id.setting_button); //Находим кнопку
        prefButton.setTextSize(texSize); //Устанавливаем размер
        Button startButton = findViewById(R.id.start_button); //Находим кнопку
        startButton.setTextSize(texSize); //Устанавливаем размер
    }

    @Override
    protected void onStart(){
        super.onStart();
        initializeFolderList(); //Инициализируем список файлов при каждом запуске активности
    }

    //Получаем разрешения на запись
    void getPermission(){
        //Запрос на получение разрешения на завпись на внешний носитель
        PERMISSION_STATUS = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE); //Получение текущего статуса разрешения на запись
        if (PERMISSION_STATUS == PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_PERMISSION_WRITE);
        }

    }

    //Метод возвращающий ответ на разрешения
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //Если код равен 1
        if (requestCode == REQUEST_CODE_PERMISSION_WRITE) {
            //Изменение состояния разрешений для приложений
            if (grantResults.length > 0){
                initializeFolderList(); //Если разрешение получено, то инициализируем список папок
            } else {
                //Иначе предупреждаем пользователя, что необходим доступ
                Toast warninToast = Toast.makeText(getApplicationContext(),
                        "Без разрешения на чтение файлов, невозможно открыть тесты, дайте его в настройках системы", Toast.LENGTH_SHORT);
                warninToast.show();
            }
        }
    }

    //Перемешивание вопросов
    void mixQuestions (){
        //Создаем экземпляр класса Рандом
        Random rand = new Random();
        //Проходим по списку вопросов и перемешиваем их
        for (int i = 0; i < dataBase.size(); i++){
            QuestionData currentQuestion = dataBase.get(i); //Получаем текущий вопрос
            int randomQuestion = rand.nextInt(dataBase.size()); //Получаем новое место вопроса
            dataBase.set(i, dataBase.get(randomQuestion)); //меняем вопросы местами
            dataBase.set(randomQuestion, currentQuestion); //меняем вопросы местами
        }
    }

    //Подсчет кол-ва вопросов, чтобы можно было проходить определенное их число
    void calculateQuestion (){
        int countQuestion; //Создаем переменную для счетчика
        //Проверка адекватности настроек
        if (preferences.getInt("QuestionPullSize", 0) > 0 &&
                preferences.getInt("QuestionPullSize", 0) <= dataBase.size()){
            countQuestion = preferences.getInt("QuestionPullSize", 0);
        } else {
            countQuestion = dataBase.size();
        }


        ArrayList<QuestionData> tempList = new ArrayList<>(dataBase);
        //tempList.addAll(dataBase);
        //Если перемешивать вопросы
        if (preferences.getBoolean("MixQuestions", false)) {
            dataBase.clear(); //Очищаем предыдущую базу
            Random rand = new Random(); //Создаем экземпляр класса рандом
            for (int i = 0; i < tempList.size(); i++){
                QuestionData currentQuestion = tempList.get(i); //Перемешиваем вопросы
                int randomQuestion = rand.nextInt(tempList.size()); //Перемешиваем вопросы
                tempList.set(i, tempList.get(randomQuestion)); //Перемешиваем вопросы
                tempList.set(randomQuestion, currentQuestion); //Перемешиваем вопросы
            }
            for (int j = 0; j < countQuestion; j++){
                dataBase.add(tempList.get(j)); //Добавляем перемешанные вопросы в список
            }

        } else {
            dataBase.clear();
            for (int i = 0; i < countQuestion; i++) {
                dataBase.add(tempList.get(i));
            }
        }
    }

    //Запускам кастомный проводник
    public void startExplorer(View v){
        Intent intent = new Intent(this, ExplorerActivity.class);
        startActivity(intent);

    }

    //Запускаем активность с настройками
    public void openPreferences(View v){
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    //Начинаем тестирование
    public void startTest(View v){
        //Проверка на наличие базы вопросов
        if (dataBase == null){
            dataBase = new ArrayList<>();
        } else {dataBase.clear();}
        TestBuilder currentTest = new TestBuilder(returnTestFiles()); //Создаем экземпляр класса

            dataBase.addAll(currentTest.getQuestionData()); //Получаем из него данные


        //Размер пула вопросов
        if (preferences.getInt("QuestionPullSize", 0) != 0){
            calculateQuestion();
        }

        //Перемешивать вопросы если включено
        if (preferences.getBoolean("MixQuestions", false)){
            mixQuestions();
        }

        //Сохранять неправильные ответы
        if (preferences.getBoolean("SaveWrongQuestions", false)){
            wrongQuestions = new ArrayList<>();
        }

        //Если в базе вопрсов больше 0 вопросов
        if (dataBase.size() > 0){
            if (preferences.getInt("WindowType" , 1) == 1){
                Intent intent = new Intent(this, QuestionsButtonsActivity.class);
                startActivity(intent);
            } else if (preferences.getInt("WindowType", 1) == 2){
                Intent intent = new Intent(this, QuestionsCheckBoxActivity.class);
                startActivity(intent);
            }

        }




    }

    //Возвращаем список файлов
    File[] returnTestFiles (){
        ArrayList<File> temp = new ArrayList<>();
        for(int i = 0; i < selectTestSwitch.size(); i++) //Проходим по списку отмеченных файлов
        {
            if (selectTestSwitch.get(i).isChecked()){
                temp.add(selectTest.get(i));
            }
        }
        File [] returnableArray = new File[temp.size()];
        for (int j = 0; j < temp.size(); j++){
            returnableArray[j] = temp.get(j);
        }
        return returnableArray;
    }

    //Инициализируем список папок из проводника
    void initializeFolderList(){
        Set<String> tempFolder = new HashSet<>();
        tempFolder = preferences.getStringSet("ActiveFolders", null);
        if (tempFolder != null){
            ArrayList<String> validPath = new ArrayList<>(tempFolder);
            ArrayList<String> actualPaths = new ArrayList<>();
            for (int o = 0; o < validPath.size(); o++){
                File t = new File(validPath.get(o));
                if (t.exists()){ actualPaths.add(validPath.get(o));}else {Log.e("InvalidFolder", t.getAbsolutePath());}
            }
            activeFolders = new File[actualPaths.size()];
            ArrayList<String> temp = new ArrayList<>(actualPaths);
            //temp.addAll(tempFolder);

            for (int i = 0; i < activeFolders.length; i++){
                activeFolders[i] = new File(temp.get(i)); //Проверяем существует ли папка
            }
        }
        if (activeFolders != null){
            drawActiveFolders();
        }

    }

    //Рисуем существующий список
    void drawActiveFolders(){

        mainLayout.removeAllViews();
        selectTestSwitch.clear();
        selectTest.clear();

        for (int i = 0; i < activeFolders.length; i++){
            LayoutInflater inflater = getLayoutInflater();
            View temp = inflater.inflate(R.layout.folders_item, mainLayout, false);
            TextView folderName = temp.findViewById(R.id.folderName_text_view);
            folderName.setTextSize(texSize);
            ImageView folderIcon = temp.findViewById(R.id.folderIcon_image_view);
            Button deleteButton = temp.findViewById(R.id.delete_folder_button);
            deleteButton.setTextSize(texSize);
            deleteButton.setOnClickListener(deleteClickListener);
            deleteButton.setId(i);
            folderName.setText(activeFolders[i].getName());
            folderName.setTextSize(texSize);
            folderIcon.setImageResource(R.drawable.folder_icon);
            mainLayout.addView(temp);
                for (int j = 0; j < activeFolders[i].listFiles().length; j++){
                    if (!activeFolders[i].listFiles()[j].isDirectory()){
                        String ext = returnFileExtension(activeFolders[i].listFiles()[j].getAbsolutePath());
                        if (ext.equalsIgnoreCase(".qst") ||ext.equalsIgnoreCase(".txt")){
                            Switch newSwitch = new Switch(this);
                            newSwitch.setText(activeFolders[i].listFiles()[j].getName());
                            newSwitch.setTextSize(texSize);
                            selectTestSwitch.add(newSwitch);
                            selectTest.add(activeFolders[i].listFiles()[j]);
                            mainLayout.addView(newSwitch);
                        }
                        if (ext.equalsIgnoreCase(".qsz") && preferences.getBoolean("DeveloperMode", false)){
                            Switch newSwitch = new Switch(this);
                            newSwitch.setText(activeFolders[i].listFiles()[j].getName());
                            newSwitch.setTextSize(texSize);
                            selectTestSwitch.add(newSwitch);
                            selectTest.add(activeFolders[i].listFiles()[j]);
                            mainLayout.addView(newSwitch);

                        }
                    }
                }
        }

    }

    //Возвращает расширение файла
    String returnFileExtension (String file){
        if (file.contains(".")) {
            String extension;
            int lastIndexOfDot;
            lastIndexOfDot = file.lastIndexOf('.');
            extension = file.substring(lastIndexOfDot);
            return extension;
        }else return null;

    }

    //Кастомный слушатель событий
    private View.OnClickListener deleteClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            Set<String> temp = new HashSet<String>();

            for (int i = 0; i < activeFolders.length; i++){
                if (i != v.getId()){
                    temp.add(activeFolders[i].getAbsolutePath());
                }
            }

            SharedPreferences.Editor editor = preferences.edit();

            editor.putStringSet("ActiveFolders", temp);



            editor.apply();
            initializeFolderList();

        }
    };



}
