package metamapWrapper;

/*
 * 
 * Project: Clinical Note Tagger
 * 
 * author: Arturo Lopez Pineda
 * 
 * Mar 7, 2018
 * 
 */


import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;


import au.com.bytecode.opencsv.CSVReader;
import bioc.BioCDocument;
import gov.nih.nlm.nls.metamap.document.FreeText;
import gov.nih.nlm.nls.metamap.lite.types.Entity;
import gov.nih.nlm.nls.metamap.lite.types.Ev;
import gov.nih.nlm.nls.ner.MetaMapLite;

public class NoteTagger {



	public NoteTagger() {

	}

	public void callMetaMap(String inputFile, char separator, String metamapData, String metamapConfig,
			String eavFile, int patientIDcolumn, int clinicalNarrativeColumn, int diseaseTargetColumn,
			String targetName, String splitMarker) {
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
			try (CSVReader reader = new CSVReader(new FileReader(inputFile), separator)) {

				String tokens[];
				String umls = "Entity\tAttribute\tDescription\tMatchedText\tSemanticType\tValue\n";
				bw.append(umls);
				

				int lineCounter = 0;
				int totalLines = 0;

				
				while ((tokens = reader.readNext()) != null) {
					umls = getUMLScodes(metamapData, metamapConfig, tokens[patientIDcolumn], tokens[clinicalNarrativeColumn]);

					//Add patient CUIs
					bw.append(umls);
					
					//Add top-level category
					String category = getCategory(tokens[patientIDcolumn], targetName, tokens[diseaseTargetColumn], splitMarker);
					bw.append(category);

					//System.out.print(umls);
					System.out.print(".");
					lineCounter++;
					totalLines++;
					if(lineCounter > 15) {
						System.out.println(" ["+totalLines+"]");
						lineCounter = 0;
					}

				}
				System.out.println(" ["+totalLines+"]");
				
				System.out.println("\n[DONE]");
			} catch(IOException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			} catch (Exception e) {
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

	private String getCategory(String patient, String targetName, String disease, String splitMarker) {
		String category = "";
		
		if (disease.contains(splitMarker)) {
			String[] parts = disease.split(splitMarker);
			
			for(int i = 0; i < parts.length; i++) {
				category = category + patient + "\t" + targetName + "_" + parts[i] + "\t\t\t\t"+ "true" + "\n";
				//System.out.print(category);
			}
		}
		else {
		category = patient + "\t" + targetName + "_"+ disease + "\t\t\t\t"+ "true" + "\n";
		//System.out.print(category);
		}
		
		return category;
	}

	//GET ALL TOKENS
	/*private List<String[]> getTokens(String csvFile){

		List<String[]> tokens = new ArrayList<String[]>();


		try (CSVReader reader = new CSVReader(new BufferedReader(new FileReader(csvFile)))) {

			tokens = reader.readAll();

		} catch(IOException e) {
			e.printStackTrace();
		}

		return(tokens);

	}*/


	private String getUMLScodes(String metamapData, String metamapConfig, String patient, String narrative) throws IllegalAccessException, InvocationTargetException, IOException, Exception {

		String umls = "";

		// Creating Properties for MetaMapLite
		Properties myProperties = new Properties();
		String models = metamapData + "models/";
		String strict = metamapData + "ivf/2017AA/Base/strict/";
		String specialterms = metamapData + "specialterms.txt";
				
		MetaMapLite.expandModelsDir(myProperties, models);
		MetaMapLite.expandIndexDir(myProperties, strict);
		myProperties.setProperty("metamaplite.excluded.termsfile", specialterms);

		// Load properties file in the config folder
		try {
			String properties = metamapConfig + "metamaplite.properties";
			myProperties.load(new FileReader(properties));
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

		

		//Extract the codes
		List<Entity> entityList = myMM.processDocumentList(documentList);
		for (Entity entity: entityList) {
			
			for (Ev ev: entity.getEvSet()) {
				
				boolean entityValue = !entity.isNegated();

					umls = umls + patient + "\t" + 
							ev.getConceptInfo().getCUI() + "\t"+ 
							ev.getConceptInfo().getPreferredName() + "\t" + 
							ev.getMatchedText() + "\t" + 
							ev.getConceptInfo().getSemanticTypeSet() +"\t" +
							entityValue + "\n";
				
			}
		}

		return(umls);
	}


}
