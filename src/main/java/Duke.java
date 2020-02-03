import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

/**
 * The Duke program implements an application that is able to keep track of a task list. A user can populate the list
 * with tasks inputted through the standard output, mainly Todo tasks, Deadline tasks and Event tasks, or delete tasks
 * from the list.
 */
public class Duke extends Application {
    private ScrollPane scrollPane;
    private VBox dialogContainer;
    private TextField userInput;
    private Button sendButton;
    private Scene scene;

    private Storage storage;
    private TaskList tasks;
    private Ui ui;

    // just an empty constructor
    public Duke() {

    }

    /**
     * Creates a Ui object to deal with user interaction , a Storage object to deal with loading or saving tasks and a
     * new TaskList object, loaded with the file storing the task list, if the file exists.
     *
     * @param filePath location where file which contains task list is found or created
     */
    public Duke(String filePath) {
        ui = new Ui();
        storage = new Storage(filePath);
        try {
            tasks = new TaskList(storage.load());
        } catch (DukeException | IOException exception) {
            ui.printLoadingError();
            tasks = new TaskList();
        }
    }

    /**
     * Runs the Duke program.
     *
     * @throws IOException if there is error handling data in the file.
     */
    public void run() throws IOException {
        ui.printWelcome();
        Scanner sc = new Scanner(System.in);
        String command = sc.nextLine();
        Parser parser = new Parser(tasks.getList());
        while (!command.equals("bye")) {
            try {
                parser.parse(command);
                command = sc.nextLine();
            } catch (DukeException exception) {
                ui.printError(exception);
                command = sc.nextLine();
            }
        }
        // update task list before exiting
        BufferedWriter writer = new BufferedWriter(new FileWriter(storage.getFile()));
        for (Task task: tasks.getList()) {
            writer.write(task.updateFile() + "\n");
        }
        writer.flush();
        ui.printExitLine();
    }

    @Override
    public void start(Stage stage) {
        //Step 1. Setting up required components

        //The container for the content of the chat to scroll.
        scrollPane = new ScrollPane();
        dialogContainer = new VBox();
        scrollPane.setContent(dialogContainer);

        userInput = new TextField();
        sendButton = new Button("Send");

        AnchorPane mainLayout = new AnchorPane();
        mainLayout.getChildren().addAll(scrollPane, userInput, sendButton);

        scene = new Scene(mainLayout);

        stage.setScene(scene);
        stage.show();

        //Step 2. Formatting the window to look as expected
        stage.setTitle("Duke");
        stage.setResizable(false);
        stage.setMinHeight(600.0);
        stage.setMinWidth(400.0);

        mainLayout.setPrefSize(400.0, 600.0);

        scrollPane.setPrefSize(385, 535);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);

        scrollPane.setVvalue(1.0);
        scrollPane.setFitToWidth(true);

        // You will need to import `javafx.scene.layout.Region` for this.
        dialogContainer.setPrefHeight(Region.USE_COMPUTED_SIZE);

        userInput.setPrefWidth(325.0);

        sendButton.setPrefWidth(55.0);

        AnchorPane.setTopAnchor(scrollPane, 1.0);

        AnchorPane.setBottomAnchor(sendButton, 1.0);
        AnchorPane.setRightAnchor(sendButton, 1.0);

        AnchorPane.setLeftAnchor(userInput , 1.0);
        AnchorPane.setBottomAnchor(userInput, 1.0);

        // more code to be added here later
    }

    public static void main(String[] args) throws IOException {
        new Duke("./src/main/data/duke.txt").run();
    }
}
