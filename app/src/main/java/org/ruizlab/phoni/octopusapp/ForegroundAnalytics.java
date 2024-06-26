// -----------------------------------------------------------------------
// <copyright file="ForegroundAnalytics.java" company="Ruiz HCI Lab">
// Copyright (c) Ruiz HCI Lab. All rights reserved.
// Licensed under the MIT license. See LICENSE file in the repository root for full license information.
// </copyright>
// -----------------------------------------------------------------------

package org.ruizlab.phoni.octopusapp;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.ForegroundInfo;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class ForegroundAnalytics extends Worker{

    private NotificationManager notificationManager;

    public ForegroundAnalytics(
            @NonNull Context context,
            @NonNull WorkerParameters params) {
        super(context, params);
        //super(params);
    }

    @NonNull
    @Override
    public Result doWork() {
        setForegroundAsync(createForegroundInfo());
        ArrayList<String> analyticValues = new ArrayList<>();
        long startTime, endTime, elapsedTime, initialCPUTime, finalCPUTime, totalCPUTime;
        float maxRam = 0;
        float totalRam = 0;
        float currentRam;
        float maxTemp = 0;
        float totalTemp = 0;
        float currentTemp;
        int counter = 0;
        startTime = System.currentTimeMillis();

        /*
        List of analytics:
            [0]- A. Sequence file
            [1]- B. Reference file
            [2]- E. Total wall time
            [3]- F. Total CPU time
            [4]- G. Max RAM usage
            [5]- H. Average RAM usage
            [6]- I. Max temperature
            [7]- J. Average temperature
        */

        try {

            System.out.println("ANALYTICS STARTED");
            String fileLocation = getApplicationContext().getExternalFilesDir(null) + "/" + ((Global)this.getApplicationContext()).getSequenceFilename() + "_Analytics.csv";
            FileWriter fileWriter = new FileWriter(fileLocation);
            BufferedWriter writer = new BufferedWriter(fileWriter);
            writer.write("Sequence File,Reference File,Total Wall Time,Total CPU Time,Max RAM Usage,Average RAM Usage,Max Temperature,Average Temperature\r\n");
            analyticValues.add(((Global)this.getApplicationContext()).getSequenceFilename()); //0
            analyticValues.add(((Global)this.getApplicationContext()).getReferenceFilename()); //1
            initialCPUTime = ((Global) this.getApplicationContext()).getCpuTime();
            System.out.println("Initial cpu time: "+initialCPUTime/1000+"s");

            while (((Global) this.getApplicationContext()).mapperIsRunning())
            {
                currentRam = (float) (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory());
                if (maxRam < currentRam) {
                    maxRam = currentRam;
                }
                totalRam += currentRam;
                currentTemp = getCPUTemperature();
                if (maxTemp < currentTemp) {
                    maxTemp = currentTemp;
                }
                totalTemp += currentTemp;
                counter++;
                Thread.sleep(3000);

                endTime = System.currentTimeMillis();
                elapsedTime = endTime-startTime;

                System.out.println("Analytics read #"+ counter +". Elapsed time: " + elapsedTime/1000 + "s, Current ram: "+ currentRam/(1024*1024)+"MB" +", Max ram: "+ maxRam/(1024*1024)+"MB" + ", Current temp: "+ currentTemp +", Max temp: "+ maxTemp);
            }

            finalCPUTime = ((Global) this.getApplicationContext()).getCpuTime();
            System.out.println("Final cpu time: "+finalCPUTime/1000+"s");
            totalCPUTime = finalCPUTime - initialCPUTime;
            totalRam = totalRam/counter;
            totalTemp = totalTemp/counter;

            endTime = System.currentTimeMillis();
            elapsedTime = endTime-startTime;
            analyticValues.add(""+elapsedTime/1000); //2
            //analyticValues.add(""+elapsedTime/1000); //3
            analyticValues.add(""+totalCPUTime/1000); //3
            analyticValues.add(""+maxRam/(1024*1024)+"MB"); //4
            analyticValues.add(""+totalRam/(1024*1024)+"MB"); //5
            analyticValues.add(""+maxTemp); //6
            analyticValues.add(""+totalTemp); //7

            for (int i = 0; i < 7; i++) {
                writer.write(analyticValues.get(i)+",");
            }
            writer.write(analyticValues.get(7)+"\r\n");
            writer.close();
            System.out.println("Final analytics: Elapsed time: " + elapsedTime/1000 + "s, Cpu time: " + totalCPUTime/1000 + "s, Max ram: "+ maxRam/(1024*1024)+"MB" +", Average ram: "+ totalRam/(1024*1024)+"MB" + ", Max temp: "+ maxTemp +", Average temp: "+ totalTemp);

            System.out.println("ANALYTICS FINISHED");
            return Result.success();
        }
        catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return Result.failure();
        }
    }

    @NonNull
    private ForegroundInfo createForegroundInfo() {

        Context context = getApplicationContext();
        String id = "2";
        String title = "Analytics";

        /*
        String cancel = context.getString(R.string.cancel_download);
        // This PendingIntent can be used to cancel the worker
        PendingIntent intent = WorkManager.getInstance(context)
                .createCancelPendingIntent(getId());
         */

        createChannel(id);

        Notification notification = new NotificationCompat.Builder(context,id)
                .setContentTitle(title)
                .setTicker(title)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                //.setSmallIcon(R.drawable.ic_work_notification)
                .setOngoing(true)
                // Add the cancel action to the notification which can
                // be used to cancel the worker
                //.addAction(android.R.drawable.ic_delete, cancel, intent)
                .build();
        return new ForegroundInfo(Integer.parseInt(id),notification);
    }

    private void createChannel(String id) {
        CharSequence name = "OctopusApp - Analytics";
        String description = "Analytics Process";
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel channel = new NotificationChannel(id, name, importance);
        channel.setDescription(description);
        NotificationManager notificationManager = getApplicationContext().getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
    }

    public static float getCPUTemperature()
    {
        Process process;
        try {
            process = Runtime.getRuntime().exec("cat sys/class/thermal/thermal_zone0/temp");
            process.waitFor();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = reader.readLine();
            if(line!=null) {
                float temp = Float.parseFloat(line);
                return temp / 1000.0f;
            }else{
                return 51.0f;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return 0.0f;
        }
    }
}

