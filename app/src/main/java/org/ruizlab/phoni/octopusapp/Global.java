// -----------------------------------------------------------------------
// <copyright file="ForegroundAnalytics.java" company="Ruiz HCI Lab">
// Copyright (c) Ruiz HCI Lab. All rights reserved.
// Licensed under the MIT license. See LICENSE file in the repository root for full license information.
// </copyright>
// -----------------------------------------------------------------------

package org.ruizlab.phoni.octopusapp;

import android.app.Application;
import android.net.Uri;

import java.util.ArrayList;

public class Global extends Application {

    //Default value of k is set to 17
    private int kValue = 17;
    private float coverageValue = 80;
    private ArrayList<String> finalGeneList;
    private ArrayList<String[]> finalComparisonGeneList;
    private ArrayList<String> fileList;
    private Uri mappedGenesUri;
    private Boolean interfaceTestActivated = false;
    private Boolean mapperIsRunning = false;
    private Boolean analyticsStatus = true;
    private String sequenceFilename = "";
    private String referenceFilename = "";
    private String firstCompareFilename = "";
    private String secondCompareFilename = "";
    private String firstFileName = "";
    private int firstFileCountValue;
    private String secondFileName = "";
    private int secondFileCountValue;
    private long cpuTime;
    private int classesToShow = 10;
    private int fileIdSelected;
    private Boolean compareFileSelection = false;
    private int currentActiveFile = 0;


    public int getKValue() {
        return kValue;
    }
    public void setKValue(int kValue) {
        this.kValue = kValue;
    }

    public float getCoverageValue() { return coverageValue; }
    public void setCoverageValue(float coverageValue) { this.coverageValue = coverageValue; }

    public ArrayList<String> getFinalGeneList() {
        return finalGeneList;
    }
    public void setFinalGeneList(ArrayList<String> finalGeneList) { this.finalGeneList = finalGeneList; }

    public ArrayList<String[]> getFinalComparisonGeneList() {
        return finalComparisonGeneList;
    }
    public void setFinalComparisonGeneList(ArrayList<String[]> finalComparisonGeneList) { this.finalComparisonGeneList = finalComparisonGeneList; }

    public ArrayList<String> getFileList() {
        return fileList;
    }
    public void setFileList(ArrayList<String> fileList) { this.fileList = fileList; }

    public Uri getMappedGenesUri() {
        return mappedGenesUri;
    }
    public void setMappedGenesUri(Uri mappedGenesUri) {
        this.mappedGenesUri = mappedGenesUri;
    }

    public Boolean getInterfaceTestActivated() {
        return interfaceTestActivated;
    }
    public void activateInterfaceTests() {
        this.interfaceTestActivated = Boolean.TRUE;
    }
    public void deactivateInterfaceTests() { this.interfaceTestActivated = Boolean.FALSE; }

    public Boolean mapperIsRunning() {
        return mapperIsRunning;
    }
    public void mapperStarts() {
        this.mapperIsRunning = Boolean.TRUE;
    }
    public void mapperStops() {
        this.mapperIsRunning = Boolean.FALSE;
    }

    public Boolean analyticsAreEnabled() {return analyticsStatus;}
    public void enableAnalytics() {
        this.analyticsStatus = Boolean.TRUE;
    }
    public void disableAnalytics() {
        this.analyticsStatus = Boolean.FALSE;
    }

    public String getSequenceFilename() {
        return sequenceFilename;
    }
    public void setSequenceFilename(String sequenceFilename) { this.sequenceFilename = sequenceFilename; }

    public String getReferenceFilename() {
        return referenceFilename;
    }
    public void setReferenceFilename(String referenceFilename) { this.referenceFilename = referenceFilename; }

    public long getCpuTime() {
        return cpuTime;
    }
    public void setCpuTime(long cpuTime) {
        this.cpuTime = cpuTime;
    }

    public int getClassesToShow() {
        return classesToShow;
    }
    public void setClassesToShow(int classesToShow) {
        this.classesToShow = classesToShow;
    }

    public int getFileIdSelected() {
        return fileIdSelected;
    }
    public void setFileIdSelected(int fileIdSelected) { this.fileIdSelected = fileIdSelected; }

    public Boolean compareFilesAreSelected() {
        return compareFileSelection;
    }
    public void compareFileSelected() {
        this.compareFileSelection = Boolean.TRUE;
    }

    public int getCurrentActiveFile() {
        return currentActiveFile;
    }
    public void setCurrentActiveFile(int currentActiveFile) { this.currentActiveFile = currentActiveFile; }

    public String getFirstCompareFilename() { return firstCompareFilename; }
    public void setFirstCompareFilename(String firstCompareFilename) { this.firstCompareFilename = firstCompareFilename; }

    public String getSecondCompareFilename() {
        return secondCompareFilename;
    }
    public void setSecondCompareFilename(String secondCompareFilename) { this.secondCompareFilename = secondCompareFilename; }

    public String getFirstFileName() { return firstFileName; } //max 20chars
    public void setFirstFileName(String firstFileName) { this.firstFileName = firstFileName; }

    public String getSecondFileName() { return secondFileName; } //max 20chars
    public void setSecondFileName(String secondFileName) { this.secondFileName = secondFileName; }

    public int getFirstFileCountValue() {
        return firstFileCountValue;
    }
    public void setFirstFileCountValue(int firstFileCountValue) { this.firstFileCountValue = firstFileCountValue; }

    public int getSecondFileCountValue() {
        return secondFileCountValue;
    }
    public void setSecondFileCountValue(int secondFileCountValue) { this.secondFileCountValue = secondFileCountValue; }
}
