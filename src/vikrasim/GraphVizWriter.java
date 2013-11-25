package vikrasim;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.HashSet;

/** The method that you need to invoke is Writer. It takes as an argument the address to the genome file 
 * 
 * 
 * @author Viktor
 *
 */
public class GraphVizWriter {
	String filename;
	private enum layer {INPUT_LAYER, HIDDEN_LAYER, OUTPUT_LAYER};
	//HashMap<Integer, HashSet<String>> genome;
	HashSet<String> graph;
	String bias = "";
	HashSet<String> sanitizedInputs;
	HashSet<String> output;
	HashSet<String> input;
	HashSet<String> hidden;
		
	public GraphVizWriter()
	{	graph = new HashSet<String>();
		output = new HashSet<String>();
		input = new HashSet<String>();
		hidden = new HashSet<String>();
		sanitizedInputs = new HashSet<String>();
	}
	

	public void writer(String population,String targetfile) throws IOException
	{	this.filename = population;
		parseGene();
		writeGene(targetfile,4);
	}
	/**Parses a genome file
	 * 
	 * @throws IOException
	 */
	private void parseGene() throws IOException
	{		BufferedReader r = new BufferedReader(new InputStreamReader(
			  						new FileInputStream(filename), "UTF-8"));
			
			while(true)
			{	String line = r.readLine();
				if( line== null)
				{	break;					
				}
				String[] s = line.split("\\W"); 
				String identif = s[0];
				String node = s[1];
				
				int last = Integer.parseInt(s[s.length-1]);  
							
				if(identif.equals("node"))
				{	if(last == 0) hidden.add(node);
				  	else if(last == 1 || last == 3) input.add(node);
					if (last == 3) bias = node;
				  	else if(last == 2) output.add(node);
				}
				
				if(identif.equals("gene"))
				{	if (last != 0) graph.add(s[3] + " -> " + s[4]);
					sanitizedInputs.add(s[3]);
				}
			}
			input.retainAll(sanitizedInputs);
	}
	/**Writes the gene to a file
	 * 
	 * @param file - the name of the file. It will be automatically put in the relevant folder
	 * @param ranksep - how wide the layers will appear from each other in inches. 3-4 is good for now
	 * @throws IOException
	 */
	private void writeGene(String file, int ranksep) throws IOException
	{ 	String dir = new File("").getAbsolutePath() + "/Generationdata/";
		FileWriter fw = new FileWriter(file + ".txt");
    	PrintWriter pw = new PrintWriter(fw);
    	pw.println("digraph " + file + " {");
    	pw.println("splines = line\n ");
    	pw.println("ranksep = " + ranksep);
    	
    	layerWriter(pw,layer.INPUT_LAYER, input);
    	layerWriter(pw,layer.HIDDEN_LAYER, hidden);
    	layerWriter(pw,layer.OUTPUT_LAYER, output);
    	pw.println("edge [color = blue2, style = bold]");
		
    	//iterate and make the connections between the nodes
    	for(String s: graph)
    	{	pw.println(s);
    	}
    	
    	pw.print("}");
    	//Flush the output to the file
        pw.flush();
        
        //Close the Print Writer
        pw.close();
        
        //Close the File Writer
        fw.close();   
	}
	
	/**Writes down a single layer of the NN
	 * 
	 * @param pw - PrinterWriter deals with writing the information to file
	 * @param label - the label of the layer see the enum in this class.
	 * @param nodes - the set of nodes that go in the layer
	 */
	public void layerWriter(PrintWriter pw, layer label, HashSet<String> nodes)
	{	pw.println("subgraph " + label + " {");
		pw.println("color=white");
		String style = "";
		
		//determines the color of the layer
		switch(label)
		{	case INPUT_LAYER:
				style = "rank = min\n node [style=filled,color=cyan1, shape=circle, height = 2, width = 2,fontsize = 64]";
				break;
			case HIDDEN_LAYER:
				style = "rank = same\n node [style=filled,color= firebrick2, shape=circle, height = 2, width = 2, fontsize = 64]";	
				break;
			case OUTPUT_LAYER:
				style = "rank = max \n node [style=filled,color= green2, shape=circle, height = 2, width = 2, fontsize = 64]";	
				break;
		}
		
		pw.println(style);
		//writes the nodes down
		for(String s: nodes)
		{	if (!s.startsWith(bias)) pw.println(s);
			else pw.println(s + " [style=filled,color= gold, shape=circle,height = 2, width = 2, fontsize = 64]");
		}
		
		pw.println("");
		pw.println("label = \"" + label + "\"");
		pw.println("}");
	}
}
