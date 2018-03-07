package metamapWrapper;

/*
 * author: Arturo Lopez Pineda
 * 
 * Mar 7, 2018
 * 
 */


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import au.com.bytecode.opencsv.CSVReader;
import bioc.BioCDocument;
import gov.nih.nlm.nls.metamap.document.FreeText;
import gov.nih.nlm.nls.metamap.lite.types.Entity;
import gov.nih.nlm.nls.metamap.lite.types.Ev;
import gov.nih.nlm.nls.ner.MetaMapLite;

public class NoteTagger {


	public static void main(String[] args) throws InvocationTargetException, Exception {
		String inputFile = "/Users/arturolp/Documents/Stanford/CNT-MetaMap/data/mimic_train.csv";
		String eavFile = "/Users/arturolp/Documents/Stanford/CNT-MetaMap/data/mimic_train_eav.csv";

		// Get all tokens at once
		//List<String[]> tokens = getTokens(csvFile);
		//System.out.println(tokens.get(0)[1]);

		BufferedWriter bw = null;
		FileWriter fw = null;

		// Try to append file
		try {


			fw = new FileWriter(eavFile);
			bw = new BufferedWriter(fw);
			bw.write("");

			// Try to read file
			try (CSVReader reader = new CSVReader(new BufferedReader(new FileReader(inputFile)))) {

				String tokens[];

				while ((tokens = reader.readNext()) != null) {
					String umls = getUMLScodes(tokens[2], tokens[6]);
					
					//Add patient CUIs
					bw.append(umls);

					System.out.print(umls);

				}
			} catch(IOException e) {
				e.printStackTrace();
			}
		}	catch (IOException e) {

			e.printStackTrace();

		} finally {

			try {

				if (bw != null)
					bw.close();

				if (fw != null)
					fw.close();

			} catch (IOException ex) {

				ex.printStackTrace();

			}

		}



	}

	public static List<String[]> getTokens(String csvFile){

		List<String[]> tokens = new ArrayList<String[]>();


		try (CSVReader reader = new CSVReader(new BufferedReader(new FileReader(csvFile)))) {

			tokens = reader.readAll();

		} catch(IOException e) {
			e.printStackTrace();
		}

		return(tokens);

	}


	public static String getUMLScodes(String patient, String narrative) throws IllegalAccessException, InvocationTargetException, IOException, Exception {

		String umls = "";

		// Creating Properties for MetaMapLite
		Properties myProperties = new Properties();
		MetaMapLite.expandModelsDir(myProperties, "data/models");
		MetaMapLite.expandIndexDir(myProperties, "data/ivf/strict");
		myProperties.setProperty("metamaplite.excluded.termsfile", "data/specialterms.txt");


		// Load properties file in the config folder
		try {
			myProperties.load(new FileReader("config/metamaplite.properties"));
		} catch (Exception e) {
			e.printStackTrace();
		}

		//Create a new MetaMap Instance
		MetaMapLite myMM = new MetaMapLite(myProperties);


		BioCDocument document = FreeText.instantiateBioCDocument(narrative);


		/*
		 * BioCDocument document = FreeText.instantiateBioCDocument("88 year old woman with hypertension, prior tobacco use, and\n" + 
				"dementia being treated for pneumonia now presenting with an\n" + 
				"episode of nausea and chest discomfort that has fully resolved,\n" + 
				"found to have STEMI on ECG and positive cardiac biomarkers.\n" + 
				"Given that she is pain free and hemodynamically stable the\n" + 
				"decision was made not to take her to the catheterization lab for\n" + 
				"coronary angiography at this time because of questionable\n" + 
				"long-term mortality benefit in this elderly, demented woman.\n" + 
				"The patient is a poor historian and it is unclear how long she\n" + 
				"may have been experiencing symptoms since her cardiac enzymes\n" + 
				"are already elevated at presentation.");
		 */
		List<BioCDocument> documentList = new ArrayList<BioCDocument>();
		documentList.add(document);



		//Provided by Ashley
		List<String> semanticTypes = Arrays.asList("blor", "bpoc", "bsoj", "chem", "clnd", "diap", "dsyn", "fndg", "lbpr", "lbtr", "medd", 
				"neop",	"orgm", "comd", "fngs", "bact", "sbst", "sosy", "tisu", "topp", "virs", "vita");

		//Extract the codes
		List<Entity> entityList = myMM.processDocumentList(documentList);
		for (Entity entity: entityList) {
			for (Ev ev: entity.getEvSet()) {

				if(semanticTypes.contains(ev.getConceptInfo().getSemanticTypeSet().toString().replace("[", "").replace("]", ""))) {
					//System.out.print(ev.getConceptInfo().getCUI() + "|" + entity.getMatchedText() + "|" + ev.getConceptInfo().getSemanticTypeSet());
					//System.out.println();
					//umls = umls + patient + "|" + ev.getConceptInfo().getCUI() + "|" + entity.getMatchedText() + "|" + ev.getConceptInfo().getSemanticTypeSet() +"\n";
					umls = umls + patient + "," + ev.getConceptInfo().getCUI() + ",T\n";
				}
			}
		}

		return(umls);
	}


}
