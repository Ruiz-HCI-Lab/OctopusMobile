package com.example.octopusapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String argumentsOct[] = { "d:octopus-data/megares_database_v3.00_OCTOPUSdb_Android", "f:simulmix.fastq"};

        Log.i("info","Copy files to internal storage");
        copyDBToInternalStorage( "octopus-data/megares_database_v3.00_OCTOPUSdb_Android","int42.db");
        copyDBToInternalStorage( "octopus-data/megares_database_v3.00_OCTOPUSdb_Android","int69.db");
        copyDBToInternalStorage( "octopus-data/megares_database_v3.00_OCTOPUSdb_Android","blo42.bl");
        copyDBToInternalStorage( "octopus-data/megares_database_v3.00_OCTOPUSdb_Android","blo69.bl");
        copyDBToInternalStorage( "octopus-data","simulmix.fastq");

        Log.i("info","Files copied");
        listFilesInInternalStorage();

        try {
            Octopus.initialize(this, argumentsOct);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    private void copyDBToInternalStorage(String path, String filename){

        InputStream is = null;
        try {
            is = getAssets().open(path+"/"+filename);
            String outputPath = getFilesDir() + "/" + filename;
            OutputStream os = new FileOutputStream(outputPath);

            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }
            // Close the streams
            os.flush();
            os.close();
            is.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private void listFilesInInternalStorage() {
        // Get the internal storage directory
        File directory = getFilesDir();

        // List all files in the directory
        File[] files = directory.listFiles();

        if (files != null) {
            for (File file : files) {
                // Log the file name or do something with the file
                Log.d("InternalStorage", "File: " + file.getName());
            }
        }
    }

}