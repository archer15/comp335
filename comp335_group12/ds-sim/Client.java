import java.io.*;
import java.net.*;
import java.util.ArrayList; 
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Attr;
import org.w3c.dom.NamedNodeMap;
import org.xml.sax.SAXException;
public class Client
{
    static int core = 0;
    static int initCore = Integer.MAX_VALUE;
    static int serverNo = -1;
    static int jobNo = 0;
    static int jobTime = 0;
    static int jobCore = 0;
    static int jobDisk = 0;
    static int jobMem = 0;
    static String serverType;
    static int count = 0;
    static String algorithm="";
    static ArrayList<String> types;
    static ArrayList<String[]> initialCap;
    static ArrayList<String[]> servers;
    static ArrayList<Integer> coreCounts;
    static int time = Integer.MAX_VALUE;
    static int i = 0;
    static boolean initial = true;
    static String [] val;
    public static void main(String[] args) throws ParserConfigurationException, IOException, SAXException{
        Socket client = new Socket("127.0.0.1", 8096);
	OutputStream outputstream = client.getOutputStream();
        DataOutputStream output = new DataOutputStream(outputstream);
	initialCap = new ArrayList <String[]>();
	coreCounts = new ArrayList <Integer>();
	servers = new ArrayList <String[]>();
	for(int i=0; i<args.length; i++){
	    if(args[i].equals("-a")){
	        if(args[i+1].equals("ff")){
		    algorithm = "ff";
	        }
	        else if(args[i+1].equals("bf")){
		    algorithm = "bf";
	        }
	    }
	}


	PrintWriter pw = new PrintWriter(outputstream, true);
	pw.print("HELO\n");
	pw.flush();	
	String username = System.getProperty("user.name");
	pw.print("AUTH " + username + "\n");
	pw.flush();	

        serverResponse(client, pw);
	pw.close();
        output.close();

        client.close();
    
    }

    public static void serverResponse(Socket client, PrintWriter pw) throws ParserConfigurationException, IOException, SAXException{
	BufferedReader input = new BufferedReader(new InputStreamReader(client.getInputStream()));
        String message;
        while((message = input.readLine())!=null){

       	    clientResponse(pw, message);

       }
	input.close();
    }
   

    public static void clientResponse(PrintWriter pw, String message)  throws ParserConfigurationException, IOException, SAXException{
        if(message.equals("OK")){
	    if(count == 0){
	        types = ReadXML("system.xml");
	        count++;
	    }
	    else{
	     	pw.print("REDY\n");
	        pw.flush();	
	    }

	}
	else if(message.equals(".")){
	    if(algorithm.equals("ff")){	
		if(servers.size()==0){
		    for(int j=0; j<initialCap.size();j++){
			    val = initialCap.get(j);
			    if(Integer.parseInt(val[4])>=jobCore && Integer.parseInt(val[5])>=jobMem && Integer.parseInt(val[6])>=jobDisk){
			       	pw.print("SCHD " + jobNo + " " + val[0] + " " + val[1] + "\n");
	   	     		pw.flush(); 	
                     		serverNo = -1;
		     		initial = false;	    
				return;
			    }                    
		    }		

		}	  
		else{
		    for(int i=0;i<types.size();i++){
		        for(int j=0;j<servers.size();j++){
		    	    String[] server;
			    server = servers.get(j);
			    if(types.get(i).equals(server[0])){
			       	pw.print("SCHD " + jobNo + " " + server[0] + " " + server[1] + "\n");
	   	     		pw.flush(); 
                     		serverNo = -1;
		     		initial = false;
				return;
			    }
			}
		    }
      		}



	    }
	    else{
	        pw.print("SCHD " + jobNo + " " + serverType + " " + serverNo + "\n");
	        pw.flush();
	    }
       }
	else { //REDY -> JOBN
              
	    String msg = message.substring(0,4);
            String[] splitmsg = message.split("\\s+");
	    if(msg.equals("JOBN")){
	        servers.clear();		
		jobNo = Integer.parseInt(splitmsg[2]);
		jobTime = Integer.parseInt(splitmsg[1]);
		jobMem = Integer.parseInt(splitmsg[5]);
		jobDisk = Integer.parseInt(splitmsg[6]);
		jobCore = Integer.parseInt(splitmsg[4]);
		if(algorithm.equals("ff")){		  
		    pw.print("RESC All " + "\n");
	            pw.flush();
		    
		}
                else{
		    pw.print("RESC Avail "+ splitmsg[4] + " " + splitmsg[5] + " " + splitmsg[6] + "\n");
		    }
	        //pw.print("SCHD " + jobNo + " " + "large" + " " + "0" + "\n");
	        pw.flush();
            }
	    else if(msg.equals("DATA")){
	        pw.print("OK\n");
	        pw.flush();
	    }
            else if(msg.equals("NONE")){
                pw.print("QUIT\n");
	        pw.flush();
            }
	    else if(msg.equals("ERR:")){
                pw.print("OK\n");
	        pw.flush();
	    }
 	    //If the array contains 7 items, the message contains the server information.
	    else if(splitmsg.length==7){ 
		if(algorithm.equals("ff")){
		    FirstFit.firstFit(splitmsg, pw);

		}
		else if(algorithm.equals("bf")){
		    BestFit.bestFit(pw, splitmsg);
		}
		else{
		    /*If current server has more cores than the previous server, update the core, serverNo, and serverType variables with the current 			    server's information (finding server with greatest number of cores)*/
		    if(Integer.parseInt(splitmsg[4])>core){
		    core = Integer.parseInt(splitmsg[4]);
		    serverNo = Integer.parseInt(splitmsg[1]);
		    serverType = splitmsg[0];
		    }
		}
                pw.print("OK\n");
	        pw.flush();
	    }
        }
	
    }

    public static ArrayList<String> ReadXML(String xml)  throws ParserConfigurationException, IOException, SAXException{
        ArrayList<String> types = new ArrayList<>();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(new File(xml));
	NodeList nodeList = document.getDocumentElement().getChildNodes();
	//Prints root element config
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
 	    if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element elem = (Element) node;
                NodeList nList1 = elem.getChildNodes();
                for (int j = 0; j < nList1.getLength(); j++) {
		    Node node1 = nList1.item(j);
 	   	    if (node1.getNodeType() == Node.ELEMENT_NODE) {
			Element elem2 = (Element)node1;
			NamedNodeMap attributes = elem2.getAttributes();
			for (int k = attributes.getLength()-1; k > -1; k--) {
			    Attr attr = (Attr) attributes.item(k);
			    String attrName = attr.getNodeName();
 			    String attrValue = attr.getNodeValue();
			    if(attrName.equals("coreCount")){
			    	coreCounts.add(Integer.parseInt(attrValue));
			    }	
			    else if(attrName.equals("type")){
			    	types.add(attrValue);
			    }

    		       }
		    }  
	        }
            }
        }

    return types;

    }
}
