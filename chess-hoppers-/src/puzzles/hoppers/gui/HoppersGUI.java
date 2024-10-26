package puzzles.hoppers.gui;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import puzzles.common.Observer;
import puzzles.hoppers.model.HoppersConfig;
import puzzles.hoppers.model.HoppersModel;

import javafx.application.Application;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
/**
 * the GUI part of the Hoppers puzzle
 * @author Andy Zheng Zheng
 * @github arz4166
 */

public class HoppersGUI extends Application implements Observer<HoppersModel, String> {
    /** The size of all icons, in square dimension */
    private final static int ICON_SIZE = 75;
    /** the font size for labels and buttons */
    private final static int FONT_SIZE = 12;

    /** The resources directory is located directly underneath the gui package */
    private final static String RESOURCES_DIR = "resources/";

    /** fields for the images **/
    private Image redFrog = new Image(getClass().getResourceAsStream(RESOURCES_DIR+"red_frog.png"));
    private Image greenFrog = new Image(getClass().getResourceAsStream(RESOURCES_DIR+"green_frog.png"));
    private Image lilyPad = new Image(getClass().getResourceAsStream(RESOURCES_DIR+"lily_pad.png"));
    private Image water = new Image(getClass().getResourceAsStream(RESOURCES_DIR+"water.png"));

    private ImageView lilypadview = new ImageView(lilyPad);
    private ImageView redfrogview = new ImageView(redFrog);
    private ImageView greenfrogview = new ImageView(greenFrog);
    private ImageView waterview = new ImageView(water);
    /** the main borderPane **/
    private BorderPane main;
    /** the stage **/
    private Stage stage;
    /** the model **/
    private HoppersModel model;
    /** the message displayed at top **/
    private Label topmsg;
    /** if you are selecting first coord **/
    private boolean from;
    /** the start row **/
    private int startRow;
    /** the start column **/
    private int startCol;

    /**
     * initializes the model and gui
     * @throws IOException when file cant be loaded
     */
    public void init() throws IOException {
        String filename = getParameters().getRaw().get(0);
        File f = new File(filename);
        this.model = new HoppersModel(filename);
        topmsg = new Label("Loaded: " +f.getName());
        model.addObserver(this);
    }

    /**
     * sets up the GUI
     * @param stage the primary stage for this application, onto which
     * the application scene can be set.
     * Applications may create other stages, if needed, but they will not be
     * primary stages.
     * @throws Exception
     */
    @Override
    public void start(Stage stage) throws Exception {
        this.stage = stage;
        main = new BorderPane();
        main.setTop(topmsg);
        main.setCenter(makeCenter());
        main.setBottom(makeLoadHintReset());
        Scene scene = new Scene(main);
        stage.setScene(scene);
        stage.setTitle("Hoppers GUI");
        stage.show();
    }

    /**
     * this makes the center part of the main borderPane. like the display of frogs and lilypads
     * @return the gridPane representing the center
     */
    private GridPane makeCenter(){
        from = true;
        GridPane center = new GridPane();
       int numrows = HoppersConfig.rows;
       int numcols = HoppersConfig.columns;
       for(int row = 0; row <numrows ; row++){
           for(int col = 0; col < numcols; col++){
               int buttonRow = row;
               int buttonCol = col;
               Button button = new Button();
               button.setMinSize(ICON_SIZE,ICON_SIZE);
               button.setMaxSize(ICON_SIZE,ICON_SIZE);
               if(model.getCurrentConfig().grid[row][col].equals("G")) {
                   button.setGraphic(new ImageView(greenFrog));
                   button.setOnAction(event -> selectGUI(buttonRow,buttonCol) );

               }
               if(model.getCurrentConfig().grid[row][col].equals("R")) {
                   button.setGraphic(new ImageView(redFrog));
                   button.setOnAction(event -> selectGUI(buttonRow,buttonCol) );

               }
               if(model.getCurrentConfig().grid[row][col].equals(".")) {
                   button.setGraphic(new ImageView(lilyPad));
                   button.setOnAction(event -> selectGUI(buttonRow,buttonCol) );

               }
               if(model.getCurrentConfig().grid[row][col].equals("*")) {
                   button.setGraphic(new ImageView(water));
                   button.setOnAction(event -> selectGUI(buttonRow,buttonCol) );

               }
               center.add(button,col,row);
           }
       }
       center.setAlignment(Pos.CENTER);
       return center;
    }

    /**
     * creates a Hbox with load, hint, and reset button.
     * @return Hbox with load, hint, and reset button.
     */
    private HBox makeLoadHintReset(){
        HBox hbox = new HBox();
        Button load = new Button("Load");
        Button reset = new Button("Reset");
        Button hint = new Button("Hint");

        //set action for load
        load.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setInitialDirectory(new File(System.getProperty("user.dir") +"/data/hoppers"));

            // make it so that only textFiles will be displayed
            fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Text Files", "*.txt"));

            File selectedFile = fileChooser.showOpenDialog(this.stage);
            if (selectedFile != null) {
                model.loadFile(selectedFile.getPath());
                topmsg.setText("Loaded: " + selectedFile.getName());
            }
        });
        //set action for reset
        reset.setOnAction(event -> {
            try {
                model.reset();
            } catch (IOException e) {
            }
        });

        // set action for hint
        hint.setOnAction(event -> model.hint());
        hbox.setAlignment(Pos.CENTER);
        hbox.getChildren().addAll(load,reset,hint);
        return hbox;
    }

    /**
     * helper method that checks if the move (from ... to ... ) is valid
     * @param buttonRow the row you're starting
     * @param buttonCol the column you're starting
     */
    private void selectGUI(int buttonRow, int buttonCol){
        if(from){ //if you are choosing first coordinate
            startRow = buttonRow;
            startCol = buttonCol;
            if(!(model.checkMoveFrom(startRow, startCol))){
                topmsg.setText("Invalid Selection ("+ startRow + "," + startCol + ")" );
            }
            else{
                topmsg.setText("Selected ("+ startRow + "," + startCol + ")" );
                from = false;
            }
        }
        else{
            int destRow = buttonRow;
            int destCol = buttonCol;
            model.validjump(startRow,startCol,destRow,destCol);
            if(model.canjump) {
                topmsg.setText("Jumped from (" + startRow + "," + startCol + ") to (" + destRow + "," + destCol + ")");
            }
            else{
                topmsg.setText("Can't jump from (" + startRow + "," + startCol + ") to (" + destRow + "," + destCol + ")");
            from = true;

        }
    }

    }

    /**
     * the update method for observers
     * @param hoppersModel the object that wishes to inform this object
     *                about something that has happened.
     * @param msg optional data the server.model can send to the observer
     *
     */
    @Override
    public void update(HoppersModel hoppersModel, String msg) {

        topmsg.setText(msg);
        model = hoppersModel;
        main.setCenter(makeCenter());
        this.stage.sizeToScene();
        // when a different sized puzzle is loaded
    }

    /**
     * the main method of the GUI
     * @param args command line arguments
     */
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java HoppersPTUI filename");
        } else {
            Application.launch(args);
        }
    }

}
