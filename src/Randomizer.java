import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Randomizer extends Application{
	static Stage mainStage;
	public static List<String> allAgents;
	public static List<String> selectedAgents;
	static Scanner sc;
	
	public static void main(String[] args) throws IOException {
		launch(args);
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		try {
			selectedAgents = new ArrayList<String>();
			update();
		    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Randomizer.fxml"));
		    Parent root1 = (Parent) fxmlLoader.load();
		    Stage stage = new Stage();
		    stage = new Stage();
		    stage.initModality(Modality.APPLICATION_MODAL);
		    stage.initStyle(StageStyle.UNDECORATED);
		    stage.setTitle("Valorant Character Randomizer");
		    stage.setScene(new Scene(root1));  
		    stage.show();
		    mainStage = stage;
		} catch(Error e) {
			//System.out.printf("Failed to open Application.%n");
		}
	}
	
	public static void update() throws IOException {
		allAgents = checkWikiCharacters();
		getAllCharacterImages();
	}
	
	public static void getAllCharacterImages() throws IOException {
		String URL = String.format("https://valorant.fandom.com/wiki/VALORANT_Wiki");
		String destinationName;
		String webFileName = String.format("WikiImagePage.txt");
		getWebsiteInformationWithHTML(URL, webFileName);
		File wikiFile = new File(String.format("src/Info/%s", webFileName));
		String AgentURL = "";
		String agent = "";
		for(Iterator<String> agentIterator = allAgents.iterator(); agentIterator.hasNext();) {
			sc = new Scanner(wikiFile);
			agent = agentIterator.next();
			destinationName = String.format("%s.png", agent);
			while(sc.hasNextLine()) {
				String next = sc.nextLine();
				if(next.contains(String.format("%s", agent)) && next.contains("data-image-key=")) {
					AgentURL = sc.nextLine();
					AgentURL = AgentURL.substring(10, AgentURL.length()-1);
				}
			}
			saveImage(AgentURL, destinationName);
			//System.out.printf("Successfully downloaded %s.%n", destinationName);
		}
	}
	
	public static void saveImage(String imageUrl, String destinationFile) throws IOException {
	    URL url = new URL(imageUrl);
	    InputStream is = url.openStream();
	    OutputStream os = new FileOutputStream(String.format("src/Images/%s",destinationFile));

	    byte[] b = new byte[2048];
	    int length;

	    while ((length = is.read(b)) != -1) {
	        os.write(b, 0, length);
	    }

	    is.close();
	    os.close();
	}
	
	public static String randomize() throws IOException {
		if(selectedAgents.size() == 0) {
     		return "?";
     	}
		Random rand = new Random();
		int randoID = rand.nextInt(selectedAgents.size());
		return selectedAgents.get(randoID);
	}
	
	public static void getWebsiteInformationNoHTML(String URLString, String fileName) throws IOException {
		URL url = new URL(URLString);
		//Retrieving the contents of the specified page
		Scanner sc = new Scanner(url.openStream());
		//Instantiating the StringBuffer class to hold the result
		StringBuffer sb = new StringBuffer();
		while(sc.hasNext()) {
			sb.append(sc.next()+String.format("%n"));
			//System.out.println(sc.next());
		}
		//Retrieving the String from the String Buffer object
		String result = sb.toString();
		//Removing the HTML tags
		result = result.replaceAll("<[^>]*>", "");
		printToText(result, fileName);
		//System.out.printf("Succesfully attained info from %s and put into %s.%n", URLString, fileName);
	}
	public static void getWebsiteInformationWithHTML(String URLString, String fileName) throws IOException {
		URL url = new URL(URLString);
		//Retrieving the contents of the specified page
		Scanner sc = new Scanner(url.openStream());
		//Instantiating the StringBuffer class to hold the result
		StringBuffer sb = new StringBuffer();
		while(sc.hasNext()) {
			sb.append(sc.next()+String.format("%n"));
			//System.out.println(sc.next());
		}
		//Retrieving the String from the String Buffer object
		String result = sb.toString();
		printToText(result, fileName);
		//System.out.printf("Succesfully attained info from %s and put into %s.%n", URLString, fileName);
	}
	
	public static List<String> checkWikiCharacters() throws IOException {
	      String fileName = "webPageOutput.txt";
	      getWebsiteInformationNoHTML("https://valorant.fandom.com/wiki/VALORANT", fileName);
	      File F1 = new File(String.format("src/Info/%s",fileName));
	      sc = new Scanner(F1);
	      
	      //Separates Agents from the rest of the text into their own file
	      StringBuffer agentsSB = new StringBuffer();
	      while(sc.hasNextLine()) {
	    	  String data = sc.nextLine();
	    	  if(data.contains("episode.:")) {
	    		  sc.nextLine();
	    		  data = sc.nextLine();
	    		  while(!data.contains("Weapons")) {
	    			  agentsSB.append(data+String.format("%n"));
	    			  data = sc.nextLine();
	    		  }
	    		  break;
	    	  }
	      }
	      String agentsS = agentsSB.toString();
	      fileName = "Agents.txt";
	      printToText(agentsS, fileName);

	      return getAllAgents();
	}
	
	private static void printToText(String text, String fileName) {
		try {
			File F1 = new File(String.format("src/Info/%s",fileName));
			if(F1.createNewFile()) {
				//System.out.printf("File Created.%n");
			} else {
				//System.out.printf("File Already Exists.%n");
			}
			
			FileWriter writer = new FileWriter(String.format("src/Info/%s",fileName));
			writer.write(text);
			writer.close();
			//System.out.printf("Successfully wrote to %s%n", fileName);
		} catch(IOException e) {
			//System.out.printf("An Unusual Error has Occured%n");
			e.printStackTrace();
		}
	}
	
	private static List<String> getAllAgents() throws FileNotFoundException {
		File file = new File("src/Info/Agents.txt");
		List<String> allAgents = new ArrayList<String>();
		sc = new Scanner(file);
		while(sc.hasNextLine()) {
			String data = sc.nextLine();
			allAgents.add(data);
		}
		return allAgents;
	}
	
	public static void quit() {
		mainStage.close();
	}
	
	public static void minimize() {
		mainStage.setIconified(true);
	}
	
	public static void removeAgent(String agent) {
		int index = 0;
		for(Iterator<String> agents = selectedAgents.iterator(); agents.hasNext();) {
			if(agents.next().equals(agent)) {
				selectedAgents.remove(index);
				break;
			}
			index++;
		}
	}
	
	public static void addAgent(String agent) {
		if(!selectedAgents.contains(agent))
			selectedAgents.add(agent);
	}
	
}
