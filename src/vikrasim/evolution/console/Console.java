package vikrasim.evolution.console;

import jNeatCommon.IOseq;

import java.io.File;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class Console {
	//You don't necessarily have to change these values
	String nameOfExperiment;
	int maxNumberOfGenerations;
	boolean stopOnFirstGoodOrganism ;
	double errorThreshold ;
	//Folders
		String dataFolder;
		String generationInfoFolder;
		String winnerFolder;
				
		//Filenames
		String parameterFileName;
		String debugParameterFileName;
				
		String genomeFileName ;
		String genomeBackupFileName;
				
		String lastPopulationInfoFileName;
		
		String inputFileNameTraining;
		String outputFileNameTraining;
		
		String inputFileNameTesting;
		String outputFileNameTesting;
		
		public Console(String nameOfExperiment, int maxNumberOfGenerations, boolean stopOnFirstGoodOrganism, double errorThreshold){
			this.nameOfExperiment = nameOfExperiment;
			this.maxNumberOfGenerations = maxNumberOfGenerations;
			this.stopOnFirstGoodOrganism = stopOnFirstGoodOrganism;
			this.errorThreshold = errorThreshold;
			
			//Folders
			dataFolder = new File("").getAbsolutePath() + "\\NEAT data";
			generationInfoFolder = dataFolder + "\\Training data\\" + nameOfExperiment + "\\Generation Info";
			winnerFolder = dataFolder + "\\Training data\\" + nameOfExperiment + "\\Winners";
					
			//Filenames
			parameterFileName = dataFolder + "\\parameters";
			debugParameterFileName = dataFolder + "\\parameters.imported";
					
			genomeFileName = dataFolder + "\\Training data\\" + nameOfExperiment + "\\starterGenome.txt";
			genomeBackupFileName = dataFolder + "\\starterGenome.read" ;
					
			lastPopulationInfoFileName = dataFolder + "\\population.LastGeneration";
			
			inputFileNameTraining = dataFolder + "\\Training data\\" + nameOfExperiment + "\\Training\\inputvalues.txt";
			outputFileNameTraining= dataFolder + "\\Training data\\" + nameOfExperiment + "\\Training\\outputvalues.txt";
			
			inputFileNameTesting = dataFolder + "\\Training data\\" + nameOfExperiment + "\\Testing\\inputvalues.txt";
			outputFileNameTesting= dataFolder + "\\Training data\\" + nameOfExperiment + "\\Testing\\outputvalues.txt";
		}
		
		protected static double[][] readValues(String filename){
			boolean ret = true; 
			 String xline;
			 IOseq xFile;
			 StringTokenizer st;
			 String s1;
			 double[][] result = null;
			 
			 
			 ArrayList<ArrayList<Double>> values = new ArrayList<>();
			 
			 	  
			 xFile = new IOseq(filename);
			 ret = xFile.IOseqOpenR();
			 if (ret) {
				try {
					xline = xFile.IOseqRead();
					int lineNumber = 0;
					while ( xline != "EOF"){
						ArrayList<Double> lineValues = new ArrayList<>();
						values.add(lineValues); 
						st = new StringTokenizer(xline);					
						while (st.hasMoreTokens()){
							s1 = st.nextToken();
							double value = Double.parseDouble(s1);
							values.get(lineNumber).add(value);					
						}
						xline = xFile.IOseqRead();
						lineNumber++;
					}
				} 
					catch (Throwable e) {
					  System.err.println(e);
				   }
			 
				xFile.IOseqCloseR();
				result = new double[values.size()][values.get(0).size()];
				for (int i = 0; i < values.size(); i++){
					for (int j = 0; j < values.get(0).size(); j++){
						result[i][j] = values.get(i).get(j);
					}
				}
			 
			 } else{
				System.err.print("\n : error during open " + filename);
			 }
			 
			 return result; 
		  }            
}
