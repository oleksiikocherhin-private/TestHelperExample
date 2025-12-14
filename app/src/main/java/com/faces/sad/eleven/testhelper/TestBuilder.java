package com.faces.sad.eleven.testhelper;

import android.util.Log;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

public class TestBuilder {

    File[] listOfTests;
    ArrayList<String> temporaryStringDataTXT;
    ArrayList<QuestionData> questionDataBase;

    public TestBuilder (File[] tests){
        listOfTests = tests;
        temporaryStringDataTXT = new ArrayList<String>();
        questionDataBase = new ArrayList<>();
    }


    void readFiles(){
        for (int i = 0; i < listOfTests.length; i++){
            String ext = returnFileExtension(listOfTests[i].getAbsolutePath());
            if (ext.equalsIgnoreCase(".qst") || ext.equalsIgnoreCase(".txt")){
                try { //Отлов исключений
                    BufferedReader reader = new BufferedReader( //Создаем ридер для файла
                            new InputStreamReader( //Создание буфера
                                    new FileInputStream(listOfTests[i]), "windows-1251")); //Создаем ридео для файла с путем selectedFileList[i]
                    String line; //Переменная для хранения текущей строки
                    while ((line = reader.readLine()) != null) { //В цикле проходим страка за строкой, пока строка не равна null
                        if (line.length() > 0)
                        temporaryStringDataTXT.add(line);
                    }
                }catch (IOException ie) { } //Отлов исключений
            } else if (ext.equalsIgnoreCase(".qsz")){

                decompressQSZFile(listOfTests[i]);

            } else {
                //
                continue;
            }
        }
    }


    void decompressQSZFile(File file) {
        try {
            byte[] fileInArray = new byte[(int) file.length()]; //преобразуем файл в байты
            FileInputStream f = new FileInputStream(file); //Читаем
            f.read(fileInArray); //Записываем
            f.close();

            Inflater decompresser = new Inflater(); //Вызываем декомпрессор
            decompresser.setInput(fileInArray); //Передаем ему данные

            File tempFile = File.createTempFile("file", "decrypt");
            int resultLenght = 0;
            FileOutputStream o = new FileOutputStream(tempFile);
            while (!decompresser.finished()){
                Log.e("REM", "" + decompresser.getRemaining());
                byte [] temp = new byte [decompresser.getRemaining()];
                resultLenght += decompresser.inflate(temp);
                o.write(temp);
            }
            o.close();
            Log.e("FILE", "Lenght " + tempFile.length() + "ResLen" + resultLenght);

            byte [] fullDecompressFile = new byte[(int)tempFile.length()];
            FileInputStream fi = new FileInputStream(tempFile);
            fi.read(fullDecompressFile);
            fi.close();


                byte [] temp = new byte [4];
                temp [0] = fullDecompressFile[0];
                temp [1] = fullDecompressFile[1];
                temp [2] = fullDecompressFile[2];
                temp [3] = fullDecompressFile[3];
                ByteBuffer bb = ByteBuffer.wrap(temp);

                bb.order(ByteOrder.LITTLE_ENDIAN);
                int j = bb.getInt();

                Log.e("INTEGER", ""+ j);
                //Log.e("String", new String(fullDecompressFile, 4, j, "windows-1251"));
            Log.e("String", new String(fullDecompressFile, 4, j, "UTF-8"));




        } catch (IOException e) {} catch (DataFormatException f) {}
    }

    void analyzeFiles(){
        ArrayList<String> temporaryList = new ArrayList<>();
        temporaryList.addAll(temporaryStringDataTXT);
        for (int i = 0; i < temporaryStringDataTXT.size(); i++){ //Проходим по каждому файлу
            String temp = temporaryStringDataTXT.get(i);

            if (thisIsBegin(temp)){
                questionDataBase.add(returnQuestion(temporaryList, i));
            }

        }

    }

    QuestionData returnQuestion(ArrayList<String> base, int startIndex){
        if (startIndex < base.size()) {
            QuestionData temp = new QuestionData(QuestionData.QuestionType.TXTQST);
            int index = startIndex + 1;
            while (!thisIsBegin(base.get(index))) {
                temp.temporaryData.add(base.get(index));
                index++;
                if (index > base.size() - 1){
                    break;
                }
            }
            return temp;
        } else {
            return null;
        }
    }




    boolean thisIsBegin(String str){
        int index = str.indexOf("?");

        if (index == -1){

            return false;

        } else if (index == 0){

            return  true;

        } else if (index > 0) {

            int currChar = 0;
            while (currChar < index){
                if (str.charAt(currChar) == ' '){
                    currChar++;
                    continue;
                } else return  false;

            }
            return true;
        }

        return false;
    }


    public ArrayList<QuestionData> getQuestionData(){
        readFiles();
        analyzeFiles();
        ArrayList<QuestionData> temp = new ArrayList<>();
        for (int i = 0; i < questionDataBase.size(); i++){
            questionDataBase.get(i).buildQuestion();
            if (questionDataBase.get(i).validQuestion){
                temp.add(questionDataBase.get(i));
            }
        }

        return temp;
    }

    String returnFileExtension (String file){
        if (file.contains(".")) {
            String extension;
            int lastIndexOfDot;
            lastIndexOfDot = file.lastIndexOf('.');
            extension = file.substring(lastIndexOfDot);

            return extension;
        }else return null;

    }
}
