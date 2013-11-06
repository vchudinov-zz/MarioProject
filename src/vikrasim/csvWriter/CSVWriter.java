package vikrasim.csvWriter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.HashSet;

/** Right now everything seems to be working on the level of a single organism. 
 * In order to be able to bunch everything in one file We need to tweek the NEAT
 * so that each gene gets an identifier that tells it which organism does it belong to.
 * 
 * The method that you need to invoke is Writer. It takes as an argument the address to the genome file 
 * 
 * 
 * @author Viktor
 *
 */
public class CSVWriter {
	String filename;
	
	HashMap<Integer, HashSet<String>> genome;
	
	//HashMap<Integer, HashMap<Integer, HashSet<Integer>>> complete;
	
	public CSVWriter()
	{	genome = new HashMap<Integer, HashSet<String>>();
		//complete = new HashMap<Integer, HashMap<Integer, HashSet<Integer>>>();
	}
	
	/** Iterates trough all the individuals in the population or generation and creates 
	 *  a new .CSV file for each of them. 
	 * 
	 * @param Genome - the address of the population file
	 * @throws IOException
	 */
	public void Writer(String population) throws IOException
	{	this.filename = population;
		parseGenome();
		//writeGenomeToSingleFile();
		writeGenome("fakefilenameaaaaaaa");
	}
	
	/** You can use this method to create the file for a single organism, say a champion.
	 *  Not tested yet. 
	 * 
	 * @param File - the file that contains the genes of the organism
	 * @param file_ID - a number that goes in the filename. 
	 * 					Be sure it is bigger than the number of genes in the 
	 * 					population so you don't accidentaly overwrite a genome 
	 * @throws IOException
	 */
	public void WriterOfOne(String File) throws IOException
	{	HashMap<Integer,HashSet<Integer>> toWrite = new HashMap<Integer,HashSet<Integer>>();
		this.filename = File;
		writeGene(File, parseGeneFromSingleFile(toWrite));
	}	
	
	
	/**Writes the entire population to a file. Cool heh?
	 * 
	 * @throws IOException
	 */
	private void writeGenome(String file) throws IOException
	{	for(int key: genome.keySet() )
		{	HashMap<Integer,HashSet<Integer>> toWrite = new HashMap<Integer,HashSet<Integer>>();
			for (String val: genome.get(key))
				{	parseGene(val, toWrite);
				}
			writeGene(file, toWrite);
		}
		
	}
	/**Parses a population or generation file. 
	 * 
	 * @throws IOException
	 */
	private void parseGenome() throws IOException
	{		BufferedReader r = new BufferedReader(new InputStreamReader(
												  new FileInputStream(filename), "UTF-8"));
			int gene = 0;
			while(true)
			{	String s = r.readLine();
				if(s == null)
				{ break;
				}
				
				if(s.startsWith("genomeend"))
				{ gene++;
				}
				
				if(!genome.containsKey(gene))
				{ genome.put(gene, new HashSet<String>());
				}
				
				if(s.startsWith("gene"))
				{ genome.get(gene).add(s);
				}
			}
		
	}
	
	/** Parses a single organism. 
	 * 
	 * @param toWrite
	 * @return
	 * @throws IOException
	 */
	private HashMap<Integer,HashSet<Integer>> parseGeneFromSingleFile(HashMap<Integer,HashSet<Integer>> toWrite) throws IOException
	{	
		BufferedReader r = new BufferedReader(new InputStreamReader(
												new FileInputStream(filename), "UTF-8"));
		while(true)
		{	String s = r.readLine();
		if(s == null)
		{	break;				
		}
		if (s.startsWith("gene"))
		{	String[] array = s.split("\\W");
			int key = Integer.parseInt(array[3]);
			int value = Integer.parseInt(array[4]);
			if(!toWrite.containsKey(key))
			{ toWrite.put(key, new HashSet<Integer>());
			}
			toWrite.get(key).add(value);
		}
		}
		
			
		return toWrite;
	}
	
	/** Parses the genes of a single organism when part of a larger population. 
	 * It is basically the same as above
	 * 
	 * @param s - the string that is to be parsed.
	 * @param toWrite - the hashmap where it gets written
	 * @throws IOException
	 */
	private void parseGene(String s, HashMap<Integer,HashSet<Integer>> toWrite) throws IOException
	{	String[] line = s.split("\\n");
			
		for(String word: line)
		{ String[] array = word.split("\\W");
			int key = Integer.parseInt(array[3]);
			int value = Integer.parseInt(array[4]);
			if(!toWrite.containsKey(key))
			{ toWrite.put(key, new HashSet<Integer>());
			}
			toWrite.get(key).add(value);
		}
	}
	
	/** Writes the parsed genes to a .csv. The .csv then is used in Gephi(or other software?)
	 *  to generate a graph.
	 *  Maybe it will benefit from adding a way to reflect the generation of each organism
	 * 
	 * @param number - the number ID. In case that this is part of the population it writes the number of the gene
	 * @param toWrite - the hashMap that supplies the values to be written
	 * @throws IOException
	 */
	private void writeGene(String file, HashMap<Integer, HashSet<Integer>> toWrite) throws IOException
	{ 	String dir = new File("").getAbsolutePath() + "/Generationdata/";
		FileWriter fw = new FileWriter(file + ".csv");
    	PrintWriter pw = new PrintWriter(fw);
    	
    	for(int key: toWrite.keySet())
    	{	pw.print(key);
    		pw.print(";");
    		for(int val: toWrite.get(key))
    		{	pw.print(val);
    			pw.print(";");
    		}
    		pw.println();
    	}
    	//Flush the output to the file
        pw.flush();
        
        //Close the Print Writer
        pw.close();
        
        //Close the File Writer
        fw.close();   
	}
	
	/** Does not work properly. The idea is to put all genes in a single file.
	 * However we need to add a unique identifier before each value, because of the way
	 * Gephi works - it does not accept duplicate names for nodes. 
	 * 
	 * @throws IOException
	 */
//	private void writeGenomeToSingleFile() throws IOException
//	{	for(int key: genome.keySet() )
//		{	complete.put(key, new HashMap<Integer, HashSet<Integer>>());
//			for (String val: genome.get(key))
//			{	parseGene(val, complete.get(key));
//			}
//		}
//		
//		writeToSingleFile("completeGenome", complete);
//	}
	/**See above. 
	 * 
	 * @param fileName
	 * @param toWrite
	 * @throws IOException
	 */
//	private void writeToSingleFile(String fileName, HashMap<Integer, HashMap<Integer, HashSet<Integer>>> toWrite) throws IOException
//	{	String dir = new File("").getAbsolutePath() + "/";
//		FileWriter fw = new FileWriter( dir + fileName + ".csv");
//		PrintWriter pw = new PrintWriter(fw);
//	
//		for(int key: toWrite.keySet())
//		{	for(int gene: toWrite.get(key).keySet())
//			{	
//				pw.print(Integer.toString(key) + gene);
//				pw.print(";");
//				for(int val: toWrite.get(key).get(gene))
//				{	pw.print(Integer.toString(key) + Integer.toString(gene) + val);
//					pw.print(";");
//				}
//				pw.println();
//			}
//		}
//	//Flush the output to the file
//    pw.flush();
//    
//    //Close the Print Writer
//    pw.close();
//    
//    //Close the File Writer
//    fw.close();  
//		
//	}
}
