# metamapWrapper

A java wrapper to extract CUI identifiers from medical notes using MetaMap

![GitHub Logo](/MMWrapper.png)

## Description
Medical trajectories of the cohort: Each patient is represented by a rectangle, with rows representing each year of follow-up treatment. Events are color-coded according to the type of event, and the length of the bar represents the duration between the occurrence of an event and the event that preceded it.

## Citation
For citation and more information refer to:

>A.M. Zehnder et al. "Automatic tagging of clinical narratives to top-level categories of diseases facilitates cross-species cohort identification" (under development).


## Current development status
This package is in development by Arturo Lopez Pineda, PhD (arturolp @ stanford.edu)


## Usage

Expected commands format: 
```
java -jar metamapWrapper.jar -input data.txt -metamapData data/ -metamapConfig config/ [-output output.txt] [-patient 0] [-narrative 1] [-disease 2]
```

|Parameter|Argument|Example|Description|
|-------------|-------------|-------------|-------------|
| -input | *\<inputFile>* | data.txt | Is the input file with three columns including patientID, clinical narrative and disease code (for training)|
| -metamapData | *\<folderPath>* | data/ | The path to the folder where MetaMap DATA is located. Default is the same path as input under config/ |
| -metamapConfig | *\<folderPath>* | config/ | The path to the folder where MetaMap CONFIG is located. Default is the same path as input under data/ |
| -output | *\<eavFileName>* | output.txt | The name of output file in EAV format. Default is the same as input with a modifier "_eav" |
| -patient | *\<columnNumber>* | 0 | The column number where the patient ID is located. Default is 0|
| -narrative | *\<columnNumber>* | 1 | The column number where the narrative text is located. Default is 1 |
| -disease | *\<columnNumber>* | 2 | The column number where the disease or top level code is located (only for training). Default is 2 |

## MetaMap considerations
metamapWrapper is only looking for the following semantic type CUIs: 
```
blor, bpoc, bsoj, chem, clnd, diap, dsyn, fndg, lbpr, lbtr, medd, neop,	orgm, comd, fngs, bact, sbst, sosy, tisu, topp, virs, vita
```

For a full list of semantic types and descriptions, please refer to:
https://metamap.nlm.nih.gov/Docs/SemanticTypes_2013AA.txt
