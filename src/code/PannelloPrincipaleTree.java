package code;

import java.io.IOException;
import java.net.URL;
import java.util.Random;
import java.util.ResourceBundle;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

public class PannelloPrincipaleTree implements Initializable {

    Parent parent;
    Stage stage;
    Scene scene;
    private double mouseX, mouseY; // Store the initial mouse click position

    @FXML
    private Pane pane;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private TextArea log;

    @FXML
    private Slider slider;

    // private double currentZoom = 1.0; // Valore di zoom iniziale

    static int indici = 0;
    private Circle selectedPallino; // Variabile per memorizzare il pallino selezionato
    private Node selectedNode; // Variabile per memorizzare il nodo selezionato
    Tree tree = new Tree();

    public void onStart() {
        Circle pallino = new Circle(20, Color.BLUE);
        pallino.setCenterX(350);
        pallino.setCenterY(40);

        Node root = new Node(0, pallino);
        tree.addNode(root);

        Text numberText = new Text("0");
        numberText.setFill(Color.WHITE);
        numberText.setX(pallino.getCenterX() - 5);
        numberText.setY(pallino.getCenterY() + 5);
        pane.getChildren().addAll(pallino, numberText);

        pallino.setOnMouseClicked(event -> {
            log.appendText("Root " + root.getIndiceNodo() + "\n");
            log.appendText(
                    "Root's childern: " + root.getPuntatoreFiglioSx() + " - " + root.getPuntatoreFiglioDx() + "\n");
            pallino.setFill(Color.GREEN);
            selectedPallino = pallino;
            selectedNode = root;
        });

        pallino.setOnMousePressed(e -> {
            mouseX = e.getSceneX() - pallino.getCenterX();
            mouseY = e.getSceneY() - pallino.getCenterY();
        });

        pallino.setOnMouseDragged(e -> {
            double newCircleX = e.getSceneX() - mouseX;
            double newCircleY = e.getSceneY() - mouseY;

            pallino.setCenterX(newCircleX);
            pallino.setCenterY(newCircleY);

            numberText.setX(newCircleX - 5);
            numberText.setY(newCircleY + 5);

            for (int i = 0; i < selectedNode.getSizeVicini(); i++) {
                selectedNode.getLine(i).setStartX(selectedPallino.getCenterX());
                selectedNode.getLine(i).setStartY(selectedPallino.getCenterY());
                selectedNode.getLine(i).setEndX(selectedNode.getVicino(i).getCenterX());
                selectedNode.getLine(i).setEndY(selectedNode.getVicino(i).getCenterY());
            }
        });
    }

    @FXML
    void buttonInstruction(ActionEvent event) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("How to use the app");
        alert.setHeaderText(null);

        String bullet = "\u2022"; // Codice Unicode per il carattere del punto

        String contentText = bullet
                + "To insert a node select a node and click either 'Insert right node' or 'Insert left node'";

        alert.setContentText(contentText);

        String customStyle = "-fx-font-size: 16px;";
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setStyle(customStyle);

        alert.showAndWait();
    }

    DFS dfs = new DFS();
    BFS bfs = new BFS();

    @FXML
    void buttonIndietro(ActionEvent event) throws IOException {
        buttonReset(event);
        parent = FXMLLoader.load(getClass()
                .getResource("../resources/PannelloPrincipale.fxml"));
        stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        scene = new Scene(parent, 900, 700);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.setTitle("Interfaccia principale");
        stage.show();
    }

    @FXML
    void buttonDFS(ActionEvent event) {
        log.appendText("---------------------\nStarting DFS...\n");
        dfs.executeDFS(tree.getRoot(), tree, slider.valueProperty().doubleValue(), log);
    }

    @FXML
    void buttonBFS(ActionEvent event) {
        log.appendText("---------------------\nStarting BFS...\n");
        bfs.executeBFS(tree.getRoot(), tree, slider.valueProperty().doubleValue(), log);
    }

    void generateLeftNode() {
        Circle node = new Circle(20, Color.BLUE);
        Node figlio = new Node(++indici, node);
        // selectedPallino = figlio.circle;
        node.setCenterX(selectedPallino.getCenterX() - 55);
        node.setCenterY(selectedPallino.getCenterY() + 60);

        tree.addNode(figlio);
        selectedNode.set_nNodes();

        selectedNode.setFiglioSx(figlio.getIndiceNodo());
        Text numberText = new Text(Integer.toString(figlio.getIndiceNodo()));
        numberText.setFill(Color.WHITE);
        numberText.setX(node.getCenterX() - 5);
        numberText.setY(node.getCenterY() + 5);
        pane.getChildren().addAll(node, numberText);
        log.appendText("Add node " + figlio.getIndiceNodo() + "\n");
        node.setOnMouseClicked(e -> {
            selectedNode = figlio;
            log.appendText("Nodo " + figlio.getIndiceNodo() + "\n");
            log.appendText("Node's childrens " + selectedNode.getPuntatoreFiglioSx() + " - "
                    + selectedNode.getPuntatoreFiglioDx() + "\n");

            node.setFill(Color.GREEN);
            selectedPallino.setFill(Color.BLUE);
            selectedPallino = node;
        });

        // Disegna la linea che connette i nodi
        Line connectionLine = new Line(
                selectedPallino.getCenterX(), selectedPallino.getCenterY(),
                node.getCenterX(), node.getCenterY());

        selectedNode.setLine(connectionLine);
        figlio.setLine(connectionLine);
        selectedNode.setVicino(figlio);
        figlio.setVicino(selectedNode);

        pane.getChildren().add(connectionLine);
        connectionLine.toBack();

        node.setOnMousePressed(e -> {
            mouseX = e.getSceneX() - node.getCenterX();
            mouseY = e.getSceneY() - node.getCenterY();
        });

        node.setOnMouseDragged(e -> {
            double newCircleX = e.getSceneX() - mouseX;
            double newCircleY = e.getSceneY() - mouseY;

            node.setCenterX(newCircleX);
            node.setCenterY(newCircleY);

            numberText.setX(newCircleX - 5);
            numberText.setY(newCircleY + 5);

            for (int i = 0; i < selectedNode.getSizeVicini(); i++) {
                selectedNode.getLine(i).setStartX(selectedPallino.getCenterX());
                selectedNode.getLine(i).setStartY(selectedPallino.getCenterY());
                selectedNode.getLine(i).setEndX(selectedNode.getVicino(i).getCenterX());
                selectedNode.getLine(i).setEndY(selectedNode.getVicino(i).getCenterY());
            }
        });
    }

    @FXML
    void buttonInsertLeftNode(ActionEvent event) {
        if (selectedNode == null) {
            log.appendText("First select a node\nwhere to insert a node\n");
            return;
        }
        if (selectedNode.getPuntatoreFiglioSx() != 0) {
            log.appendText("The node has already\na left children\n");
            return;
        }
        generateLeftNode();
    }

    void generateRightNode() {
        Circle node = new Circle(20, Color.BLUE); // Crea il pallino colorato

        node.setCenterX(selectedPallino.getCenterX() + 55);
        node.setCenterY(selectedPallino.getCenterY() + 60);
        Node figlio = new Node(++indici, node); // Crea il nodo
        tree.addNode(figlio); // Lo aggiunge all'albero

        selectedNode.set_nNodes();
        selectedNode.setVicino(figlio);
        figlio.setVicino(selectedNode);

        selectedNode.setFiglioDx(figlio.getIndiceNodo());
        Text numberText = new Text(Integer.toString(figlio.getIndiceNodo()));
        numberText.setFill(Color.WHITE);
        numberText.setX(node.getCenterX() - 5); // Imposta la posizione X del testo all'interno del cerchio
        numberText.setY(node.getCenterY() + 5);

        pane.getChildren().addAll(node, numberText);

        log.appendText("Add node " + figlio.getIndiceNodo() + "\n");
        node.setOnMouseClicked(e -> {
            selectedNode = figlio;
            log.appendText("Nodo " + figlio.getIndiceNodo() + "\n");
            log.appendText("Node's childrens " + selectedNode.getPuntatoreFiglioSx() + " - "
                    + selectedNode.getPuntatoreFiglioDx() + "\n");

            node.setFill(Color.GREEN);
            selectedPallino.setFill(Color.BLUE);
            selectedPallino = node;
        });

        // Disegna la linea che connette i nodi
        Line connectionLine = new Line(
                selectedPallino.getCenterX(), selectedPallino.getCenterY(),
                node.getCenterX(), node.getCenterY());

        selectedNode.setLine(connectionLine);
        figlio.setLine(connectionLine);

        pane.getChildren().add(connectionLine);
        connectionLine.toBack();
        
        node.setOnMousePressed(e -> {
            mouseX = e.getSceneX() - node.getCenterX();
            mouseY = e.getSceneY() - node.getCenterY();
        });

        node.setOnMouseDragged(e -> {
            double newCircleX = e.getSceneX() - mouseX;
            double newCircleY = e.getSceneY() - mouseY;

            node.setCenterX(newCircleX);
            node.setCenterY(newCircleY);

            numberText.setX(newCircleX - 5);
            numberText.setY(newCircleY + 5);

            for (int i = 0; i < selectedNode.getSizeVicini(); i++) {
                selectedNode.getLine(i).setStartX(selectedPallino.getCenterX());
                selectedNode.getLine(i).setStartY(selectedPallino.getCenterY());
                selectedNode.getLine(i).setEndX(selectedNode.getVicino(i).getCenterX());
                selectedNode.getLine(i).setEndY(selectedNode.getVicino(i).getCenterY());
            }
        });
    }

    @FXML
    void buttonInsertRightNode(ActionEvent event) {
        if (selectedNode == null) {
            log.appendText("First select a node\nwhere to insert a node\n");
            return;
        }
        if (selectedNode.getPuntatoreFiglioDx() != 0) {
            log.appendText("The node has already\na right children\n");
            return;
        }
        generateRightNode();
    }

    @FXML
    void buttonReset(ActionEvent event) {
        tree.deleteTree();
        pane.getChildren().clear();
        log.clear();

        // Reimposta le variabili allo stato iniziale
        indici = 0;
        selectedPallino = null;
        selectedNode = null;
        tree = new Tree();

        // Avvia nuovamente la scena
        onStart();
    }

    private Timeline timeline;

    @FXML
    void buttonRandom(ActionEvent event) {
        buttonReset(event);
        Random random = new Random();

        log.appendText("Generating a tree...\n");
        timeline = new Timeline();

        int randomDim = random.nextInt(20) + 30;
        selectedNode = tree.getRoot();
        selectedPallino = selectedNode.circle;
        generateLeftNode();
        generateRightNode();

        double frameDurationMillis = 15; // Adjust frame duration in milliseconds as needed

        for (int i = 0; i < randomDim; i++) {
            KeyFrame keyFrame = new KeyFrame(Duration.millis(i * frameDurationMillis), e -> {
                Node node = tree.selectRandomNode();
                selectedNode = node;
                selectedPallino = selectedNode.circle;

                if (random.nextInt(2) == 0 && selectedNode.getPuntatoreFiglioSx() == 0
                        && tree.verificaNodo(selectedNode)) {
                    generateLeftNode();
                } else {
                    if (selectedNode.getPuntatoreFiglioDx() == 0 && tree.verificaNodo(selectedNode)) {
                        generateRightNode();
                    }
                }
            });
            timeline.getKeyFrames().add(keyFrame);

        }
        timeline.setOnFinished(e -> {
            log.appendText("Tree generated\n");
        });
        timeline.setCycleCount(1);
        timeline.play();
    }

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        onStart();
    }
}