package org.ruizlab.phoni.octopusapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import android.os.Bundle;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        clearInternalStorage();
        Log.i("info","Copy files from external storage to internal storage");

        File dataSource = new File(getExternalFilesDir(null)+"/database");
        String[] children = dataSource.list();
        for (int i = 0; i < dataSource.listFiles().length; i++) {
            copyDBToInternalStorage(new File(dataSource, children[i]), children[i]);
        }
        dataSource = new File(getExternalFilesDir(null)+"");
        File[] childFiles = dataSource.listFiles();
        for (int i = 0; i < childFiles.length; i++) {
            if (childFiles[i].isFile() && childFiles[i].getName().endsWith(".fastq"))
                copyDBToInternalStorage(childFiles[i], "simulmix.fastq" );
        }

        Log.i("info","Files copied");
        listFilesInInternalStorage();

        System.gc();
        WorkRequest octopusWorkRequest, analyticsWorkRequest;
        WorkManager workManager = WorkManager.getInstance(getApplicationContext());

        try {


            octopusWorkRequest = new OneTimeWorkRequest.Builder(Octopus.class).build();
            workManager.enqueue(octopusWorkRequest);
            System.out.println("OCTOPUS STARTING");
            if(((Global)getApplicationContext()).analyticsAreEnabled())
            {
                analyticsWorkRequest = new OneTimeWorkRequest.Builder(ForegroundAnalytics.class)
                        .build();
                workManager.enqueue(analyticsWorkRequest);
                System.out.println("ANALYTICS STARTING");
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    private void copyDBToInternalStorage(File file, String filename){

        InputStream is = null;
        try {
            is = new FileInputStream(file);
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

    private void clearInternalStorage() {
        // Get the internal storage directory
        File directory = getFilesDir();

        // List all files in the directory
        File[] files = directory.listFiles();

        if (files != null) {
            for (File file : files) {
                file.delete();
            }
        }
    }
}