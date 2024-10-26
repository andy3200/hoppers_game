package puzzles.chess.gui;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import puzzles.common.Coordinates;
import puzzles.common.Observer;
import puzzles.chess.model.ChessModel;
import java.io.File;
import java.io.IOException;

/**
 * the gui part of chess
 * @author KUNLIN WEN
 * @github KW9521
 */
public class ChessGUI extends Application implements Observer<ChessModel, String> {

    /** The resources directory is located directly underneath the gui package */
    private final static String RESOURCES_DIR = "resources/";

    private Image bishop = new Image(getClass().getResourceAsStream(RESOURCES_DIR+"bishop.png"));
    private Image king = new Image(getClass().getResourceAsStream(RESOURCES_DIR+"king.png"));
    private Image knight = new Image(getClass().getResourceAsStream(RESOURCES_DIR+"knight.png"));
    private Image pawn = new Image(getClass().getResourceAsStream(RESOURCES_DIR+"pawn.png"));
    private Image queen = new Image(getClass().getResourceAsStream(RESOURCES_DIR+"queen.png"));
    private Image rook = new Image(getClass().getResourceAsStream(RESOURCES_DIR+"rook.png"));


    /** a definition of light and dark and for the button backgrounds */
    private static final Background WHITE =
            new Background( new BackgroundFill(Color.WHITE, null, null));
    private static final Background BLUE =
            new Background( new BackgroundFill(Color.MIDNIGHTBLUE, null, null));

    private Stage stage;
    private ChessModel model;
    /** the overall game...has status, chess board and the bottom load reset hint buttons**/
    private BorderPane bigBorderPane;
    /** status message **/
    private Label topMsg;
    /** the chess board with buttons **/
    private GridPane chessBoard;
    /** load reset hint buttons in a hbox **/
    private HBox loadResetHint;
    private Coordinates selectedCoord;
    private int selectCounter = 1; // keeps track of which select user is up to, odd = from and even = to


    /**
     * makes new model, sets top msg to loaded, adds this obj as an observer of model
     * @throws IOException
     */
    @Override
    public void init() throws IOException {
        // get the file name from the command line
        String filename = getParameters().getRaw().get(0);
        this.model = new ChessModel(filename);

        File file = new File(filename);
        topMsg = new Label("Loaded: " +file.getName());
        model.addObserver(this);
    }

    /**
     * creates the whole gui board, sets title, makes new scene and shows scene
     * calls helper methods to construct the top label, the chess board and the hint load reset buttons
     * @param stage the primary stage for this application, onto which
     * the application scene can be set.
     * Applications may create other stages, if needed, but they will not be
     * primary stages.
     * @throws Exception
     */
    @Override
    public void start(Stage stage) throws Exception {
        bigBorderPane = new BorderPane();
        chessBoard = makeChessBoard();
        loadResetHint = loadResetHint();

        bigBorderPane.setTop(topMsg);
        bigBorderPane.setCenter(chessBoard);
        bigBorderPane.setBottom(loadResetHint);

        this.stage = stage;
        Scene scene = new Scene(bigBorderPane);
        stage.setTitle("Chess GUI");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * the actual chess board with all the buttons and chess pieces
     * @return
     */
    public GridPane makeChessBoard() {
        GridPane gp = new GridPane();
        int counter = 1;    // keep track of which background color

        for (int row = 0; row < model.rowDim; row++) {
            for (int col = 0; col < model.colDim; col++) {

                Button b = new Button();

                // sets the background of the button
                if (counter % 2 == 1) {
                    b.setBackground(WHITE);
                    b.setPrefSize(100, 100);
                    counter++;
                } else {
                    b.setBackground(BLUE);
                    b.setPrefSize(100, 100);
                    counter++;
                }

                // adds the chess piece images on top and setOnAction all buttons
                int finalRow = row;
                int finalCol = col;
                switch (model.getCurrConfig().grid[row][col]) {
                    case ('K') -> {
                        b.setGraphic(new ImageView(king));
                        b.setOnAction(event -> selectTwo(finalRow, finalCol));
                    }
                    case ('N') -> {
                        b.setGraphic(new ImageView(knight));
                        b.setOnAction(event -> selectTwo(finalRow, finalCol));
                    }
                    case ('P') -> {
                        b.setGraphic(new ImageView(pawn));
                        b.setOnAction(event -> selectTwo(finalRow, finalCol));
                    }
                    case ('B') -> {
                        b.setGraphic(new ImageView(bishop));
                        b.setOnAction(event -> selectTwo(finalRow, finalCol));
                    }
                    case ('R') -> {
                        b.setGraphic(new ImageView(rook));
                        b.setOnAction(event -> selectTwo(finalRow, finalCol));
                    }
                    case ('Q') -> {
                        b.setGraphic(new ImageView(queen));
                        b.setOnAction(event -> selectTwo(finalRow, finalCol));
                    }
                    default -> b.setOnAction(event -> selectTwo(finalRow, finalCol));
                }
                gp.add(b, col, row);
            }

            // if counter at last colum is even, make next row counter also even
            // chess-5.txt at (0,4) after counter++ is even but want (1,0) to also be an even # so it can alternate colors
            if (model.colDim % 2 == 1){
                counter+=2;
            } else {
                counter++;
            }

        }
        return gp;
    }

    /**
     * helper function for when user selects 2 coordinates
     * @param finalRow row of button user selected
     * @param finalCol col of button user selected
     */
    public void selectTwo(int finalRow, int finalCol){

        // the starting/from coordinate
        if (selectCounter %2 == 1){
            selectedCoord = new Coordinates(finalRow, finalCol);

            // if starting coordinate does not have a chess piece on grid
            if(model.startCoordNoPiece(selectedCoord)){
                topMsg.setText("Invalid Selection: " +selectedCoord);

            } else {
                topMsg.setText("Selected: " +selectedCoord);
                selectCounter++;
            }
        }

        // alr selected something, this one is the "to"
        else{
            Coordinates toThisCoord = new Coordinates(finalRow, finalCol);
            model.select(selectedCoord, toThisCoord);
            selectCounter++;
        }
    }

    /**
     * the load reset and hint buttons and their actions
     * @return hbox with load -> reset -> hint positioned in the center
     */
    public HBox loadResetHint() {
        HBox hbox = new HBox();

        // load button
        Button load = new Button("Load");
        load.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setInitialDirectory(new File(System.getProperty("user.dir") +"/data/chess"));

            // Add a filter to show only text files
            fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Text Files", "*.txt"));

            // Show the file chooser dialog and wait for user response
            File selectedFile = fileChooser.showOpenDialog(this.stage);

            // Check if the user selected a file
            if (selectedFile != null) {
                model.loadFile(selectedFile.getPath());
                topMsg.setText("Loaded "+selectedFile.getName());
                this.stage.sizeToScene();
            }
        });

        // reset button
        Button reset = new Button("Reset");
        reset.setOnAction(event -> {
            try {
                model.reset();
            } catch (IOException e) {}
        });

        // hint button
        Button hint = new Button("Hint");
        hint.setOnAction(event -> model.hint());

        hbox.setAlignment(Pos.CENTER);
        hbox.getChildren().addAll(load, reset, hint);

        return hbox;

    }

    /**
     * updates the gui whenever an action has been done
     * @param chessModel the object that wishes to inform this object about something that has happened.
     * @param msg optional data the server.model can send to the observer
     */
    @Override
    public void update(ChessModel chessModel, String msg) {
        topMsg.setText(msg);
        model = chessModel;
        bigBorderPane.setCenter(makeChessBoard());
        this.stage.sizeToScene();
    }

    /**
     * launches the gui
     * @param args command line arguments
     */
    public static void main(String[] args) {
        Application.launch(args);
    }
}

