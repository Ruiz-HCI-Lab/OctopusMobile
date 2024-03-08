// -----------------------------------------------------------------------
// <copyright file="ForegroundAnalytics.java" company="Ruiz HCI Lab">
// Copyright (c) Ruiz HCI Lab. All rights reserved.
// Licensed under the MIT license. See LICENSE file in the repository root for full license information.
// </copyright>
// -----------------------------------------------------------------------

package org.ruizlab.phoni.octopusapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import org.json.JSONObject;

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

        getExternalFilesDir(null); //to create app data storage folder

//        String configurations = loadConfig();
//        JSONObject config = new JSONObject(configurations);
//        String databaseName = config.getString("databaseName");
//        String fastqFileName = config.getString("fastqFileName");

        Button btnStart = findViewById(R.id.btnStart);
        RadioGroup rgDatabase = findViewById(R.id.rgDatabase);
        RadioGroup rgFastQ = findViewById(R.id.rgFastQ);

        EditText etCustomDB = findViewById(R.id.etCustomDB);
        EditText etCustomSeq = findViewById(R.id.etCustomSeq);

        rgDatabase.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rbCustomDB) {
                etCustomDB.setVisibility(View.VISIBLE);
            } else {
                etCustomDB.setVisibility(View.GONE);
            }
        });

        rgFastQ.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rbCustomSeq) {
                etCustomSeq.setVisibility(View.VISIBLE);
            } else {
                etCustomSeq.setVisibility(View.GONE);
            }
        });



        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedDBId = rgDatabase.getCheckedRadioButtonId();
                String databaseName = getSelectedDBName(selectedDBId);

                int selectedFastQId = rgFastQ.getCheckedRadioButtonId();
                String fastqFileName = getSelectedFastQName(selectedFastQId);

                if (databaseName.trim().isEmpty() || fastqFileName.trim().isEmpty()) {
                    Log.e("Error","Empty Database or Sequence Field");
                    return; // Stop further execution
                }

                btnStart.setEnabled(false);
                startAnalysis(databaseName,fastqFileName);
            }
        });
    }

    private String getSelectedDBName(int selectedDBId) {
        String selectedDB="";
        if (selectedDBId == R.id.rbDbWHO) {
            selectedDB = "who_priority_bacteria";
        } else if (selectedDBId == R.id.rbDbMITO) {
            selectedDB = "mitochondrion.1.1.genomic";
        } else if (selectedDBId == R.id.rbDbVIRAL) {
            selectedDB = "viral.1.1.genomic";
        } else if (selectedDBId == R.id.rbDbMEGA) {
            selectedDB = "megares_database_v3.00";
        } else if (selectedDBId == R.id.rbCustomDB){
            EditText editText = findViewById(R.id.etCustomDB);
            selectedDB = editText.getText().toString();
            if(selectedDB.trim().isEmpty()) {
                editText.setError("This field cannot be empty");
                return "";
            }
        }
        else {
            selectedDB = "megares_database_v3.00"; //default
        }
        return selectedDB;
    }

    private String getSelectedFastQName(int selectedFastQId) {
        String selectedFile="";
        if (selectedFastQId == R.id.rbFQ605) {
            selectedFile = "SRR9687605.fastq";
        } else if (selectedFastQId == R.id.rbFQ647) {
            selectedFile = "SRR9687647.fastq";
        } else if (selectedFastQId == R.id.rbFQ648) {
            selectedFile = "SRR9687648.fastq";
        } else if (selectedFastQId == R.id.rbCustomSeq){
            EditText editText = findViewById(R.id.etCustomSeq);
            selectedFile = editText.getText().toString();
            if(selectedFile.trim().isEmpty()) {
                editText.setError("This field cannot be empty");
                return "";
            }
        }
        else {
            selectedFile = "SRR9687605.fastq"; //default
        }
        return selectedFile;
    }

    private void startAnalysis(String databaseName, String fastqFileName) {
        try {

            Log.i("info","Copy files from external storage to internal storage");

            File dataSource = new File(getExternalFilesDir(null)+"/"+ databaseName);
            String[] children = dataSource.list();
            if(children==null) { //Error handling
                Toast.makeText(getApplicationContext(), "Database not found", Toast.LENGTH_SHORT).show();
                findViewById(R.id.btnStart).setEnabled(true);
                return;
            }
            for (int i = 0; i < dataSource.listFiles().length; i++) {
                copyDBToInternalStorage(new File(dataSource, children[i]), children[i]);
            }
            File fastqSource = new File(getExternalFilesDir(null)+"/"+fastqFileName);
            if(fastqSource.isFile())
                copyDBToInternalStorage(fastqSource, fastqFileName );


            Log.i("info","Files copied");
            listFilesInInternalStorage();

            System.gc();
            WorkRequest octopusWorkRequest, analyticsWorkRequest = null;
            WorkManager workManager = WorkManager.getInstance(getApplicationContext());



            Data myParameters = new Data.Builder()
                    .putStringArray(Octopus.KEY_ARGS, new String[]{"d:" + databaseName, "f:" + fastqFileName})
                    .build();

            octopusWorkRequest = new OneTimeWorkRequest.Builder(Octopus.class).setInputData(myParameters).build();
            workManager.enqueue(octopusWorkRequest);
            System.out.println("OCTOPUS STARTING");
            if(((Global)getApplicationContext()).analyticsAreEnabled())
            {
                ((Global)this.getApplicationContext()).setSequenceFilename(fastqFileName);
                ((Global)this.getApplicationContext()).setReferenceFilename(databaseName);
                analyticsWorkRequest = new OneTimeWorkRequest.Builder(ForegroundAnalytics.class)
                        .build();
                workManager.enqueue(analyticsWorkRequest);
                System.out.println("ANALYTICS STARTING");
            }
            workManager.getWorkInfoByIdLiveData(octopusWorkRequest.getId())
                    .observe((LifecycleOwner) this, workInfo -> {
                        if (workInfo.getState() == WorkInfo.State.SUCCEEDED) {
                            Toast.makeText(getApplicationContext(),"CSV Created!", Toast.LENGTH_LONG).show();
                            Button btnStart = findViewById(R.id.btnStart);
                            btnStart.setEnabled(true);
                        }
                    });
            if(analyticsWorkRequest!=null) {
                workManager.getWorkInfoByIdLiveData(analyticsWorkRequest.getId())
                        .observe((LifecycleOwner) this, workInfo -> {
                            if (workInfo.getState() == WorkInfo.State.SUCCEEDED) {
                                Toast.makeText(getApplicationContext(), "ANALYTICS DONE!", Toast.LENGTH_SHORT).show();
                            }
                        });
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
            Toast.makeText(getApplicationContext(),"File not found", Toast.LENGTH_SHORT).show();
            findViewById(R.id.btnStart).setEnabled(true);
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

    private String loadConfig(){
        String json = null;
        try {
            InputStream is = getAssets().open("config.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }
}