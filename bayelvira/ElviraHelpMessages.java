
import Elvira;

/**
 * This class implements the method necessary to display messages when 
 * something happen. This class contains too all the messages.
 */

class ElviraHelpMessages {

    /**
     * Basic method to display messages
     * 
     * @param message String that contains the message to display
     */
     
    static void show (ElviraFrame frame, String message) {
        frame.appendText(message);
    }
    
    static void show (String message) {
        System.out.println(message);
    }


	// ************** Help messages ****************

    static final String version_number_message = "Version 0.01";

    static final String about_message = new String(
        "Elvira " + version_number_message + "\n" +
        "Copyright 1998 Proyecto Elvira \n" +
        "<smc@decsai.ugr.es>\n" +
        "<http://decsai.ugr.es/~smc>\n"+"\n\n");
	

   static final String start_message = new String(
        "To start, press the Editor button and click on the Elvira\n" +
        "editor or load a network using the Network->Open menu.\n\n");

    static final String create_message = new String(
    	"To create a new node, click the mouse button once\n" +
	    "on the area above.\n" +
        "To connect two nodes, click on the parent node\n" +
	    "drag to the child node, and then release.\n" +
        "To edit node attributes, click on Edit button.\n" +
        "To move or delete nodes, click on appropriate button.\n\n");

    static final String move_message = new String(
        "To move a node, click on it and drag it to the new position.\n\n");

    static final String delete_message = new String(
        "To delete a node, click on it.\n" +
        "To delet an arrow, click on the arrow's head.\n\n");

    static final String edit_message = new String(
        "To edit attributes of a node, click on it.\n" +
        "Note: after you edit attributes in a text field,\n" +
        "you must press the Enter key. Only after that\n" +
        "your changes will be entered.\n\n");

    static final String observe_message = new String(
        "To observe a node, click on it.\n\n");

    static final String query_message = new String(
        "To query on a particular node, click on it.\n\n");

    // Syntax message

    static final String syntax = new String ("use: Elvira [-l <language>] file.elv...");

    // Error and exception messages

    static final String unexpected_end_of_input = new String(
        "Unable to complete load: Unexpected end of input!\n\n");

	static final String incorrect_file_format = new String(
	    "Unable to complete load: Incorrect file format.\n\n");

    static final String unable_read_file = new String(
        "Unable to read file!\n\n");

	static final String unable_generate_parents_dialog = new String(
	    "Unable to generate parent values dialog!\n\n");

	static final String duplicate_values = new String(
	    "Duplicate value!\n\n");

 	static final String node_name_change_failed = new String(
 	    "Node name change failed.\n\n");

    static final String no_value_to_replace = new String(
        "No new value to replace!\n\n");

    static final String no_value_selected_to_replace = new String(
        "No value selected for replace!\n\n");

    static final String no_value_to_add = new String(
        "No value to add!\n\n");

	static final String no_value_selected_to_delete = new String(
	    "No value selected for delete!\n\n");

    static final String observe_error = new String(
        "No value selected for Observe!\n\n");

    static final String notnode = new String(
        "Please click on a node.\n\n");

    static final String maxnodes = new String(
    	"Reached limit on maximum number of nodes.\n\n");

    static final String selfarc = new String(
    	"Can not create arc to self.\n\n");

    static final String circular = new String(
    	"Circular parent relations not allowed.\n\n");

    static final String notDecisionYet = new String(
    	"Decision Nodes not allowed in this version.\n\n");

    static final String notUtilityYet = new String(
    	"Utility Nodes not allowed in this version.\n\n");

    static final String notContinuousYet = new String(
    	"Continuous Variables not allowed in this version.\n\n");

    static final String notInfiniteDiscreteYet = new String(
    	"Infinite Discrete Variables not allowed in this version.\n\n");

    static final String notMixedYet = new String(
    	"Mixed Variables not allowed in this version.\n\n");

}
