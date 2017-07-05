/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.unibi.agbi.gnius.business.controller;

import edu.unibi.agbi.gnius.business.controller.editor.TabsController;
import edu.unibi.agbi.gnius.business.controller.editor.element.ParameterController;
import edu.unibi.agbi.gnius.business.controller.editor.element.ElementController;
import edu.unibi.agbi.gnius.business.controller.editor.model.HierarchyController;
import edu.unibi.agbi.gnius.business.controller.editor.model.ModelController;
import edu.unibi.agbi.gnius.business.controller.menu.FileMenuController;
import edu.unibi.agbi.gnius.core.model.entity.data.IDataElement;
import edu.unibi.agbi.gnius.core.model.entity.graph.IGraphElement;
import edu.unibi.agbi.gnius.core.model.dao.DataDao;
import edu.unibi.agbi.gnius.core.service.DataService;
import edu.unibi.agbi.gnius.core.service.SimulationService;
import edu.unibi.agbi.gnius.util.Calculator;
import edu.unibi.agbi.gravisfx.graph.GraphPane;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;

/**
 *
 * @author PR
 */
@Controller
public class MainController implements Initializable
{
    @Autowired private Calculator calculator;
    @Autowired private DataService dataService;
    @Autowired private SimulationService simulationService;

    @Autowired private ElementController elementController;
    @Autowired private FileMenuController fileMenuController;
    @Autowired private HierarchyController hierarchyController;
    @Autowired private ModelController modelController;
    @Autowired private ParameterController parameterController;
    @Autowired private TabsController tabsController;

    @FXML private HBox zoomFrame;
    @FXML private VBox hierarchyFrame;
    @FXML private VBox modelFrame;
    @FXML private VBox elementFrame;
    @FXML private VBox parameterFrame;
    @FXML private Label statusTop;

    @Value("${zoom.scale.base}") private double scaleBase;
    @Value("${zoom.scale.factor}") private double scaleFactor;
    @Value("${zoom.scale.max}") private double scaleMax;
    @Value("${zoom.scale.min}") private double scaleMin;

    /**
     * Applies an offset in translation to keep the focus for a given zoom
     * factor to a graph pane.
     *
     * @param graphPane
     * @param zoomOffsetX
     * @param zoomOffsetY
     * @param zoomFactor
     */
    public void ApplyZoomOffset(GraphPane graphPane, double zoomOffsetX, double zoomOffsetY, double zoomFactor) {

        double startX, startY, endX, endY;
        double translateX, translateY;

        startX = zoomOffsetX - graphPane.getGraph().translateXProperty().get();
        startY = zoomOffsetY - graphPane.getGraph().translateYProperty().get();

        endX = startX * zoomFactor;
        endY = startY * zoomFactor;

        translateX = startX - endX;
        translateY = startY - endY;

        graphPane.getGraph().setTranslateX(graphPane.getGraph().translateXProperty().get() + translateX);
        graphPane.getGraph().setTranslateY(graphPane.getGraph().translateYProperty().get() + translateY);
    }

    public Stage getStage() {
        return (Stage) elementFrame.getScene().getWindow();
    }

    public Label getStatusTop() {
        return statusTop;
    }

    public void HideElementPanel() {
        elementFrame.setVisible(false);
        parameterFrame.setVisible(false);
    }

    public void HideModelPanel() {
        zoomFrame.setVisible(false);
        modelFrame.setVisible(false);
        hierarchyFrame.setVisible(false);
    }

    public void ShowDialogExit(Event event) {

        List<DataDao> daos = dataService.getDataDaosWithChanges();

        ButtonType buttonCancel = new ButtonType("Cancel");
        ButtonType buttonSave = new ButtonType("Save");
        ButtonType buttonQuit = new ButtonType("Exit");

        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Confirm exit");

        if (daos.isEmpty()) {
            alert.setHeaderText("Close the application");
            alert.setContentText("Are you sure you want to close the application?");
            alert.getButtonTypes().setAll(buttonCancel, buttonQuit);
        } else {
            alert.setHeaderText("Close the application and discard latest changes");
            alert.setContentText("You made changes to your model(s). Are you sure you want to close the application and discard any changes?");
            alert.getButtonTypes().setAll(buttonCancel, buttonSave, buttonQuit);
        }

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == buttonSave) {
            for (DataDao dao : daos) {
                fileMenuController.SaveAs(dao);
            }
            if (event != null) {
                event.consume();
            }
        } else if (result.get() == buttonQuit) {
            try {
                simulationService.StopSimulation();
                System.out.println("Closing application...");
                System.exit(0);
            } catch (Exception ex) {
                System.out.println(ex);
            }
        } else {
            if (event != null) {
                event.consume();
            }
        }
    }

    public void ShowElementDetails(IGraphElement element) {
        if (tabsController.getGraphPane() == null) {
            return;
        }
        parameterFrame.setVisible(false);
        elementFrame.setVisible(true);
        elementController.ShowElementDetails(element);
    }

    public void ShowElementParameters(IDataElement element) {
        if (tabsController.getGraphPane() == null) {
            return;
        }
        elementFrame.setVisible(false);
        parameterFrame.setVisible(true);
        parameterController.ShowParameters(element);
    }

    public void ShowModelPanel(DataDao dataDao) {
        zoomFrame.setVisible(true);
        modelFrame.setVisible(true);
        hierarchyFrame.setVisible(true);
        modelController.setDao(dataDao);
        hierarchyController.setDao(dataDao);
    }

    @FXML
    public void CenterNodes() {

        Point2D center;
        double scaleDistance, scaleTarget, scaleCurrent, adjustedOffsetX, adjustedOffsetY;

        tabsController.getGraphPane().getGraph().setTranslateX(0);
        tabsController.getGraphPane().getGraph().setTranslateY(0);

        center = calculator.getCenter(dataService.getGraph().getNodes());

        adjustedOffsetX = center.getX() - (tabsController.getGraphPane().getWidth() / 2) / tabsController.getGraphPane().getGraph().getScale().getX();
        adjustedOffsetY = center.getY() - (tabsController.getGraphPane().getHeight() / 2) / tabsController.getGraphPane().getGraph().getScale().getX();

        dataService.getGraph().getNodes().forEach(node -> {
            Point2D pos = new Point2D(
                    node.translateXProperty().get() - adjustedOffsetX, 
                    node.translateYProperty().get() - adjustedOffsetY
            );
            if (dataService.isGridEnabled()) {
                pos = calculator.getPositionInGrid(pos, dataService.getGraph());
            }
            node.translateXProperty().set(pos.getX());
            node.translateYProperty().set(pos.getY());
        });

        scaleTarget = calculator.getOptimalScale(tabsController.getGraphPane());
        scaleCurrent = scaleBase * Math.pow(scaleFactor, dataService.getDao().getScalePower());
        scaleDistance = Math.abs(scaleTarget - scaleCurrent);

        if (scaleTarget > scaleCurrent) {
            while (scaleCurrent < scaleMax && scaleCurrent < scaleTarget) {
                scaleCurrent = scaleBase * Math.pow(scaleFactor, dataService.getDao().getScalePower() + 1);
                if (scaleDistance > (scaleDistance = Math.abs(scaleTarget - scaleCurrent))) {
                    ZoomIn();
                }
            }
        } else if (scaleTarget < scaleCurrent) {
            while (scaleCurrent > scaleMin && scaleCurrent > scaleTarget) {
                scaleCurrent = scaleBase * Math.pow(scaleFactor, dataService.getDao().getScalePower() - 1);
                if (scaleDistance > (scaleDistance = Math.abs(scaleTarget - scaleCurrent))) {
                    ZoomOut();
                }
            }
        }
    }

    @FXML
    public void ZoomIn() {

        double scale_t0 = scaleBase * Math.pow(scaleFactor, dataService.getDao().getScalePower());
        double scale_t1 = scaleBase * Math.pow(scaleFactor, dataService.getDao().getScalePower() + 1);

        if (scale_t1 > scaleMax) {
            return;
        }
        dataService.getDao().setScalePower(dataService.getDao().getScalePower() + 1);

        tabsController.getGraphPane().getGraph().getScale().setX(scale_t1);
        tabsController.getGraphPane().getGraph().getScale().setY(scale_t1);

        ApplyZoomOffset(
                tabsController.getGraphPane(),
                tabsController.getGraphPane().getWidth() / 2,
                tabsController.getGraphPane().getHeight() / 2,
                scale_t1 / scale_t0
        );
    }

    @FXML
    public void ZoomOut() {

        double scale_t0 = scaleBase * Math.pow(scaleFactor, dataService.getDao().getScalePower());
        double scale_t1 = scaleBase * Math.pow(scaleFactor, dataService.getDao().getScalePower() - 1);

        if (scale_t1 < scaleMin) {
            return;
        }
        dataService.getDao().setScalePower(dataService.getDao().getScalePower() - 1);

        tabsController.getGraphPane().getGraph().getScale().setX(scale_t1);
        tabsController.getGraphPane().getGraph().getScale().setY(scale_t1);

        ApplyZoomOffset(
                tabsController.getGraphPane(),
                tabsController.getGraphPane().getWidth() / 2,
                tabsController.getGraphPane().getHeight() / 2,
                scale_t1 / scale_t0
        );
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        zoomFrame.setVisible(false);
        hierarchyFrame.setVisible(false);
        modelFrame.setVisible(false);
        elementFrame.setVisible(false);
        parameterFrame.setVisible(false);
    }
}
