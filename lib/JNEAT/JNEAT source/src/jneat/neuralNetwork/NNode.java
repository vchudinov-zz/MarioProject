/* Generated by Together */

   package jneat.neuralNetwork;

   import java.util.*;
import java.text.*;

import jneat.Neat;
import jNeatCommon.*;
/**
 * A NODE is either a NEURON or a SENSOR. If it's a sensor, it can be loaded with a value for output
 * If it's a neuron, it has a list of its incoming input signals Use an activation count to avoid flushing
 */
	public class NNode extends Neat {
   /** type is either SIGMOID ..or others that can be added */
	  int ftype;
   
   /** type is either NEURON or SENSOR */
	  int type;
   
   /** The incoming activity before being processed */
   
	  double activesum;
   
   /** The total activation entering in this Node */
	  double activation;
   
   /** when are signal's  to node,  the node switch this field  from FALSE to  TRUE */
	  boolean active_flag;
   
   /** not used */
	  double output;
   
   /** vector of real values for hebbian or other advanced function future */
	  double[] params = new double[Neat.p_num_trait_params];
   
   /** A list of pointers to incoming weighted signals from other nodes */
	  Vector incoming = new Vector(1,0);
   
   /** A list of pointers to links carrying this node's signal */
	  Vector outgoing = new Vector(1,0);
   
   /** Numeric identification of node */
	  int node_id;
   
   /** Used for genetic marking of nodes. are 4  type of node : input,bias,hidden,output */
	  int gen_node_label;
   
   /** this value is how many time this node are activated during activation of network */
	  double activation_count;
   
   /** activation value of node at time t-1; Holds the previous step's activation for recurrency */
	  double last_activation;
   
   /** activation value of node at time t-2 Holds the activation before  the previous step's */
	  double last_activation2;
   
   /** Points to a trait of parameters */
	  Trait nodetrait;
   
   /**
   * Is a reference  to a  Node ; Has used for generate and point from a genetic node (genotype)  to a real node (fenotype)
   * during 'genesis' process
   * @clientCardinality 1
   * @supplierCardinality 1
   */
	  NNode analogue;
   
   /**
   * Is a  temporary reference  to a  Node ; Has used for generate a new genome during duplicate phase of genotype.
   * @supplierCardinality 1
   * @clientCardinality 1
   */
	  NNode dup;
   
	   public int getFtype() {
		 return ftype;
	  }                                          
   
	   public void setFtype(int ftype) {
		 this.ftype = ftype;
	  }                                          
   
	   public int getType() {
		 return type;
	  }                                          
   
	   public void setType(int type) {
		 this.type = type;
	  }                                          
   
   
   
	   public void setActivesum(int activesum) {
		 this.activesum = activesum;
	  }                                          
   
	   public double getActivation() {
		 return activation;
	  }                                          
   
	   public void setActivation(double activation) {
		 this.activation = activation;
	  }                                          
   
	   public boolean getActive_flag() {
		 return active_flag;
	  }                                          
   
	   public void setActive_flag(boolean active_flag) {
		 this.active_flag = active_flag;
	  }                                          
   
	   public double getOutput() {
		 return output;
	  }                                          
   
	   public void setOutput(double output) {
		 this.output = output;
	  }                                          
   
	   public double[] getParams() {
		 return params;
	  }                                          
   
	   public void setParams(double[] params) {
		 this.params = params;
	  }                                          
   
	   public Vector getIncoming() {
		 return incoming;
	  }                                          
   
	   public void setIncoming(Vector incoming) {
		 this.incoming = incoming;
	  }                                          
   
	   public Vector getOutgoing() {
		 return outgoing;
	  }                                          
   
	   public void setOutgoing(Vector outgoing) {
		 this.outgoing = outgoing;
	  }                                          
   
	   public int getNode_id() {
		 return node_id;
	  }                                          
   
	   public void setNode_id(int node_id) {
		 this.node_id = node_id;
	  }                                          
   
	   public int getGen_node_label() {
		 return gen_node_label;
	  }                                          
   
	   public void setGen_node_label(int gen_node_label) {
		 this.gen_node_label = gen_node_label;
	  }                                          
   
	   public double getActivation_count() {
		 return activation_count;
	  }                                          
   
	   public void setActivation_count(double activation_count) {
		 this.activation_count = activation_count;
	  }                                          
   
	   public double getLast_activation() {
		 return last_activation;
	  }                                          
   
	   public void setLast_activation(double last_activation) {
		 this.last_activation = last_activation;
	  }                                          
   
	   public double getLast_activation2() {
		 return last_activation2;
	  }                                          
   
	   public void setLast_activation2(double last_activation2) {
		 this.last_activation2 = last_activation2;
	  }                                          
   
	   public Trait getNodetrait() {
		 return nodetrait;
	  }                                          
   
	   public void setNodetrait(Trait nodetrait) {
		 this.nodetrait = nodetrait;
	  }                                          
   
	   public NNode getAnalogue() {
		 return analogue;
	  }                                          
   
	   public void setAnalogue(NNode analogue) {
		 this.analogue = analogue;
	  }                                          
   
	   public NNode getDup() {
		 return dup;
	  }                                          
   
	   public void setDup(NNode dup) {
		 this.dup = dup;
	  }                                          
   
	   public NNode(int ntype, int nodeid) {
		 active_flag = false;
		 activesum = 0;
		 activation = 0;
		 output = 0;
		 last_activation = 0;
		 last_activation2 = 0;
		 type = ntype; //NEURON or SENSOR type
		 activation_count = 0; //Inactive upon creation
		 node_id = nodeid; // id del nodo
		 ftype = NeatConstant.SIGMOID; // funt act : signmoide
		 gen_node_label = NeatConstant.HIDDEN;
		 nodetrait = null;
	  //	  incoming = new Vector(1, 0);
	  //	  outgoing = new Vector(1, 0);
		 dup = null;
		 analogue = null;
		 is_traversed = false;
		 inner_level = 0;
	  } 
	   /**
	    * STC
	    * @param ntype Neuron or Sensor type
	    * @param nodeid Id of node
	    * @param placement Input, bias, hidden, or output
	    */
	   public NNode(int ntype, int nodeid, int placement) {
		 active_flag = false;
		 activesum = 0;
		 activation = 0;
		 output = 0;
		 last_activation = 0;
		 last_activation2 = 0;
		 type = ntype; //NEURON or SENSOR type
		 activation_count = 0; //Inactive upon creation
		 node_id = nodeid; // id del nodo
		 ftype = NeatConstant.SIGMOID; // funt act : signmoide
		 gen_node_label = placement;
		 nodetrait = null;
	  //	  incoming = new Vector(1, 0);
	  //	  outgoing = new Vector(1, 0);
		 dup = null;
		 analogue = null;
		 is_traversed = false;
		 inner_level = 0;
	  } 
	   
	   /**
	    * Construct the node out of a file specification
    	* using given list of traits
	    * @param xline
	    * @param traits
	    */

	   public NNode(String xline, Vector traits) 
	  {
	  
		 active_flag = false;
		 inner_level = 0;
		 activesum = 0;
		 activation = 0;
		 output = 0;
		 last_activation = 0;
		 last_activation2 = 0;
		 activation_count = 0; //Inactive upon creation
		 ftype = NeatConstant.SIGMOID; // funt act : signmoide
	  //  incoming = new Vector(1, 0);
	  //  outgoing = new Vector(1, 0);
		 dup = null;
		 analogue = null;
	  
		 Iterator itr_trait;
		 int _trait_id;
		 StringTokenizer st;
		 String s1;
		 st = new StringTokenizer(xline);
	  
	  //skip keyword
		 s1 = st.nextToken();
	  
	  //Get the node parameters
		 s1 = st.nextToken();
		 node_id = Integer.parseInt(s1);
	  
	  //get trait
		 s1 = st.nextToken();
		 _trait_id = Integer.parseInt(s1);
	  
	  //get type of node
		 s1 = st.nextToken();
		 type = Integer.parseInt(s1);
	  
	  //get genetic typw of node
		 s1 = st.nextToken();
		 gen_node_label = Integer.parseInt(s1);
	  
		 nodetrait = null;
		 is_traversed = false;
	  
		 if (_trait_id > 0 && traits != null) {
		 
			itr_trait = traits.iterator();
			while (itr_trait.hasNext()) {
			   Trait _trait = ((Trait) itr_trait.next());
			   if (_trait.trait_id == _trait_id) {
				  nodetrait = _trait;
				  break;
			   }
			}
		 }
	  
	  }         
	   public NNode(NNode n, Trait t) {
		 active_flag = false;
		 activesum = 0;
		 activation = 0;
		 output = 0;
		 last_activation = 0;
		 last_activation2 = 0;
		 type = n.type; //NEURON or SENSOR type
		 activation_count = 0; //Inactive upon creation
		 node_id = n.node_id; // id del nodo
		 ftype = NeatConstant.SIGMOID; // funt act : sigmoid
		 gen_node_label = n.gen_node_label;
		 nodetrait = t;
		 dup = null;
		 analogue = null;
		 is_traversed = false;
		 inner_level = 0;
	  }
	   
	   public void derive_trait(Trait t) 
	  {
		 if (t != null) 
		 {
			for (int count = 0; count < Neat.p_num_trait_params; count++)
			   params[count] = t.params[count];
		 } 
		 else 
		 {
			for (int count = 0; count < Neat.p_num_trait_params; count++)
			   params[count] = 0;
		 }
	  }                
   
   
   /**
   * 
   * 
   */
	   public int depth(int xlevel, Network mynet, int xmax_level) 
	  {
	  
		 Iterator itr_link;
	  
		 StringBuffer cost1 = null;
		 String cost2 = null;
	  
	  
	  // control for loop 
		 if (xlevel > 100) 
		 {
			System.out.print("\n ** DEPTH NOT DETERMINED FOR NETWORK WITH LOOP ");
		 //	  	System.out.print("\n Fenotype is " + mynet.getNet_id());
		 //	  	System.out.print("\n Genotype is  " + mynet.getNet_id());
		 //	  	mynet.genotype.op_view();
			return 10;
		 }
	  
	  //Base Case
		 if (this.type == NeatConstant.SENSOR)
			return xlevel;
	  
		 xlevel++;
	  
	  // recursion case  
		 itr_link = this.getIncoming().iterator();
		 int cur_depth = 0; //The depth of the current node
	  
		 while (itr_link.hasNext()) 
		 {
			Link _link = ((Link) itr_link.next());
			NNode _ynode = _link.in_node;
		 
			if (!_ynode.is_traversed) 
			{
			   _ynode.is_traversed = true;
			   cur_depth = _ynode.depth(xlevel, mynet, xmax_level);
			   _ynode.inner_level = cur_depth - xlevel;
			} 
			else 
			   cur_depth = xlevel + _ynode.inner_level;
		 
			if (cur_depth > xmax_level)
			   xmax_level = cur_depth;
		 }
		 return xmax_level;
	  
	  }  
	  
	   /**
	    * STC
	    * @return Total activation entering this node
	    */
	   public double get_active_out() 
	  {
		 if (activation_count>0)
			return activation;
		 else 
			return 0.0;
	  }
   /**
   *	Return activation currently in node 
   *	from PREVIOUS (time-delayed) time step,
   *	if there is one
   */
	   public double get_active_out_td() 
	  {
		 if (activation_count>1)
			return last_activation;
		 else 
			return 0.0;
	  }
	   public double getActivesum() {
		 return activesum;
	  }
	   
	   /**
	    * STC
	    * Prints out information about the node.
	    * Info printed: Type, Node ID, Number of times the node has been activated,
	    * Total activation entering the node 
	    */
	   public void op_view() 
	  {
	  
		 String maskf = " #,##0";
		 DecimalFormat fmtf = new DecimalFormat(maskf);
	  
		 String mask5 = " #,##0.000";
		 DecimalFormat fmt5 = new DecimalFormat(mask5);
	  
		 if (type == NeatConstant.SENSOR)
			System.out.print("\n (Sensor)");
		 if (type == NeatConstant.NEURON)
			System.out.print("\n (Neuron)");
	  
		 System.out.print(
			fmtf.format(node_id)
			+ " activation count "
			+ fmt5.format(activation_count)
			+ " activation="
			+ fmt5.format(activation)
			+ ")"); 
	  
	  }
	   
	   /**
	    * STC
	    * If node is a sensor, the activation value of the sensor is set to the value
	    * @param value output value
	    * @return 
	    */
	   public boolean sensor_load(double value) 
	  {
		 if (type==NeatConstant.SENSOR) 
		 {
		 //Time delay memory
			last_activation2=last_activation;
			last_activation=activation;
		 
			activation_count++;  //Puts sensor into next time-step
			activation=value;    //ovviamente non viene applicata la f(x)!!!!
			return true;
		 }
		 else 
			return false;
	  }
   
	   public void setActivesum(double activesum) {
		 this.activesum = activesum;
	  }                                 
   
   
	  public int inner_level;	
	  public boolean is_traversed;
	  /**
   * .
   * 
   */
	   public boolean mark(int xlevel, Network mynet) 
	  {
	  
		 Iterator itr_link;
	  
	  // loop control 
		 if (xlevel > 100) 
		 {
		 //      System.out.print("\n ** DEPTH NOT DETERMINED FOR NETWORK WITH LOOP ");
		 //      System.out.print("\n Network name is " + mynet.getNet_id());
		 //      mynet.genotype.op_view();
			return false;
		 }
	  
	  // base Case
		 if (this.type == NeatConstant.SENSOR) 
		 {
			this.is_traversed = true;
			return true;
		 }
	  
	  
	  
	  // recurrency case
		 itr_link = this.getIncoming().iterator();
		 boolean rc = false;
	  
		 while (itr_link.hasNext()) 
		 {
			Link _link = ((Link) itr_link.next());
			if (!_link.in_node.is_traversed) 
			{
			   _link.in_node.is_traversed = true;
			   rc = _link.in_node.mark(xlevel + 1, mynet);
			   if (rc == false)
				  return false;
			} 
		 }
		 return true;
	  
	  }
	   
	   public void print_to_file(IOseq xFile) 
	  {
	  
		 StringBuffer s2 = new StringBuffer("");
	  
		 s2.append("node ");
		 s2.append(node_id);
	  
		 if (nodetrait != null)
			s2.append(" " + nodetrait.trait_id);
		 else
			s2.append(" 0");
	  
		 s2.append(" " + type);
		 s2.append(" " + gen_node_label);
	  
		 xFile.IOseqWrite(s2.toString());
	  
	  }	   public void flushbackOLD() 
	  {
		 Iterator itr_link;
	  
	  //A sensor should not flush black
		 if (type != NeatConstant.SENSOR) 
		 {
			if (activation_count > 0) 
			{
			   activation_count = 0;
			   activation = 0;
			   last_activation = 0;
			   last_activation2 = 0;
			}
		 //Flush back recursively
			itr_link = incoming.iterator();
		 
			while (itr_link.hasNext()) 
			{
			   Link _link = ((Link) itr_link.next());
			   _link.added_weight = 0.0;
			
			   _link.is_traversed = false;
			
			   if (_link.in_node.activation_count > 0)
				  _link.in_node.flushbackOLD();
			}
		 } 
		 else 
		 {
		 //Flush the SENSOR
			activation_count = 0;
			activation = 0;
			last_activation = 0;
			last_activation2 = 0;
		 }
	  }	   
	  /**
	   * STC
	   * Resets values in the node and sets the weight of 
	   * connection to and from the node to zero
	   */
	  public void resetNNode() 
	  {
		    Iterator itr_link;
		  
			activation_count = 0;
			activation = 0;
			last_activation = 0;
			last_activation2 = 0;
			
		 //Flush back link
			itr_link = incoming.iterator();
			while (itr_link.hasNext()) 
			{
			   Link _link = ((Link) itr_link.next());
			   _link.added_weight = 0.0;
			   _link.is_traversed = false;
			}

		 //Flush forw link
			itr_link = outgoing.iterator();
			while (itr_link.hasNext()) 
			{
			   Link _link = ((Link) itr_link.next());
			   _link.added_weight = 0.0;
			   _link.is_traversed = false;
			}

	  }}