# OctopusMobile

Main code repository for the OctopusMobile system.

## Usage

Currently, system requires for the sequence (.fastq) and database files to be in the app's external storage in phone at location 
```
/Internal storage/Android/data/org.ruizlab.phoni.octopusapp/files
``` 

## Configurations

### Interface Enabled

User can select exitsing database or can access a different database by selecting 'Other database' and entering the folder name 
User can select exitsing sequence file or can access a different sequence file by selecting 'Other Sequence File' and entering the file name 

### Code Enabled

In the Global class, testing features and analytics can be enabled/disabled.

## Functionality

<>  
Once completed, the system stores a .CSV file with the results in the default app folder (/android/data/org.ruizlab.phoni.octopusapp/).  
 
When activated, the system executes another foreground thread with analytics, recording total wall/CPU time, max/average RAM, and max/average temperature. Analytics results are stored in a separate .CSV file in the same default app folder.  

## Support

Contact a.barquero@ruizlab.org

