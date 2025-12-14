package com.faces.sad.eleven.testhelper;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class ExplorerActivity extends AppCompatActivity {

    File currentPath; //Ссылка на текущую папку
    ListView pathListView; //Ссылка на лист вью
    File [] currentFileEnvironment; //Текущее окружение
    int texSize = 14;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explorer);
        texSize = MainActivity.preferences.getInt("TextSize", 14);
        pathListView = (ListView) findViewById(R.id.list_view_path);
        setTextSizes();
        updateEnv(); //Обновляем окружение
    }

    void setTextSizes(){
        TextView curPath = findViewById(R.id.path_text);
        curPath.setTextSize(texSize);
        Button backButton = findViewById(R.id.back_button);
        backButton.setTextSize(texSize);
        Button confButton = findViewById(R.id.set_current_directory_button);
        confButton.setTextSize(texSize);
    }

    //Добавляем новую папку
    public void addNewFolder (View v){
        Set<String> folderList = new HashSet<>();

        if (MainActivity.preferences.getStringSet("ActiveFolders", null) != null) {
            folderList.addAll(MainActivity.preferences.getStringSet("ActiveFolders", null));
        }


        if (!folderList.contains(currentPath.getAbsolutePath())){
            folderList.add(currentPath.getAbsolutePath());
            SharedPreferences.Editor editor = MainActivity.preferences.edit();
            editor.putStringSet("ActiveFolders", folderList);
            editor.apply();

            finish();

        } else {
            Toast warninToast = Toast.makeText(getApplicationContext(),
                    "Эта папка уже добавлена", Toast.LENGTH_SHORT);
            warninToast.show();
        }


    }

    void updateEnv(){

        if (currentPath == null){
            currentPath = Environment.getExternalStorageDirectory();
            updateEnv();
        } else {

            if (currentPath.getAbsolutePath().equals(Environment.getExternalStorageDirectory().getAbsolutePath())){

                Log.e("THISPATH", "ACCES");
                findViewById(R.id.back_button).setEnabled(false);
                findViewById(R.id.set_current_directory_button).setEnabled(false);
            } else {
            findViewById(R.id.back_button).setEnabled(true);
                findViewById(R.id.set_current_directory_button).setEnabled(true);
            }


            TextView curPathView = findViewById(R.id.path_text);
            curPathView.setText(currentPath.getAbsolutePath());
            currentFileEnvironment = new File[currentPath.listFiles().length];

            currentFileEnvironment = currentPath.listFiles();

            Arrays.sort(currentFileEnvironment);

            pathListView.setAdapter(new CustomIconListAdapter(this, R.layout.pathlist_item, currentFileEnvironment));
            pathListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        if (currentFileEnvironment[position].isDirectory()){
                            currentPath = currentFileEnvironment[position];
                            Log.e("NEWPATH", currentPath.getAbsolutePath());
                            updateEnv();
                        }

                }
            });
        }

    }




    public void backToPreviousPath(View v)
    {
        Log.e("Path", currentPath.toString());
        File temp = new File(currentPath.getAbsolutePath().substring(0, currentPath.getAbsolutePath().lastIndexOf("/")));
        currentPath = temp;
        updateEnv();
    }


    class CustomIconListAdapter extends ArrayAdapter<File> {
        CustomIconListAdapter (Context context, int textViewResourceId, File[] objects)
        { super(context, textViewResourceId, objects); }


        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent)
        {

            LayoutInflater inflater = getLayoutInflater();
            View row = inflater.inflate(R.layout.pathlist_item, parent, false);
            TextView name = (TextView) row.findViewById(R.id.name_text_view);
            name.setTextSize(texSize);
            TextView path = (TextView) row.findViewById(R.id.path_text_view);
            path.setTextSize(texSize);
            name.setText(currentFileEnvironment[position].getName());
            path.setText(currentFileEnvironment[position].getAbsolutePath());
            ImageView iconImageView = (ImageView) row.findViewById(R.id.icon_image_view);


            if ( currentFileEnvironment[position].isDirectory() ) {
                iconImageView.setImageResource(R.drawable.folder_icon);
            } else if (currentFileEnvironment[position].isFile()){
                iconImageView.setImageResource(R.drawable.file_icon);
            }

            return row;
        }



    }

}
