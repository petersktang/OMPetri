/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.unibi.agbi.gnius.business.controller;

import edu.unibi.agbi.gnius.core.model.entity.simulation.Simulation;
import edu.unibi.agbi.gnius.core.model.entity.simulation.SimulationLineChartData;
import edu.unibi.agbi.gnius.core.service.ResultsService;
import edu.unibi.agbi.gnius.core.service.SimulationService;
import edu.unibi.agbi.gnius.core.service.exception.ResultsServiceException;
import edu.unibi.agbi.petrinet.entity.abstr.Element;
import edu.unibi.agbi.petrinet.entity.IElement;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;

/**
 *
 * @author PR
 */
@Controller
public class ResultsController implements Initializable
{
    @Autowired private SimulationService simulationService;
    @Autowired private ResultsService resultsService;

    @FXML private ChoiceBox simulationChoices;
    @FXML private ChoiceBox elementChoices;
    @FXML private ChoiceBox valueChoices;
    @FXML private TextField simulationFilterInput;
    @FXML private TextField elementFilterInput;
    @FXML private TextField valueFilterInput;

    @FXML private TableView<SimulationLineChartData> tableView;
    @FXML private TableColumn<SimulationLineChartData, String> columnSimulation;
    @FXML private TableColumn<SimulationLineChartData, String> columnElementId;
    @FXML private TableColumn<SimulationLineChartData, String> columnElementName;
    @FXML private TableColumn<SimulationLineChartData, String> columnValue;
    @FXML private TableColumn<SimulationLineChartData, CheckBox> columnEnable;
    @FXML private TableColumn<SimulationLineChartData, Button> columnDrop;

    @FXML private TextField xLabelInput;
    @FXML private TextField yLabelInput;

    @FXML private LineChart lineChart;

    @Value("${results.window.choice.dateformat}") private String dateFormat;
    @Value("${results.window.linechart.y.label}") private String xAxisLabel;
    @Value("${results.window.linechart.x.label}") private String yAxisLabel;
    
    private DateTimeFormatter simulationChoiceDateTimeFormatter;

    @FXML
    public void AddSelectedToChart() {

        SimulationChoice simulationChoice = (SimulationChoice) simulationChoices.getSelectionModel().getSelectedItem();
        ElementChoice elementChoice = (ElementChoice) elementChoices.getSelectionModel().getSelectedItem();
        ValueChoice valueChoice = (ValueChoice) valueChoices.getSelectionModel().getSelectedItem();
        
        SimulationLineChartData data = new SimulationLineChartData(simulationChoice.getSimulation(), elementChoice.getElement(), valueChoice.getValue());

        if (resultsService.add(lineChart, data)) {
            resultsService.UpdateSeries(data);
            resultsService.show(lineChart, data);
        } else {
            System.out.println("The selected data has already been added to the graph. Check the table if showing has been disabled!");
        }
    }

    public synchronized void RefreshSimulationChoices() {

        int index = simulationChoices.getSelectionModel().getSelectedIndex();

        ObservableList<Object> choices = FXCollections.observableArrayList();
        for (Simulation simulation : simulationService.getSimulations()) {
            choices.add(new SimulationChoice(simulation));
        }

        simulationChoices.setItems(choices);
        simulationChoices.getSelectionModel().select(index);

        FilterChoices(simulationChoices, simulationFilterInput.getText());
    }

    private synchronized void RefreshElementChoices() {

        SimulationChoice simulationChoice = (SimulationChoice) simulationChoices.getSelectionModel().getSelectedItem();
        if (simulationChoice != null) {

            int index = elementChoices.getSelectionModel().getSelectedIndex();
            int oldSize = elementChoices.getItems().size();

            Set<IElement> elements = simulationChoice.getSimulation().getElementFilterReferences().keySet();
            ObservableList<Object> choices = FXCollections.observableArrayList();
            for (IElement element : elements) {
                if (element.getElementType() == Element.Type.ARC) {
                    continue;
                }
                choices.add(new ElementChoice(element));
            }

            elementChoices.setItems(choices);
            if (oldSize == choices.size()) {
                elementChoices.getSelectionModel().select(index);
            }
        }
        FilterChoices(elementChoices, elementFilterInput.getText());
    }

    private synchronized void RefreshValueChoices() {

        SimulationChoice simulationChoice = (SimulationChoice) simulationChoices.getSelectionModel().getSelectedItem();
        ElementChoice elementChoice = (ElementChoice) elementChoices.getSelectionModel().getSelectedItem();
        if (simulationChoice != null && elementChoice != null) {

            int index = valueChoices.getSelectionModel().getSelectedIndex();
            int oldSize = valueChoices.getItems().size();

            List<String> values = simulationChoice.getSimulation().getElementFilterReferences().get(elementChoice.getElement());
            ObservableList<Object> choices = FXCollections.observableArrayList();
            for (String value : values) {
                choices.add(new ValueChoice(value));
            }

            valueChoices.setItems(choices);
            if (oldSize == choices.size()) {
                valueChoices.getSelectionModel().select(index);
            }
        }
        FilterChoices(valueChoices, valueFilterInput.getText());
    }

    private void FilterChoices(ChoiceBox choiceBox, String text) {

        if (text == null || text.matches("")) {
            return;
        }

        Object selectedChoice = choiceBox.getSelectionModel().getSelectedItem();

        int selectedIndex = -1;
        int index = 0;

        ObservableList choices = choiceBox.getItems();
        ObservableList choicesFiltered = FXCollections.observableArrayList();

        for (Object choice : choices) {
            if (choice.toString().contains(text)) {
                choicesFiltered.add(choice);
                if (choice.equals(selectedChoice)) {
                    selectedIndex = index;
                }
                index++;
            }
        }

        choiceBox.setItems(choicesFiltered);
        if (selectedIndex != -1) {
            choiceBox.getSelectionModel().select(selectedIndex);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        /**
         * LineChart specs.
         */
        lineChart.createSymbolsProperty().set(false);
        lineChart.getXAxis().labelProperty().bind(xLabelInput.textProperty());
        lineChart.getYAxis().labelProperty().bind(yLabelInput.textProperty());
        xLabelInput.setText(xAxisLabel);
        yLabelInput.setText(yAxisLabel);

        /**
         * ChoiceBox and TextField actions.
         */
        simulationChoiceDateTimeFormatter = DateTimeFormatter.ofPattern(dateFormat);
        simulationService.getSimulations().addListener(new ListChangeListener()
        {
            @Override
            public void onChanged(ListChangeListener.Change change) {
                RefreshSimulationChoices();
            }
        });
        simulationFilterInput.setOnKeyReleased(e -> {
            RefreshSimulationChoices();
            simulationChoices.show();
            elementChoices.hide();
            valueChoices.hide();
        });

        simulationChoices.valueProperty().addListener((ObservableValue obs, Object o, Object n) -> RefreshElementChoices());
        elementFilterInput.setOnKeyReleased(e -> {
            RefreshElementChoices();
            simulationChoices.hide();
            elementChoices.show();
            valueChoices.hide();
        });

        elementChoices.valueProperty().addListener((ObservableValue obs, Object o, Object n) -> RefreshValueChoices());
        valueFilterInput.setOnKeyReleased(e -> {
            RefreshValueChoices();
            simulationChoices.hide();
            elementChoices.hide();
            valueChoices.show();
        });

        /**
         * TableView specs.
         */
        tableView.setItems(FXCollections.observableArrayList());
        try {
            resultsService.add(lineChart, tableView.getItems());
        } catch (ResultsServiceException ex) {
            System.out.println(ex.getMessage());
        }

        columnSimulation.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getSimulation().getTime().format(simulationChoiceDateTimeFormatter) + " " + cellData.getValue().getSimulation().getName()));
        columnElementId.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getElement().getId()));
        columnElementName.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getElement().getName()));
        columnValue.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getValue()));
        columnEnable.setCellValueFactory(cellData -> {
            CheckBox cb = new CheckBox();
            cb.setSelected(true);
            cb.selectedProperty().addListener(e -> {
                // wait for animations to finish or LineChart breaks if data is added/removed too fast
                if ((System.currentTimeMillis() - cellData.getValue().timeMilliSecondLastStatusChange()) < 1000) {
                    if (resultsService.isShown(lineChart, cellData.getValue())) {
                        if (!cb.selectedProperty().get()) {
                            cb.selectedProperty().set(true);
                        }
                    } else {
                        if (cb.selectedProperty().get()) {
                            cb.selectedProperty().set(false);
                        }
                    }
                    return;
                }
                cellData.getValue().updateMilliSecondLastStatusChange();
                if (cb.selectedProperty().getValue()) {
                    resultsService.UpdateSeries(cellData.getValue());
                    resultsService.show(lineChart, cellData.getValue());
                } else {
                    resultsService.hide(lineChart, cellData.getValue());
                }
            });
            return new ReadOnlyObjectWrapper(cb);
        });
        columnDrop.setCellValueFactory(cellData -> {
            Button btn = new Button();
            btn.setOnAction(e -> {
                resultsService.drop(lineChart, cellData.getValue());
            });
            return new ReadOnlyObjectWrapper(btn);
        });
    }

    private class ValueChoice
    {
        private final String value;

        private ValueChoice(String value) {
            this.value = value;
        }

        private String getValue() {
            return value;
        }

        @Override
        public String toString() {
            return value;
        }
    }

    private class ElementChoice
    {
        private final IElement element;

        private ElementChoice(IElement element) {
            this.element = element;
        }

        private IElement getElement() {
            return element;
        }

        @Override
        public String toString() {
            return "(" + element.getId() + ") " + element.getName();
        }
    }

    private class SimulationChoice
    {
        private final Simulation simulation;

        private SimulationChoice(Simulation simulation) {
            this.simulation = simulation;
        }

        private Simulation getSimulation() {
            return simulation;
        }

        @Override
        public String toString() {
            return simulation.getTime().format(simulationChoiceDateTimeFormatter) + " " + simulation.getName();
        }
    }
}