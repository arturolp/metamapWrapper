package metamapWrapper;

import java.io.File;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;

/*
 * 
 * Project: Clinical Note Tagger
 * 
 * author: Arturo Lopez Pineda
 * 
 * Mar 7, 2018
 * 
 */


import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.FilenameUtils;

public class WrapperApp {
	
	
	private static Options options;
	public static String WrapperVersion = "1.0";
	public static String MetaMapVersion = "Lite 3.6.1";
	public static String CLIVersion = "1.4";
	public static String IOVersion = "2.6";
	public static String OpenCSVVersion = "4.1";

	public static void createOptions() {
		// Create Options object
		options = new Options();
		
		// Help options
		Option help = new Option( "help", "Print this message" );
		Option version = new Option( "version", "Print the version information and exit" );

		// App specific options
		Option input = Option.builder("input")
				.argName("inputFile")
				.hasArg()
				.required(true)
				.desc("Is the input file with three columns including patientID, clinical narrative and disease code (for training)")
				.build();
		Option separator = Option.builder("sep")
				.argName("character")
				.hasArg()
				.desc("Is the character that separates fields in the file. Default is comma. Other possible values are [tab, colon, semicolon]")
				.build();
		Option metamapData = Option.builder("mmData")
				.argName("folderPath")
				.hasArg()
				.desc("The path to the folder where MetaMap DATA is located. Default is the same path as input under metamap/")
				.build();
		Option metamapConfig = Option.builder("mmConfig")
				.argName("folderPath")
				.hasArg()
				.desc("The path to the folder where MetaMap CONFIG is located. Default is the same path as input under metamap/")
				.build();
		Option output = Option.builder("output")
				.argName("eavFileName")
				.hasArg()
				.desc("The name of output file in EAV format. Default is the same as input with a modifier \"_eav\"")
				.build();
		Option encounterCol = Option.builder("encounter")
				.argName("columnNumber")
				.hasArg()
				.desc("The column number where the encounter ID is located. Default is 0")
				.build();
		Option narrativeCol = Option.builder("narrative")
				.argName("columnNumber")
				.hasArg()
				.desc("The column number where the narrative text is located. Default is 1")
				.build();
		Option diseaseCol = Option.builder("disease")
				.argName("columnNumber")
				.hasArg()
				.desc("The column number where the disease or top level code is located (only for training). Default is 2")
				.build();

		// add default option
		options.addOption(help);
		options.addOption(version);

		//add custom options
		options.addOption(input);
		options.addOption(separator);
		options.addOption(metamapData);
		options.addOption(metamapConfig);
		options.addOption(output);
		options.addOption(encounterCol);
		options.addOption(narrativeCol);
		options.addOption(diseaseCol);
		

	}


	public static void main(String[] args){
		
		//String inputFile = "/Users/arturolp/Documents/Stanford/CNT-MetaMap/data/mimic_small.csv";
		//String eavFile = "/Users/arturolp/Documents/Stanford/CNT-MetaMap/data/mimic_small_eav.csv";
		
		createOptions();
		
		//Default values
		String inputFile = "";
		String metamapData = "";
		String metamapConfig = "";
		/*String[] semanticTypes = {"blor", "bpoc", "bsoj", "chem", "clnd", "diap", "dsyn", "fndg", "lbpr", "lbtr", "medd", 
				"neop",	"orgm", "comd", "fngs", "bact", "sbst", "sosy", "tisu", "topp", "virs", "vita"};*/
		String outputFile = "";
		int encounterCol = 0;
		int narrativeCol = 1; 
		int diseaseCol = 2;
		String targetName = "class";
		String splitMarker = "-";
		char separator = ',';
		
		// Parsing the command line arguments
				CommandLineParser parser = new DefaultParser();
				try {
					CommandLine line = parser.parse( options, args);
					
					if (line.hasOption("help")) {
			            HelpFormatter formatter = new HelpFormatter();
			            
			            formatter.printHelp("HELP:", options, true);
			        }
					else if (line.hasOption("version")) {
						System.out.println("=====================================");
			            System.out.println("Wrapper version: \t" + WrapperVersion);
			            System.out.println("=====================================");
			            System.out.println("\nUsing the following packages>");
			            System.out.println("\tNLM MetaMap version: " + MetaMapVersion);
			            System.out.println("\tApache Commons CLI version: " + CLIVersion);
			            System.out.println("\tApache Commons IO version: " + IOVersion);
			            System.out.println("\tOpenCSV version: " + OpenCSVVersion);
			            System.out.println("\nGet current version at: \n\thttps://github.com/bustamante-lab/metamapWrapper");
			        } 
					else if (line.hasOption("input")) {
							System.out.println("=====================================");
				            System.out.println("Running MetaMap Wrapper");
				            System.out.println("=====================================\n");
				           
				            //Read input filename
				            	inputFile = line.getOptionValue("input");
				            	System.out.println("Reading input file: " + inputFile);
				            	
				            	
				            	//Read separator character
				            	String sepCode = "comma";
				            	if (line.hasOption("sep")) {
				            		sepCode = line.getOptionValue("sep");
				            		if(sepCode.equals("tab")){
				            			separator='\t';
				            		}
				            		else if(sepCode.equals("tab")){
				            			separator='\t';
				            		}
				            		else if(sepCode.equals("colon")){
				            			separator=':';
				            		}
				            		else if(sepCode.equals("semicolon")){
				            			separator=';';
				            		}
				            	}
				            	System.out.println("Input file separator character: ´" + separator+ "´ ["+sepCode+"]");
				            	
				            	
				            	//Read metamap DATA path
				            	if (line.hasOption("mmData")) {
				            		metamapData = line.getOptionValue("mmData");   	
				            	}
				            	else {
				            		File file = new File(inputFile);
				            		String path = FilenameUtils.getFullPath(file.getPath());
				            		metamapData = path + "data/";
				            	}
				            	System.out.println("Metamap data folder: " + metamapData);
				            	
				            	
				            	//Read metamap CONFIG path
				            	if (line.hasOption("mmConfig")) {
				            		metamapConfig = line.getOptionValue("mmConfig");
				            	}
				            	else {
				            		File file = new File(inputFile);
				            		String path = FilenameUtils.getFullPath(file.getPath());
				            		metamapConfig = path + "config/";
				            	}
				            	System.out.println("Metamap config folder: " + metamapConfig);
				            	
				            	//Read output filename
				            	if (line.hasOption("output")) {
				            		outputFile = line.getOptionValue("output");   	
				            	}
				            	else {
				            		File file = new File(inputFile);
				            		String filename = file.getName();
				            		String path = FilenameUtils.getFullPath(file.getPath());
				            		String base = FilenameUtils.removeExtension(filename);
				            		String extension = FilenameUtils.getExtension(filename);
				            		outputFile = path + base + "_eav" + "." + extension;
				            	}
				            	System.out.println("Output file to write: " + outputFile);
				            	
				            	
				            	//Read patientCol
				            	if (line.hasOption("encounter")) {
				            		encounterCol = Integer.parseInt(line.getOptionValue("encounter"));
				            	}
				            	System.out.println("Patient information in column: " + encounterCol);
				            	
				            	//Read discretizeCol
				            	if (line.hasOption("narrative")) {
				            		narrativeCol = Integer.parseInt(line.getOptionValue("narrative"));
				            	}
				            	System.out.println("Narrative information in column: " + narrativeCol);
				            	
				            	//Read diseaseCol
				            	if (line.hasOption("disease")) {
				            		diseaseCol = Integer.parseInt(line.getOptionValue("disease"));
				            	}
				            	System.out.println("Disease (target) information in column: " + diseaseCol);
				            	
				            	//-------------------------------
				            	//Calling MetaMap NoteTagger
				            	//-------------------------------
				            	System.out.println("\nRunning MetaMap...");
				            	NoteTagger nt = new NoteTagger();
				        		nt.callMetaMap(inputFile, separator, metamapData, metamapConfig, outputFile, encounterCol, narrativeCol, diseaseCol,targetName, splitMarker);
				            	
				        }
					else {
						//Print help
						HelpFormatter formatter = new HelpFormatter();
			            formatter.printHelp("\nHELP:", options, true);
					}
						
					
					
					
				} catch (ParseException exp) {
					System.err.println( "Parsing failed.  Reason: " + exp.getMessage() );
					
					//Print help
					HelpFormatter formatter = new HelpFormatter();
		            formatter.printHelp("\nHELP:", options, true);
				}

		



	}
	


}
