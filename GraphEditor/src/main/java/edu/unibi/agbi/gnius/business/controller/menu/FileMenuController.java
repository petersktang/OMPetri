/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.unibi.agbi.gnius.business.controller.menu;

import edu.unibi.agbi.gnius.business.controller.tab.editor.EditorPaneController;
import edu.unibi.agbi.gnius.core.service.DataService;
import edu.unibi.agbi.petrinet.util.OM_Exporter;
import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author PR
 */
@Component
public class FileMenuController implements Initializable
{
    @Autowired private DataService dataService;
    @Autowired private EditorPaneController editorPaneController;
    
    @FXML
    public void SaveFileAs() {
        
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Petri Net");
        
        File file = fileChooser.showSaveDialog(editorPaneController.getStage());
        
        OM_Exporter omExporter = new OM_Exporter();
        omExporter.export(dataService.getPetriNet() , file);
    }

    @Override
    public void initialize(URL location , ResourceBundle resources) {
        
    }
}
