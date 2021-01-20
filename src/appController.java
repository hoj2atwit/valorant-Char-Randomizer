import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class appController {
	//initialize Scenebuilder objects
	@FXML
	Button randomizeButton;
	@FXML
	Button quitButton;
	@FXML
	Button minimizeButton;
	@FXML
	ImageView characterImageDisplay;
	@FXML
	ScrollPane characterSelectScrollPane;
	@FXML
	MenuItem updateMenu;
	@FXML
	MenuItem selectAllMenu;
	@FXML
	MenuItem deselectAllMenu;
	@FXML
	Text nameText;
	@FXML
	MenuBar mb;
	@FXML
	CheckBox repeatsCheckbox;
	
	ArrayList<CheckBox> characterCheckBoxes;
	VBox checkBoxHolder;
	
	//Window offset
	private static double xOffset = 0;
    private static double yOffset = 0;
	
    String lastAgent = "";
    
	public void initialize(){
		characterCheckBoxes = new ArrayList<CheckBox>();
		makeAllAgentCheckBoxes();
		
		//Allows Window Dragging.
		mb.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                xOffset = Randomizer.mainStage.getX() - event.getScreenX();
                yOffset = Randomizer.mainStage.getY() - event.getScreenY();
            }
        });

		mb.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
            	Randomizer.mainStage.setX(event.getScreenX() + xOffset);
            	Randomizer.mainStage.setY(event.getScreenY() + yOffset);
            }
        });
    }
	
	public void quit() {
		Randomizer.quit();
	}
	
	public void minimize() {
		Randomizer.minimize();
	}
	
	public void update() throws IOException {
		Randomizer.update();
		makeAllAgentCheckBoxes();
	}
	
	public void randomize() throws IOException, URISyntaxException {
		String agent = Randomizer.randomize();
		if(agent.equals("?")) {
			changeImage("Questionmark");
			nameText.setText("None");
		}else {
			while(agent.equals(lastAgent) && repeatsCheckbox.isSelected() && Randomizer.selectedAgents.size() > 1) {
				agent = Randomizer.randomize();
			}
			changeImage(agent);
			nameText.setText(agent);
			lastAgent = agent;
		}
	}
	
	private void changeImage(String agent) throws URISyntaxException {
		String directory = String.format("src/Images/%s.png",agent);
		try
		{
			Image image = new Image((new File(directory).toURI().toString()));
			characterImageDisplay.setImage(image);
		}
		catch (Exception e)
		{
		    //String workingDir = System.getProperty("user.dir");
		    //System.out.println("Current working directory : " + workingDir);
		    e.printStackTrace();
		}
	}
	
	public void selectAll() {
		for(int i = 0; i < characterCheckBoxes.size(); i++) {
			characterCheckBoxes.get(i).setSelected(true);
	        Randomizer.addAgent(Randomizer.allAgents.get(i));
		}
	}
	
	public void deselectAll() {
		for(int i = 0; i < characterCheckBoxes.size(); i++) {
			characterCheckBoxes.get(i).setSelected(false);
	        Randomizer.removeAgent(Randomizer.allAgents.get(i));
		}
	}
	
	private void makeAllAgentCheckBoxes() {
		checkBoxHolder = new VBox();
		checkBoxHolder.setSpacing(5);
		int index = 0;
		for(Iterator<String> agentsIt = Randomizer.allAgents.iterator(); agentsIt.hasNext();) {
			int ID = index;
			String agent = agentsIt.next();
			CheckBox agentCheckBox = new CheckBox(agent);
			characterCheckBoxes.add(agentCheckBox);
			characterCheckBoxes.get(ID).setStyle("-fx-font: 15 arial;");
			characterCheckBoxes.get(ID).setAllowIndeterminate(false);
			characterCheckBoxes.get(ID).setOnAction(new EventHandler<ActionEvent>() {
			    @Override 
			    public void handle(ActionEvent e) {
			    	String AID = agent;
			        if(characterCheckBoxes.get(ID).isSelected()) {
			        	Randomizer.addAgent(AID);
			        } else {
			        	Randomizer.removeAgent(AID);
			        }
			    }
			});
			checkBoxHolder.getChildren().add(characterCheckBoxes.get(index));
			index++;
		}
		characterSelectScrollPane.setContent(checkBoxHolder);
		
	}
	
}
