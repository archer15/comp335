import java.io.*;
import java.net.*;
import org.xml.sax.SAXException;
import java.util.ArrayList; 
import javax.xml.parsers.ParserConfigurationException;
public class FirstFit{
    public static void firstFit(String[] splitmsg, PrintWriter pw)  throws ParserConfigurationException, IOException, SAXException{
        if(Integer.parseInt(splitmsg[2])==2||Integer.parseInt(splitmsg[2])==0 ||Integer.parseInt(splitmsg[2])==3 ||Integer.parseInt(splitmsg[2])==1){
	    if(Client.initial==true){
	        Client.initialCap.add(splitmsg);		//Store initial server cap into ArrayList
	    }
	    if(Client.jobCore <= Integer.parseInt(splitmsg[4]) && Client.jobMem <= Integer.parseInt(splitmsg[5]) && Client.jobDisk <= 			Integer.parseInt(splitmsg[6])){
		if(Client.i==0){
		    smallestToLargest();
		    Client.i++;		
		}
		Client.servers.add(splitmsg);		   
       	    }  	 
	}
	
    }
    public static void smallestToLargest(){
	int temp = 0; String temp2 = "";
        for (int i = 0; i < Client.coreCounts.size(); i++){
            for (int j = i + 1; j < Client.coreCounts.size(); j++){
                if (Client.coreCounts.get(i) > Client.coreCounts.get(j)){
                    temp = Client.coreCounts.get(i);
		    temp2 = Client.types.get(i);
                    Client.types.set(i, Client.types.get(j));
                    Client.coreCounts.set(i, Client.coreCounts.get(j));
                    Client.coreCounts.set(j, temp);
                    Client.types.set(j, temp2);
                }
            }

        }
    }
}
