
import java.io.*;
import java.nio.file.*;
import java.util.*;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.text.*;
import javafx.scene.control.Alert.*;
import javafx.stage.*;

//Application
public class Simpletron extends Application {

	private final int READ = 10;
	private final int WRITE = 11;
	private final int LOAD = 20;
	private final int STORE = 21;
	private final int ADD = 30;
	private final int SUBTRACT = 31;
	private final int DIVIDE = 32;
	private final int MULTIPLY = 33;
	private final int BRANCH = 40;
	private final int BRANCHNEG = 41;
	private final int BRANCHZERO = 42;
	private final int HALT = 43;

	private int array[] = new int[100]; // memory array
	private int instCounter = 0; // instruction counter
	private int instSize = 0; // no. of instructions
	private int instructionReg = 0; // instruction register
	private Scanner input; // scanner to read from a file
	private int accumulator = 0; // accumulator
	private int opcode = 0; // operation code
	private int operand = 0; // operand code
	private boolean found = false; // find HALT

	// list for storing the grid position where 0 is placed to output only single
	// '0' instead of four
	private List<Integer> list = new ArrayList<>();

	private GridPane gPane;
	private TextField tf1;
	private TextField tf2;
	private TextField tf3;
	private TextField tf4;
	private TextField tf5;

	// main method
	public static void main(String[] args) {
		launch(args);
	}

	// start method
	@Override
	public void start(Stage stage) {

		Background bg = new Background(new BackgroundFill(Color.LIGHTGRAY, null, null));
		Background bg2 = new Background(new BackgroundFill(Color.LIGHTCYAN, null, null));
		Background bg4 = new Background(new BackgroundFill(Color.ALICEBLUE, null, null));

		gPane = new GridPane();
		// adding labels and their respective values in the grid
		for (int i = 0; i < 11; i++) {

			for (int j = 0; j < 11; j++) {

				if (i == 0 && j != 0) {
					Label l = new Label(String.format("%02d", 10 * (j - 1)));
					l.setFont(Font.font(20));
					l.setTextFill(Color.BLACK);
					l.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
					l.setAlignment(Pos.CENTER);
					l.setBackground(bg2);
					gPane.add(l, i, j);
				} else if (j == 0 && i != 0) {
					Label l = new Label(String.format("%d", i - 1));
					l.setFont(Font.font(20));
					l.setTextFill(Color.BLACK);
					l.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
					l.setAlignment(Pos.CENTER);
					l.setBackground(bg2);
					gPane.add(l, i, j);
				} else if (j != 0 && i != 0) {
					Label l = new Label(String.format("%04d", 0));
					l.setFont(Font.font(17));
					l.setTextFill(Color.BLACK);
					l.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
					l.setAlignment(Pos.CENTER);
					l.setBackground(bg4);
					l.setOnMouseEntered(e -> l.setFont(Font.font(20)));
					l.setOnMouseExited(e -> l.setFont(Font.font(17)));
					gPane.add(l, i, j);
				} else {
					Label l = new Label("Memory");
					l.setFont(Font.font(20));
					l.setTextFill(Color.BLACK);
					l.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
					l.setAlignment(Pos.CENTER);
					l.setBackground(bg2);
					gPane.add(l, i, j);
				}
			}
		}

		// auto resizing of the grid
		ColumnConstraints c = new ColumnConstraints();
		c.setPercentWidth(10);
		RowConstraints r = new RowConstraints();
		r.setPercentHeight(10);
		gPane.getColumnConstraints().addAll(c, c, c, c, c, c, c, c, c, c, c);
		gPane.getRowConstraints().addAll(r, r, r, r, r, r, r, r, r, r, r);
		gPane.setAlignment(Pos.CENTER);
		gPane.setGridLinesVisible(true);

		// for top horizontal bar
		Label lb1 = new Label("Accumulator");
		Label lb2 = new Label("InstCounter");
		Label lb3 = new Label("InstReg");
		Label lb4 = new Label("OpCode");
		Label lb5 = new Label("Operand");

		for (Label lb : new Label[] { lb1, lb2, lb3, lb4, lb5 }) {
			lb.setFont(Font.font(null, FontWeight.SEMI_BOLD, 17));
			lb.setTextFill(Color.BLACK);
		}

		tf1 = new TextField(String.format("%d", 0));
		tf2 = new TextField(String.format("%02d", 0));
		tf3 = new TextField(String.format("%04d", 0));
		tf4 = new TextField(String.format("%02d", 0));
		tf5 = new TextField(String.format("%02d", 0));

		TextField[] tfs = { tf1, tf2, tf3, tf4, tf5 };

		for(int i = 0; i < tfs.length; i++) {
			tfs[i].setFont(Font.font(17));
			tfs[i].setFocusTraversable(false);
			tfs[i].setAlignment(Pos.CENTER);
			tfs[i].setEditable(false);
			tfs[i].setCursor(Cursor.DEFAULT);

			if(i == 0)
				tfs[i].setMaxWidth(100);
			else if(i == 2)
				tfs[i].setMaxWidth(80);	
			else
				tfs[i].setMaxWidth(40);		
			
		}

		HBox hBox = new HBox(10, lb1, tf1, lb2, tf2, lb3, tf3, lb4, tf4, lb5, tf5);
		hBox.setAlignment(Pos.CENTER);
		hBox.setBackground(bg);
		hBox.setMinHeight(50);

		// for bottom horizontal bar
		Button b1 = new Button("Load Program");
		b1.setFont(Font.font(null, FontWeight.SEMI_BOLD, 17));

		Button b2 = new Button("Execute Next Instruction");
		b2.setFont(Font.font(null, FontWeight.SEMI_BOLD, 17));
		b2.setDisable(true);

		RadioButton r1 = new RadioButton("Exe one inst");
		r1.setFont(Font.font(null, FontWeight.SEMI_BOLD, 17));
		// r1.setSelected(true);
		RadioButton r2 = new RadioButton("Exe program");
		r2.setFont(Font.font(null, FontWeight.SEMI_BOLD, 17));
		r2.setSelected(true);
		ToggleGroup tg = new ToggleGroup();
		r1.setToggleGroup(tg);
		r2.setToggleGroup(tg);

		HBox hBox2 = new HBox(10, b1, b2, r1, r2);
		hBox2.setAlignment(Pos.CENTER);
		hBox2.setBackground(bg);
		hBox2.setMinHeight(50);

		// root node border pane
		BorderPane bPane = new BorderPane();
		bPane.setTop(hBox);
		bPane.setBottom(hBox2);
		bPane.setCenter(gPane);

		// button 1 functionality -> read instructions from a file and store in the
		// array
		b1.setOnAction(lmda -> {
			try {
				FileChooser fileChooser = new FileChooser();
				fileChooser.setTitle("Open Instructions Text File");
				fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
				File file = fileChooser.showOpenDialog(null);
				input = new Scanner(file);

				if (file == null) {
					throw new FileNotFoundException("No file selected!");
				}
				else if (Files.size(Paths.get(file.getPath())) == 0) {
					throw new IllegalArgumentException("File is empty!");
				} else {
					b2.setDisable(false);
					reset();

					while (input.hasNext()) {
						int a = input.nextInt();
						if (a < -9999 || a > 9999) {
							throw new IllegalArgumentException(String.format("%s%n%s",
									"Invalid instruction in the file!",
									"Instruction must be between -9999 and 9999 inclusive"));
						}
						array[instSize] = a;
						instSize++;
					}
					int x = 0;
					for (int i = 1; i < 11; i++) {

						for (int j = 1; j < 11; j++) {

							Label l = (Label) gPane.getChildren().get(j * 11 + i);
							l.setText(String.format("%04d", array[x]));
							x++;
						}
					}
				}
			} catch (IOException | NullPointerException | InputMismatchException | IllegalStateException
					| IllegalArgumentException e) {

				Alert alert = new Alert(AlertType.ERROR);
				if (e.getClass().getName().equals("java.util.InputMismatchException"))
					alert.setContentText("File contains invalid data!");
				else if (e.getClass().getName().equals("java.lang.NullPointerException"))
					alert.setContentText("No file selected!");
				else
					alert.setContentText(e.getMessage());
				// b2.setDisable(true);
				alert.show();
			}

		});

		// button 2 functionality -> execute program with respect to the radio button choosed
		b2.setOnAction(lmda2 -> {
			Alert alert = new Alert(AlertType.ERROR);
			try {
				if (r2.isSelected()) {
					executeAll();
				} else {
					executeOne();
					instCounter++;
				}
			} catch (IllegalArgumentException | IllegalStateException e) {
				alert.setContentText(String.format("%s%n%s", e.getMessage(),
						"Simpletron execution abnormally terminated!"));
				alert.showAndWait();
				b2.setDisable(true);
			}
			if (found == true) {
				b2.setDisable(true);
				alert.setAlertType(AlertType.INFORMATION);
				alert.setContentText("Simpletron execution completed successfully!");
				alert.show();
			}

		});

		// create scene
		Scene scene = new Scene(bPane, 1100, 600);

		// set and show stage
		stage.setScene(scene);
		stage.setTitle("Simpletron");
		stage.show();

	}

	// execute all the remaining instructions at once
	private void executeAll() {

		for (; instCounter < instSize; instCounter++) {

			executeOne();

			// if HALT or default is found, break
			if (found == true)
				break;

		}

	}

	// execute all the remaining instructions one at a time
	private void executeOne() {

		instructionReg = array[instCounter];
		opcode = Math.abs(instructionReg) / 100; // operation code
		operand = Math.abs(instructionReg) % 100; // operand code

		tf2.setText(String.format("%02d", instCounter + 1));
		tf3.setText(String.format("%04d", instructionReg));
		tf4.setText(String.format("%02d", opcode));
		tf5.setText(String.format("%02d", operand));

		switch (opcode) {
			// input/output operations
			case READ: // Read a word into a specific location in memory
				TextInputDialog dialog = new TextInputDialog();
				boolean found2 = true;
				do {
					try {
						dialog.setHeaderText("Enter a number");
						dialog.setTitle("Input Dialog");
						Optional<String> res = dialog.showAndWait();
						res.ifPresentOrElse(e -> array[operand] = Integer.parseInt(e),
								() -> {
									throw new IllegalArgumentException("No input, Try again!");
								});

						if (array[operand] < -9999 || array[operand] > 9999) {
							throw new IllegalArgumentException(String.format("%s%n%s%n%s", "Invalid input!",
									"Number must be between -9999 and 9999 inclusive.", "Try again!"));
						}
						found2 = false;
						if (array[operand] == 0) {
							array[operand] = 10000;
						}
					} catch (IllegalArgumentException e) {
						dialog.getEditor().clear();
						Alert alert = new Alert(AlertType.ERROR);
						if (e.getClass().getName().equals("java.lang.NumberFormatException"))
							alert.setContentText(String.format("%s%n%s%n%s",
									"Invalid input!", "Enter only integer value.", "Try again!"));
						else
							alert.setContentText(e.getMessage());
						alert.showAndWait();
					}
				} while (found2);

				output();

				break;

			case WRITE:// Write a word from a specific location in memory
				Alert a = new Alert(AlertType.INFORMATION);
				a.setTitle("Output");
				a.setHeaderText(String.format("%38s  :  %d", "Result", array[operand]));
				a.showAndWait();
				break;

			// load/store operations
			case LOAD: // Load a word from a specific location in memory into the accumulator.
				accumulator = array[operand];
				tf1.setText(String.format("%d", accumulator));
				break;

			case STORE: // Store a word from the accumulator into a specific location in memory.
				array[operand] = accumulator;
				if (array[operand] == 0) {
					array[operand] = 10000;
				}
				output();
				break;

			// arithmetic operations
			case ADD: // Add a word from a specific location in memory to the word in the accumulator
				accumulator += array[operand];
				tf1.setText(String.format("%d", accumulator));
				if (accumulator < -9999 || accumulator > 9999) {
					throw new IllegalStateException("Accumulator overflowed!");
				}

				break;

			case SUBTRACT: // Subtract a word from a specific location in memory from the word in the
							// accumulator
				accumulator -= array[operand];
				tf1.setText(String.format("%d", accumulator));
				if (accumulator < -9999 || accumulator > 9999) {
					throw new IllegalStateException("Accumulator overflowed!");
				}
				break;

			case DIVIDE: // Divide a word from a specific location in memory into the word in the
							// accumulator
				if (array[operand] == 0) {
					throw new IllegalArgumentException("Attempt to divide by 0!");
				}
				accumulator /= array[operand];
				tf1.setText(String.format("%d", accumulator));
				break;

			case MULTIPLY: // Multiply a word from a specific location in memory by the word in the
							// accumulator
				accumulator *= array[operand];
				tf1.setText(String.format("%d", accumulator));
				if (accumulator < -9999 || accumulator > 9999) {
					throw new IllegalStateException("Accumulator overflowed!");
				}
				break;

			// transfer of control operations
			case BRANCH: // Branch to a specific location in memory
				instCounter = operand - 1; // subtracting 1 so that it cancels with the for loop increment
				tf2.setText(String.format("%02d", instCounter + 1));
				break;

			case BRANCHNEG: // Branch to a specific location in memory if the accumulator is negative
				if (accumulator < 0)
					instCounter = operand - 1;
				tf2.setText(String.format("%02d", instCounter + 1));
				break;

			case BRANCHZERO: // Branch to a specific location in memory if the accumulator is zero
				if (accumulator == 0)
					instCounter = operand - 1;
				tf2.setText(String.format("%02d", instCounter + 1));
				break;

			case HALT: // Halt. The program has completed its task
				found = true;
				break;

			default:
				throw new IllegalArgumentException("Invalid instruction!");
		}

	}

	// output data to the grid
	private void output() {

		int x = 0;
		for (int i = 1; i < 11; i++) {

			for (int j = 1; j < 11; j++) {

				Label l = (Label) gPane.getChildren().get(j * 11 + i);
				if (array[x] != 0 && array[x] != 10000)
					l.setText(String.format("%d", array[x]));

				else if (array[x] == 10000) {
					list.add(j * 11 + i);
					array[x] = 0;
				} else
					l.setText(String.format("%04d", array[x]));

				x++;
				if (!list.isEmpty()) {
					for (int o = 0; o < list.size(); o++) {
						Label l2 = (Label) gPane.getChildren().get(list.get(o));
						l2.setText(String.format("%d", 0));
					}
				}
			}
		}
	}

	// reset data after a new valid file is opened
	private void reset() {

		for (int i = 1; i < 11; i++) {

			for (int j = 1; j < 11; j++) {

				Label l = (Label) gPane.getChildren().get(j * 11 + i);
				l.setText(String.format("%04d", 0));
			}
		}

		tf1.setText(String.format("%d", 0));
		tf2.setText(String.format("%02d", 0));
		tf3.setText(String.format("%04d", 0));
		tf4.setText(String.format("%02d", 0));
		tf5.setText(String.format("%02d", 0));
		accumulator = 0;
		instructionReg = 0;
		instCounter = 0;
		instSize = 0;
		opcode = 0;
		operand = 0;
		found = false;
		Arrays.fill(array, 0);
		list.clear();
	}
}
