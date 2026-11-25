package gh.ucc.smartroute;

import gh.ucc.smartroute.data.CampusDataLoader;
import gh.ucc.smartroute.graph.WeightedGraph;
import gh.ucc.smartroute.model.Location;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class App extends Application {
    private WeightedGraph graph;
    private WebView mapView;
    private Label status;

    @Override
    public void start(Stage stage) {
        try {
            graph = CampusDataLoader.loadFromResources();
        } catch (IOException exception) {
            showStartupError(exception);
            return;
        }

        mapView = new WebView();
        mapView.getEngine().load(getClass().getResource("/map.html").toExternalForm());
        mapView.getEngine().getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
            if (newState == javafx.concurrent.Worker.State.SUCCEEDED) renderMarkers();
        });

        ComboBox<Location> startBox = locationPicker();
        ComboBox<Location> endBox = locationPicker();
        List<Location> locations = List.copyOf(graph.getLocations());
        startBox.setItems(FXCollections.observableArrayList(locations));
        endBox.setItems(FXCollections.observableArrayList(locations));
        if (!locations.isEmpty()) {
            startBox.getSelectionModel().selectFirst();
            endBox.getSelectionModel().selectLast();
        }

        Button routeButton = new Button("Find shortest route");
        routeButton.setDefaultButton(true);
        routeButton.setOnAction(event -> findRoute(startBox.getValue(), endBox.getValue()));

        Button bfsButton = new Button("Show BFS");
        bfsButton.setOnAction(event -> showTraversal(startBox.getValue(), true));
        Button dfsButton = new Button("Show DFS");
        dfsButton.setOnAction(event -> showTraversal(startBox.getValue(), false));

        status = new Label("Campus markers loaded. Pedestrian connections still need verification.");
        status.setWrapText(true);

        HBox controls = new HBox(10,
                new Label("From:"), startBox,
                new Label("To:"), endBox,
                routeButton, bfsButton, dfsButton);
        controls.setPadding(new Insets(12));
        controls.setStyle("-fx-background-color: #f4f7fb;");

        BorderPane root = new BorderPane();
        root.setTop(controls);
        root.setCenter(mapView);
        root.setBottom(status);
        BorderPane.setMargin(status, new Insets(10));

        stage.setTitle("UCC SmartRoute — Group 28");
        stage.setScene(new Scene(root, 1200, 760));
        stage.show();
    }

    private ComboBox<Location> locationPicker() {
        ComboBox<Location> picker = new ComboBox<>();
        picker.setPrefWidth(190);
        picker.setConverter(new StringConverter<>() {
            @Override public String toString(Location location) {
                return location == null ? "" : location.name();
            }
            @Override public Location fromString(String value) { return null; }
        });
        return picker;
    }

    private void renderMarkers() {
        String markers = graph.getLocations().stream()
                .map(location -> String.format(
                        "{id:'%s',name:'%s',lat:%s,lon:%s}",
                        jsEscape(location.id()), jsEscape(location.name()),
                        location.latitude(), location.longitude()))
                .collect(Collectors.joining(","));
        mapView.getEngine().executeScript("showLocations([" + markers + "])");
    }

    private void findRoute(Location start, Location end) {
        if (start == null || end == null) {
            status.setText("Select both a starting point and a destination.");
            return;
        }
        var route = graph.shortestPath(start.id(), end.id());
        if (route.isEmpty()) {
            status.setText("No verified pedestrian route is available between "
                    + start.name() + " and " + end.name()
                    + ". The team must confirm the walkway and distance before adding a graph edge.");
            mapView.getEngine().executeScript("clearRoute()");
            return;
        }

        List<Location> path = route.get().locations();
        String points = path.stream()
                .map(location -> String.format("[%s,%s]", location.latitude(), location.longitude()))
                .collect(Collectors.joining(","));
        mapView.getEngine().executeScript("drawRoute([" + points + "])");
        String names = path.stream().map(Location::name).collect(Collectors.joining(" → "));
        status.setText(String.format("Shortest verified route: %s | Total distance: %.0f m",
                names, route.get().totalDistanceMetres()));
    }

    private void showTraversal(Location start, boolean breadthFirst) {
        if (start == null) {
            status.setText("Select a starting location first.");
            return;
        }
        List<Location> order = breadthFirst
                ? graph.breadthFirstSearch(start.id())
                : graph.depthFirstSearch(start.id());
        String names = order.stream().map(Location::name).collect(Collectors.joining(" → "));
        status.setText((breadthFirst ? "BFS" : "DFS") + " traversal from " + start.name()
                + ": " + names + ". Only connected locations are visited.");
    }

    private String jsEscape(String value) {
        return value.replace("\\", "\\\\").replace("'", "\\'");
    }

    private void showStartupError(Exception exception) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("UCC SmartRoute");
        alert.setHeaderText("Could not load campus data");
        alert.setContentText(exception.getMessage());
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
