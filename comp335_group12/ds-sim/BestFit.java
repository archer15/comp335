import java.io.*;
import java.net.*;
import org.xml.sax.SAXException;
import javax.xml.parsers.ParserConfigurationException;
public class BestFit{
    public static void bestFit(PrintWriter pw, String []splitmsg)  throws ParserConfigurationException, IOException, SAXException{
        int bestFitServer = Integer.MAX_VALUE;
	int fitnessValue = 0;
	int minAvail = Integer.MAX_VALUE;
	int i = 0;
	while (i<Client.types.size()) {
		if(Client.jobCore >= Integer.parseInt(splitmsg[5]))	{//if server has >= cores as job DONT KNOW HOW TO ACCESS 
			if(fitnessValue > Integer.parseInt(splitmsg[5])) {
				fitnessValue = Integer.parseInt(splitmsg[5]);
				bestFitServer = Integer.parseInt(splitmsg[5]);
			} else if (fitnessValue == Integer.parseInt(splitmsg[5])) {
				//if the current fitness value and the bestFitServer is identical, the minAvail times are checked
			}

		}
		i++;
	}
	pw.print("SCHD " +"something" +splitmsg[0] + "something");
	pw.flush();
    }
}
