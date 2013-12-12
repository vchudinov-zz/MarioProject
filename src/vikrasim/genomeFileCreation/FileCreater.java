package vikrasim.genomeFileCreation;

import jNeatCommon.IOseq;

import java.io.File;

public class FileCreater {

	public static void main(String[] args) {
		String fileName = new File("").getAbsolutePath()
				+ "\\NEAT data\\Training data\\Mario 1\\starterGenome.txt";
		int inputNodes = 29;
		int outputNodes = 6;

		FileCreater gfc = new FileCreater();
		gfc.createGenomeFile(fileName, inputNodes, outputNodes);

	}

	public void createGenomeFile(String fileName, int inputNodes,
			int outputNodes) {
		String s = "";
		String delimiter = "\n";

		// Write start of file
		s = "genomestart 1 " + delimiter;
		s = s + "trait 1 0.1 0 0 0 0 0 0 0 " + delimiter;
		s = s + "trait 2 0.1 0 0 0 0 0 0 0 " + delimiter;

		int nodeNumber = 1;

		for (int i = 1; i <= inputNodes; i++) {
			s += "node " + nodeNumber + " 0 1 1 " + delimiter;
			nodeNumber++;
		}

		// Add bias node
		int biasNumber = nodeNumber;
		s += "node " + biasNumber + " 0 1 3 " + delimiter;
		nodeNumber++;

		int outStart = nodeNumber;
		for (int i = 1; i <= outputNodes; i++) {
			s += "node " + nodeNumber + " 0 0 2 " + delimiter;
			nodeNumber++;
		}

		// Add genes
		for (int out = outStart; out < nodeNumber; out++) {
			s += "gene 1 " + biasNumber + " " + out + " 0 0 1 0 1 " + delimiter;
		}

		// Add close line
		s += "genomeend 1";

		IOseq writer = new IOseq(fileName);
		writer.IOseqOpenW(false);
		for (String line : s.split(delimiter)) {
			writer.IOseqWrite(line);
		}
		writer.IOseqCloseW();
	}

	public void createParametersFile(String fileName) {
		String s = "";
		String delimiter = "\n";

		// Write start of file
		s += "p_trait_param_mut_prob  0.5" + delimiter;
		s += "p_trait_mutation_power  1.0" + delimiter;
		s += "p_linktrait_mut_sig  1.0" + delimiter;
		s += "p_nodetrait_mut_sig  0.5" + delimiter;
		s += "p_weight_mut_power  2.5" + delimiter;
		s += "p_recur_prob  0.1" + delimiter;
		s += "p_disjoint_coeff  1.0" + delimiter;
		s += "p_excess_coeff  1.0" + delimiter;
		s += "p_mutdiff_coeff  0.4" + delimiter;
		s += "p_compat_threshold  3.0" + delimiter;
		s += "p_age_significance  1.0" + delimiter;
		s += "p_survival_thresh  0.2" + delimiter;
		s += "p_mutate_only_prob  0.25" + delimiter;
		s += "p_mutate_random_trait_prob  0.1" + delimiter;
		s += "p_mutate_link_trait_prob  0.1" + delimiter;
		s += "p_mutate_node_trait_prob  0.1" + delimiter;
		s += "p_mutate_link_weights_prob  0.9" + delimiter;
		s += "p_mutate_toggle_enable_prob  0.0" + delimiter;
		s += "p_mutate_gene_reenable_prob  0.0" + delimiter;
		s += "p_mutate_add_node_prob  0.03" + delimiter;
		s += "p_mutate_add_link_prob  0.08" + delimiter;
		s += "p_mutate_add_sensor_prob  0.05" + delimiter;
		s += "p_interspecies_mate_rate  0.0010" + delimiter;
		s += "p_mate_multipoint_prob  0.3" + delimiter;
		s += "p_mate_multipoint_avg_prob  0.3" + delimiter;
		s += "p_mate_singlepoint_prob  0.3" + delimiter;
		s += "p_mate_only_prob  0.2" + delimiter;
		s += "p_recur_only_prob  0.0" + delimiter;
		s += "p_pop_size  200" + delimiter;
		s += "p_dropoff_age  50" + delimiter;
		s += "p_newlink_tries  50" + delimiter;
		s += "p_print_every  1" + delimiter;
		s += "p_babies_stolen  0" + delimiter;
		s += "p_num_runs  1" + delimiter;
		s += "p_num_trait_params  8" + delimiter;
		s += "p_min_num_offspring_before_save 5" + delimiter;
		s += "p_num_species_target 10" + delimiter;

		IOseq writer = new IOseq(fileName);
		writer.IOseqOpenW(false);
		for (String line : s.split(delimiter)) {
			writer.IOseqWrite(line);
		}
		writer.IOseqCloseW();
	}

}
