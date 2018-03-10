package metamapWrapper;

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

public class WrapperApp {
	
	
	private static Options options;

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
				.desc("Is the input file with three columns including patientID, clinical narrative and disease code (for training)")
				.build();
		Option output = Option.builder("output")
				.argName("eavFileName")
				.valueSeparator(' ')
				.numberOfArgs(2)
				.desc("The name of output file in EAV format. Default is the same as input with a modifier \"_eav\"")
				.build();
		Option patientCol = Option.builder("patient")
				.argName("columnNumber")
				.valueSeparator(' ')
				.numberOfArgs(2)
				.desc("The column number where the patient ID is located. Default is 0")
				.build();
		Option discretizeCol = Option.builder("narrative")
				.argName("columnNumber")
				.valueSeparator(' ')
				.numberOfArgs(2)
				.desc("The column number where the narrative text is located. Default is 1")
				.build();
		Option diseaseCol = Option.builder("disease")
				.argName("columnNumber")
				.valueSeparator(' ')
				.numberOfArgs(2)
				.desc("The column number where the disease or top level code is located (only for training). Default is 2")
				.build();

		// add default option
		options.addOption(help);
		options.addOption(version);

		//add custom options
		options.addOption(input);
		options.addOption(output);
		options.addOption(patientCol);
		options.addOption(discretizeCol);
		options.addOption(diseaseCol);
		

	}


	public static void main(String[] args){
		
		//String inputFile = "/Users/arturolp/Documents/Stanford/CNT-MetaMap/data/mimic_small.csv";
		//String eavFile = "/Users/arturolp/Documents/Stanford/CNT-MetaMap/data/mimic_small_eav.csv";
		
		createOptions();
		
		// Parsing the command line arguments
				CommandLineParser parser = new DefaultParser();
				try {
					CommandLine line = parser.parse( options, args);
					
					if (line.hasOption("help")) {
			            HelpFormatter formatter = new HelpFormatter();
			            
			            formatter.printHelp("HELP:", options, true);
			        }
					else {
						
						if (line.hasOption("input")) {
				            HelpFormatter formatter = new HelpFormatter();
				            
				            formatter.printHelp("HELP:", options, true);
				        }
						
					}
					
					
				} catch (ParseException exp) {
					System.err.println( "Parsing failed.  Reason: " + exp.getMessage() );
				}

		//NoteTagger nt = new NoteTagger();
		//nt.callMetaMap(inputFile, eavFile, 0, 1);



	}
	


}
