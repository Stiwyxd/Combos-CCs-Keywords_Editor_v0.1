import javafx.application.Application;
import javafx.application.HostServices;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.stage.FileChooser;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.effect.DropShadow;
import javafx.animation.FadeTransition;
import javafx.util.Duration;
import java.io.*;
import java.util.concurrent.*;

public class ComboEditorApp extends Application {

    private String comboFilePath = "";
    private String keywordsFilePath = "";
    private String creditCardFilePath = "";
    private TextArea logArea;
    private String currentStyle = "milky.css";
    private Scene scene;
    private String customBackgroundColor = "#F5F5F5";
    private String customTextColor = "#333333";
    private String customButtonColor = "#E0E0E0";
    private String customAccentColor = "#B0E0E6";
    private VBox sidebar; 
    private HostServices hostServices; 

    @Override
    public void init() {
        hostServices = getHostServices();
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("ULTIMATE EDITOR v0.1");
        primaryStage.setWidth(1200);
        primaryStage.setHeight(800);

        BorderPane root = new BorderPane();
        LinearGradient gradient = new LinearGradient(0, 0, 1, 1, true, javafx.scene.paint.CycleMethod.NO_CYCLE,
                new Stop(0, Color.web(customBackgroundColor)), new Stop(1, Color.web("#E0E0E0")));
        root.setBackground(new Background(new BackgroundFill(gradient, CornerRadii.EMPTY, Insets.EMPTY)));

        sidebar = createSidebar(primaryStage);
        root.setLeft(sidebar);

        TabPane tabPane = new TabPane();
        tabPane.setStyle("-fx-tab-min-height: 35; -fx-tab-max-height: 35;");
        Tab comboTab = new Tab("Combo");
        comboTab.setContent(createComboContent());
        comboTab.setClosable(false);
        Tab keywordTab = new Tab("Keywords");
        keywordTab.setContent(createKeywordContent());
        keywordTab.setClosable(false);
        Tab creditCardTab = new Tab("Credit Cards");
        creditCardTab.setContent(createCreditCardContent());
        creditCardTab.setClosable(false);
        tabPane.getTabs().addAll(comboTab, keywordTab, creditCardTab);
        root.setCenter(tabPane);

        scene = new Scene(root);
        try {
            scene.getStylesheets().add(getClass().getResource(currentStyle).toExternalForm());
        } catch (Exception e) {
            System.err.println("Failed to load CSS: " + e.getMessage());
        }
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private VBox createSidebar(Stage primaryStage) {
        VBox sidebar = new VBox(10);
        sidebar.setPrefWidth(100);
        sidebar.setPadding(new Insets(20));
        if (currentStyle.equals("milky.css")) {
            sidebar.setStyle("-fx-background-color: #99b8b6; -fx-border-color: " + customAccentColor + "; -fx-border-width: 0 1 0 0; -fx-effect: none;");
        } else {
            sidebar.setStyle("-fx-background-color: rgba(255, 255, 255, 0.9); -fx-border-color: " + customAccentColor + "; -fx-border-width: 0 1 0 0; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 0);");
        }

        Hyperlink home = new Hyperlink("Home");
        home.setStyle("-fx-text-fill: " + customAccentColor + "; -fx-font-size: 14px; -fx-font-family: 'Arial';");
        addShadowEffect(home);
        home.setOnAction(e -> showNotification("Home", "Welcome to ULTIMATE EDITOR v0.1!"));

        Hyperlink projects = new Hyperlink("Projects");
        projects.setStyle("-fx-text-fill: " + customAccentColor + "; -fx-font-size: 14px; -fx-font-family: 'Arial';");
        addShadowEffect(projects);
        projects.setOnAction(e -> showNotification("Projects", "View your projects here."));

        Hyperlink settings = new Hyperlink("Settings");
        settings.setStyle("-fx-text-fill: " + customAccentColor + "; -fx-font-size: 14px; -fx-font-family: 'Arial';");
        addShadowEffect(settings);
        settings.setOnAction(e -> showSettings(primaryStage));

        VBox footer = new VBox(5);
        footer.setAlignment(Pos.BOTTOM_CENTER);

        Hyperlink githubLink = new Hyperlink("GitHub");
        githubLink.setStyle("-fx-text-fill: " + customAccentColor + "; -fx-font-size: 12px; -fx-font-family: 'Arial';");
        githubLink.setOnAction(e -> openLink("https://github.com/Stiwyxd"));

        Hyperlink telegramLink = new Hyperlink("Telegram");
        telegramLink.setStyle("-fx-text-fill: " + customAccentColor + "; -fx-font-size: 12px; -fx-font-family: 'Arial';");
        telegramLink.setOnAction(e -> openLink("https://t.me/Stiwy_Xd")); 

        footer.getChildren().addAll(githubLink, telegramLink);

        sidebar.getChildren().addAll(home, projects, settings, footer);
        sidebar.setAlignment(Pos.TOP_CENTER);
        VBox.setVgrow(footer, Priority.ALWAYS); 
        return sidebar;
    }

    private void openLink(String url) {
        try {
            hostServices.showDocument(url);
        } catch (Exception e) {
            Platform.runLater(() -> showNotification("Error", "Failed to open link: " + (e.getMessage() != null ? e.getMessage() : "Unknown error")));
        }
    }

    private VBox createComboContent() {
        VBox content = new VBox(20);
        content.setPadding(new Insets(20));
        content.setStyle("-fx-background-color: rgba(240, 240, 240, 0.95); -fx-border-color: " + customAccentColor + "; -fx-border-radius: 15; -fx-background-radius: 15; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 15, 0, 0, 0);");

        HBox fileSection = createFileSection("combo");
        GridPane optionsGrid = createOptionsGrid(new String[] {
            "1) Delete Passwords", "2) Delete Emails", "3) Remove Duplicates",
            "4) Sort Lines", "5) Remove Bad Lines", "6) Combine Combos",
            "7) Split Combos", "8) Extract USER:PASS", "9) USER:PASS to PASS:USER",
            "10) Soft Randomize Combos", "11) Hard Randomize Combos", "12) Sort Domains(+1mio)",
            "13) Split by Domain"
        }, "combo");

        logArea = new TextArea();
        logArea.setPrefHeight(200);
        logArea.setEditable(false);
        logArea.setStyle("-fx-control-inner-background: #FFFFFF; -fx-text-fill: " + customTextColor + ";");

        content.getChildren().addAll(fileSection, optionsGrid, logArea);
        content.setAlignment(Pos.CENTER);
        VBox.setVgrow(logArea, Priority.ALWAYS);
        return content;
    }

    private VBox createKeywordContent() {
        VBox content = new VBox(20);
        content.setPadding(new Insets(20));
        content.setStyle("-fx-background-color: rgba(240, 240, 240, 0.95); -fx-border-color: " + customAccentColor + "; -fx-border-radius: 15; -fx-background-radius: 15; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 15, 0, 0, 0);");

        HBox fileSection = createFileSection("keyword");
        GridPane optionsGrid = createOptionsGrid(new String[] {
            "1) Remove Duplicates", "2) Sort Lines", "3) Remove Bad Lines"
        }, "keyword");

        content.getChildren().addAll(fileSection, optionsGrid);
        content.setAlignment(Pos.CENTER);
        return content;
    }

    private VBox createCreditCardContent() {
        VBox content = new VBox(20);
        content.setPadding(new Insets(20));
        content.setStyle("-fx-background-color: rgba(240, 240, 240, 0.95); -fx-border-color: " + customAccentColor + "; -fx-border-radius: 15; -fx-background-radius: 15; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 15, 0, 0, 0);");

        HBox fileSection = createFileSection("creditcard");
        GridPane optionsGrid = createOptionsGrid(new String[] {
            "1) Extract Creditcard Data", "2) Sort Lines", "3) Remove Duplicates"
        }, "creditcard");

        content.getChildren().addAll(fileSection, optionsGrid);
        content.setAlignment(Pos.CENTER);
        return content;
    }

    private HBox createFileSection(String tabType) {
        HBox fileSection = new HBox(10);
        TextField fileField = new TextField();
        fileField.setPromptText("No file selected");
        fileField.setPrefWidth(200);
        fileField.setStyle("-fx-background-color: #FFFFFF; -fx-text-fill: " + customTextColor + "; -fx-prompt-text-fill: derive(" + customTextColor + ", 50%);");
        Button browseButton = new Button("Browse");
        browseButton.setStyle("-fx-background-color: " + customButtonColor + "; -fx-text-fill: " + customTextColor + ";");
        addShadowEffect(browseButton);
        String finalTabType = tabType;
        browseButton.setOnAction(e -> {
            String filePath = fileDialog();
            if (filePath != null) {
                fileField.setText(filePath);
                if (finalTabType.equals("combo")) comboFilePath = filePath;
                else if (finalTabType.equals("keyword")) keywordsFilePath = filePath;
                else creditCardFilePath = filePath;
                showNotification("File Selected", "Successfully selected " + new File(filePath).getName());
            }
        });
        fileSection.getChildren().addAll(fileField, browseButton);
        fileSection.setAlignment(Pos.CENTER);
        return fileSection;
    }

    private GridPane createOptionsGrid(String[] optionLabels, String tabType) {
        GridPane optionsGrid = new GridPane();
        optionsGrid.setHgap(15);
        optionsGrid.setVgap(15);
        for (int i = 0; i < optionLabels.length; i++) {
            final String option = optionLabels[i];
            Button button = new Button(option);
            button.setStyle("-fx-background-color: " + customButtonColor + "; -fx-text-fill: " + customTextColor + ";");
            button.setTooltip(new Tooltip("Click to perform " + option.toLowerCase().replace(")", "")));
            addShadowEffect(button);
            button.setOnAction(e -> handleOption(option, tabType));
            optionsGrid.add(button, i % 3, i / 3);
        }
        optionsGrid.setAlignment(Pos.CENTER);
        return optionsGrid;
    }

    private void addShadowEffect(Control node) {
        DropShadow shadow = new DropShadow(10, Color.web(customAccentColor));
        node.setEffect(shadow);
        node.setOnMouseEntered(e -> shadow.setRadius(15));
        node.setOnMouseExited(e -> shadow.setRadius(10));
    }

    private void showSettings(Stage primaryStage) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Settings");
        dialog.setHeaderText("Customize ULTIMATE EDITOR v0.1");

        VBox content = new VBox(15);
        content.setPadding(new Insets(20));
        content.setStyle("-fx-background-color: rgba(245, 245, 245, 0.95); -fx-border-color: " + customAccentColor + "; -fx-border-radius: 10; -fx-background-radius: 10; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 0);");

        Label themeLabel = new Label("Select Theme");
        themeLabel.setStyle("-fx-text-fill: " + customTextColor + "; -fx-font-size: 14px;");
        ToggleGroup themeGroup = new ToggleGroup();
        RadioButton classy = new RadioButton("Classy");
        classy.setToggleGroup(themeGroup);
        classy.setUserData("classy.css");
        RadioButton neon = new RadioButton("Neon");
        neon.setToggleGroup(themeGroup);
        neon.setUserData("neon.css");
        RadioButton tesla = new RadioButton("Tesla Motors");
        tesla.setToggleGroup(themeGroup);
        tesla.setUserData("tesla.css");
        RadioButton milky = new RadioButton("Milky");
        milky.setToggleGroup(themeGroup);
        milky.setUserData("milky.css");
        milky.setSelected(true);

        Label colorLabel = new Label("Customize Colors:");
        colorLabel.setStyle("-fx-text-fill: " + customTextColor + "; -fx-font-size: 14px;");
        HBox bgColorBox = new HBox(10);
        Label bgColorLabel = new Label("Background Color:");
        bgColorLabel.setStyle("-fx-text-fill: " + customTextColor + "; -fx-font-size: 12px;");
        ColorPicker bgColorPicker = new ColorPicker(Color.web(customBackgroundColor));
        bgColorBox.getChildren().addAll(bgColorLabel, bgColorPicker);

        HBox textColorBox = new HBox(10);
        Label textColorLabel = new Label("Text Color:");
        textColorLabel.setStyle("-fx-text-fill: " + customTextColor + "; -fx-font-size: 12px;");
        ColorPicker textColorPicker = new ColorPicker(Color.web(customTextColor));
        textColorBox.getChildren().addAll(textColorLabel, textColorPicker);

        HBox buttonColorBox = new HBox(10);
        Label buttonColorLabel = new Label("Button Color:");
        buttonColorLabel.setStyle("-fx-text-fill: " + customTextColor + "; -fx-font-size: 12px;");
        ColorPicker buttonColorPicker = new ColorPicker(Color.web(customButtonColor));
        buttonColorBox.getChildren().addAll(buttonColorLabel, buttonColorPicker);

        HBox accentColorBox = new HBox(10);
        Label accentColorLabel = new Label("Accent Color:");
        accentColorLabel.setStyle("-fx-text-fill: " + customTextColor + "; -fx-font-size: 12px;");
        ColorPicker accentColorPicker = new ColorPicker(Color.web(customAccentColor));
        accentColorBox.getChildren().addAll(accentColorLabel, accentColorPicker);

        content.getChildren().addAll(themeLabel, classy, neon, tesla, milky, colorLabel, bgColorBox, textColorBox, buttonColorBox, accentColorBox);

        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                String selectedStyle = (String) themeGroup.getSelectedToggle().getUserData();
                if (!currentStyle.equals(selectedStyle)) {
                    currentStyle = selectedStyle;
                    scene.getStylesheets().clear();
                    try {
                        scene.getStylesheets().add(getClass().getResource(currentStyle).toExternalForm());
                    } catch (Exception e) {
                        System.err.println("Failed to load CSS: " + e.getMessage());
                    }
                    showNotification("Theme Changed", "Switched to " + currentStyle.replace(".css", "") + " theme!");
                    
                    if (currentStyle.equals("milky.css")) {
                        sidebar.setStyle("-fx-background-color: #99b8b6; -fx-border-color: " + customAccentColor + "; -fx-border-width: 0 1 0 0; -fx-effect: none;");
                    } else {
                        sidebar.setStyle("-fx-background-color: rgba(255, 255, 255, 0.9); -fx-border-color: " + customAccentColor + "; -fx-border-width: 0 1 0 0; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 0);");
                    }
                }

                customBackgroundColor = "#" + bgColorPicker.getValue().toString().substring(2, 8);
                customTextColor = "#" + textColorPicker.getValue().toString().substring(2, 8);
                customButtonColor = "#" + buttonColorPicker.getValue().toString().substring(2, 8);
                customAccentColor = "#" + accentColorPicker.getValue().toString().substring(2, 8);

                updateCustomStyles();
                showNotification("Colors Updated", "Custom colors have been applied!");
            }
        });
    }

    private void updateCustomStyles() {
        
        if (currentStyle.equals("milky.css")) {
            sidebar.setStyle("-fx-background-color: #99b8b6; -fx-border-color: " + customAccentColor + "; -fx-border-width: 0 1 0 0; -fx-effect: none;");
        } else {
            sidebar.setStyle("-fx-background-color: rgba(255, 255, 255, 0.9); -fx-border-color: " + customAccentColor + "; -fx-border-width: 0 1 0 0; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 0);");
        }
        for (var child : sidebar.getChildren()) {
            if (child instanceof Hyperlink) {
                ((Hyperlink) child).setStyle("-fx-text-fill: " + customAccentColor + "; -fx-font-size: " + (child.getId() != null && child.getId().equals("footer-link") ? "12px" : "14px") + "; -fx-font-family: 'Arial';");
            }
        }

        TabPane tabPane = (TabPane) ((BorderPane) scene.getRoot()).getCenter();
        for (Tab tab : tabPane.getTabs()) {
            VBox content = (VBox) tab.getContent();
            content.setStyle("-fx-background-color: rgba(240, 240, 240, 0.95); -fx-border-color: " + customAccentColor + "; -fx-border-radius: 15; -fx-background-radius: 15; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 15, 0, 0, 0);");

            for (var child : content.getChildren()) {
                if (child instanceof HBox) { 
                    for (var subChild : ((HBox) child).getChildren()) {
                        if (subChild instanceof TextField) {
                            ((TextField) subChild).setStyle("-fx-background-color: #FFFFFF; -fx-text-fill: " + customTextColor + "; -fx-prompt-text-fill: derive(" + customTextColor + ", 50%);");
                        } else if (subChild instanceof Button) {
                            ((Button) subChild).setStyle("-fx-background-color: " + customButtonColor + "; -fx-text-fill: " + customTextColor + ";");
                        }
                    }
                } else if (child instanceof GridPane) { 
                    for (var subChild : ((GridPane) child).getChildren()) {
                        if (subChild instanceof Button) {
                            ((Button) subChild).setStyle("-fx-background-color: " + customButtonColor + "; -fx-text-fill: " + customTextColor + ";");
                            DropShadow shadow = (DropShadow) ((Button) subChild).getEffect();
                            if (shadow != null) {
                                shadow.setColor(Color.web(customAccentColor));
                            }
                        }
                    }
                } else if (child instanceof TextArea) { 
                    ((TextArea) child).setStyle("-fx-control-inner-background: #FFFFFF; -fx-text-fill: " + customTextColor + ";");
                }
            }
        }

       
        BorderPane root = (BorderPane) scene.getRoot();
        LinearGradient gradient = new LinearGradient(0, 0, 1, 1, true, javafx.scene.paint.CycleMethod.NO_CYCLE,
                new Stop(0, Color.web(customBackgroundColor)), new Stop(1, Color.web("#E0E0E0")));
        root.setBackground(new Background(new BackgroundFill(gradient, CornerRadii.EMPTY, Insets.EMPTY)));
    }

    private String fileDialog() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
        File file = fileChooser.showOpenDialog(null);
        return file != null ? file.getAbsolutePath() : null;
    }

    private void handleOption(String option, String tabType) {
        String filePath;
        if (tabType.equals("combo")) {
            filePath = comboFilePath;
        } else if (tabType.equals("keyword")) {
            filePath = keywordsFilePath;
        } else {
            filePath = creditCardFilePath;
        }

        if (filePath.isEmpty()) {
            showAlert("Error", "Please select a " + tabType + " file first!");
            return;
        }

        logArea.appendText("Processing: " + option + " on " + new File(filePath).getName() + "\n");
        FadeTransition ft = new FadeTransition(Duration.millis(500), logArea);
        ft.setFromValue(0.0);
        ft.setToValue(1.0);
        ft.play();

        String functionName = null;
        switch (option.toLowerCase().replaceAll("[0-9 )]+", "")) { 
            case "delete passwords": functionName = "password_remover"; break;
            case "delete emails": functionName = "email_remover"; break;
            case "remove duplicates": functionName = "remove_duplicate"; break;
            case "sort lines": functionName = "sort_lines"; break;
            case "remove bad lines": functionName = "removelines"; break;
            case "combine combos": functionName = "combine_combos"; break;
            case "split combos": functionName = "splitter"; break;
            case "extract user:pass": functionName = "extracter"; break;
            case "user:pass to pass:user": functionName = "passuser"; break;
            case "soft randomize combos": functionName = "soft_mix"; break;
            case "hard randomize combos": functionName = "hard_mix"; break;
            case "sort domains(+1mio)": functionName = "domain_sorter"; break;
            case "split by domain": functionName = "split_by_domain"; break;
            case "extract creditcard data": functionName = "cc_extractor"; break;
            default: logArea.appendText("Option not implemented yet.\n"); return;
        }

        if (functionName.equals("splitter")) {
            final int[] parts = new int[1];
            TextInputDialog dialog = new TextInputDialog("4");
            dialog.setTitle("Split Combos");
            dialog.setHeaderText("Enter the number of parts to split into:");
            dialog.setContentText("Number of parts:");
            dialog.showAndWait().ifPresent(numParts -> {
                try {
                    int value = Integer.parseInt(numParts);
                    if (value <= 0) {
                        showAlert("Error", "Number of parts must be positive!");
                        return;
                    }
                    parts[0] = value;
                } catch (NumberFormatException e) {
                    showAlert("Error", "Please enter a valid number!");
                }
            });
            if (parts[0] > 0) {
                executePythonScript(functionName, filePath, tabType, option, String.valueOf(parts[0]));
            }
        } else if (functionName.equals("combine_combos")) {
            String secondFilePath = fileDialog();
            if (secondFilePath != null) {
                executePythonScript(functionName, filePath, tabType, option, secondFilePath);
            } else {
                showAlert("Error", "No second file selected for combining!");
            }
        } else {
            executePythonScript(functionName, filePath, tabType, option, "1"); 
        }
    }

    private void executePythonScript(String functionName, String filePath, String tabType, String option, String extraArg) {
        Process process = null;
        try {
            Runtime runtime = Runtime.getRuntime();
            long usedMemoryBefore = (runtime.totalMemory() - runtime.freeMemory()) / 1024 / 1024;
            logArea.appendText("Memory usage before operation: " + usedMemoryBefore + " MB\n");

            ProcessBuilder pb = new ProcessBuilder("python-embeddable\\python.exe", "backend.py", functionName, filePath, tabType, extraArg);
            pb.redirectErrorStream(true);
            process = pb.start();
            final Process finalProcess = process;

            FutureTask<String> future = new FutureTask<>(() -> {
                StringBuilder result = new StringBuilder();
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(finalProcess.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        result.append(line).append("\n");
                    }
                }
                return result.toString();
            });
            new Thread(future).start();

            int exitCode = process.waitFor();
            String result = future.get(10, TimeUnit.SECONDS);
            if (exitCode == 0) {
                logArea.appendText(result);
                String message = getSuccessMessage(option, result);
                showNotification("Success", message);
                if (functionName.equals("remove_duplicate") || functionName.equals("sort_lines") ||
                    functionName.equals("passuser") || functionName.equals("split_by_domain") || functionName.equals("splitter") ||
                    functionName.equals("combine_combos")) {
                    displayResult(filePath, tabType, functionName);
                }
            } else {
                logArea.appendText("Error: Python script exited with code " + exitCode + "\n" + result);
                showNotification("Error", "Operation failed with exit code " + exitCode);
            }

            long usedMemoryAfter = (runtime.totalMemory() - runtime.freeMemory()) / 1024 / 1024;
            logArea.appendText("Memory usage after operation: " + usedMemoryAfter + " MB\n");
        } catch (InterruptedException e) {
            logArea.appendText("Operation interrupted: " + (e.getMessage() != null ? e.getMessage() : "Unknown error") + "\n");
            showNotification("Error", "Operation interrupted: " + (e.getMessage() != null ? e.getMessage() : "Unknown error"));
            Thread.currentThread().isInterrupted(); 
        } catch (ExecutionException | TimeoutException e) {
            logArea.appendText("Error executing Python script: " + (e.getMessage() != null ? e.getMessage() : "Unknown error") + "\n");
            showNotification("Error", "Failed to execute Python script: " + (e.getMessage() != null ? e.getMessage() : "Unknown error"));
        } catch (IOException e) {
            logArea.appendText("IO Error: " + (e.getMessage() != null ? e.getMessage() : "Unknown error") + "\n");
            showNotification("Error", "IO failure: " + (e.getMessage() != null ? e.getMessage() : "Unknown error"));
        } finally {
            if (process != null) {
                process.destroy();
            }
        }
    }

    private void displayResult(String filePath, String tabType, String functionName) {
        try {
            if (functionName.equals("split_by_domain")) {
                String baseName = new File(filePath).getName();
                File dir = new File(".");
                for (File file : dir.listFiles()) {
                    if (file.getName().endsWith("_" + baseName)) {
                        String content = previewFile(file.toPath(), 100);
                        logArea.appendText("Preview of " + file.getName() + " (first 100 lines):\n" + content + "\n");
                    }
                }
            } else if (functionName.equals("splitter")) {
                File dir = new File(".");
                for (File file : dir.listFiles()) {
                    if (file.getName().startsWith("split_") && file.getName().endsWith(".txt")) {
                        String content = previewFile(file.toPath(), 100);
                        logArea.appendText("Preview of " + file.getName() + " (first 100 lines):\n" + content + "\n");
                    }
                }
            } else if (functionName.equals("combine_combos")) {
                String content = previewFile(new File("combined.txt").toPath(), 100);
                logArea.appendText("Preview of Result (first 100 lines):\n" + content + "\n");
            } else {
                String resultFile = functionName.equals("passuser") ? "converted.txt" :
                        functionName.equals("sort_lines") ? "sorted_" + (tabType.equals("combo") ? "combo.txt" : tabType + ".txt") :
                        functionName.equals("remove_duplicate") ? "cleared.txt" : tabType + "_cleared.txt";
                String content = previewFile(new File(resultFile).toPath(), 100);
                logArea.appendText("Preview of Result (first 100 lines):\n" + content + "\n");
            }
        } catch (Exception e) {
            logArea.appendText("Error reading result file: " + (e.getMessage() != null ? e.getMessage() : "Unknown error") + "\n");
            showNotification("Error", "Failed to read result file: " + (e.getMessage() != null ? e.getMessage() : "Unknown error"));
        }
    }

    private String previewFile(java.nio.file.Path path, int maxLines) throws IOException {
        StringBuilder content = new StringBuilder();
        int lineCount = 0;
        try (BufferedReader reader = java.nio.file.Files.newBufferedReader(path)) {
            String line;
            while ((line = reader.readLine()) != null && lineCount < maxLines) {
                content.append(line).append("\n");
                lineCount++;
            }
        }
        if (lineCount == maxLines) {
            content.append("... (showing first ").append(maxLines).append(" lines only)\n");
        }
        return content.toString();
    }

    private String getSuccessMessage(String option, String result) {
        int duplicatesDeleted = 0;
        if (option.contains("Remove Duplicates")) {
            try {
                String[] lines = result.split("\n");
                for (String line : lines) {
                    if (line.contains("Duplicates removed successfully")) {
                        String originalFile = (option.contains("Combo") ? comboFilePath : keywordsFilePath);
                        long originalLines = countLines(originalFile);
                        String resultFile = "cleared.txt";
                        long resultLines = countLines(resultFile);
                        duplicatesDeleted = (int) (originalLines - resultLines);
                        break;
                    }
                }
            } catch (Exception e) {
                duplicatesDeleted = -1;
            }
            return duplicatesDeleted >= 0 ?
                   "Removed " + duplicatesDeleted + " duplicates successfully!" :
                   "Duplicates removed, but count failed.";
        }
        switch (option.toLowerCase().replaceAll("[0-9 )]+", "")) {
            case "delete passwords": return "Passwords deleted successfully!";
            case "delete emails": return "Emails deleted successfully!";
            case "remove duplicates": return "Duplicates removed successfully!";
            case "sort lines": return "Lines sorted successfully!";
            case "remove bad lines": return "Bad lines removed successfully!";
            case "combine combos": return "Combos combined successfully!";
            case "split combos": return "Combos split into parts successfully!";
            case "extract user:pass": return "USER:PASS extracted successfully!";
            case "user:pass to pass:user": return "USER:PASS converted to PASS:USER successfully!";
            case "soft randomize combos": return "Combos soft randomized successfully!";
            case "hard randomize combos": return "Combos hard randomized successfully!";
            case "sort domains(+1mio)": return "Domains sorted successfully!";
            case "split by domain": return "Split by domain successfully!";
            case "extract creditcard data": return "Credit card data extracted successfully!";
            default: return "Operation completed!";
        }
    }

    private long countLines(String filePath) {
        try {
            return java.nio.file.Files.lines(java.nio.file.Paths.get(filePath)).count();
        } catch (IOException e) {
            return 0;
        }
    }

    private void showNotification(String title, String message) {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle(title);
        dialog.setHeaderText(null);
        dialog.setContentText(message);
        dialog.getDialogPane().setStyle("-fx-background-color: rgba(245, 245, 245, 0.95); -fx-text-fill: " + customTextColor + "; -fx-font-family: 'Arial'; -fx-font-size: 14px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 0);");
        ButtonType okButton = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().add(okButton);
        dialog.showAndWait();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.getDialogPane().setStyle("-fx-background-color: rgba(245, 245, 245, 0.95); -fx-text-fill: " + customTextColor + "; -fx-font-family: 'Arial'; -fx-font-size: 14px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 0);");
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
