
import edu.unibi.agbi.gnius.Main;
import edu.unibi.agbi.gravisfx.graph.Graph;
import edu.unibi.agbi.gravisfx.pane.GraphPane;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import org.junit.Assert;
import org.junit.Test;
import org.testfx.api.FxAssert;
import org.testfx.api.FxRobotException;
import static org.testfx.api.FxAssert.verifyThat;
import org.testfx.matcher.base.NodeMatchers;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author PR
 */
public class ButtonActionTest extends TestFXBase
{
    final String buttonLoad = "#buttonLoad";
    final String buttonConnect = "#buttonConnect";
    final String buttonCreate = "#buttonCreate";
    final String choicesCreate = "#choicesCreate";
    
    final String buttonAlign = "#buttonAlign";
    final String choicesAlign = "#choicesAlign";
    
    final String GRAPH_PANE_ID = "#graphPane";
    
    /**
     * Expects the given ID not to exist.
     */
    //@Test(expected = FxRobotException.class)
    public void clickOnButton() {
        clickOn("#sector9");
    }
    
    //@Test
    public void ensureButtonAddsExampleNodes() {
        
        Graph graph = Main.getGraph();
        
        Group nodeLayer =  graph.getTopLayer().getNodeLayer();
        Group edgeLayer = graph.getTopLayer().getEdgeLayer();
        Group selectionLayer = graph.getTopLayer().getSelectionLayer();
        
        GraphPane graphPane = find(GRAPH_PANE_ID);
        Assert.assertNotNull(graphPane);
        
        Assert.assertEquals(1 , graphPane.getChildren().size());
        Assert.assertEquals(0, graph.getNodes().length);
        Assert.assertEquals(0, nodeLayer.getChildren().size());
        Assert.assertEquals(graph.getNodes().length, nodeLayer.getChildren().size());
        
        clickOn(buttonLoad);
        
        Assert.assertTrue(graph.getNodes().length != 0);
        Assert.assertTrue(nodeLayer.getChildren().size() != 0);
        Assert.assertEquals(graph.getNodes().length, nodeLayer.getChildren().size());
    }
    
    
    public void ensureButtonConnectsNodes() {
        Assert.assertEquals(this , this);
        clickOn(buttonConnect);
        Assert.assertEquals(this , this);
    }
    
    /*
    @Test
    public void ensureButtonLoadsGraph() {
        sleep(1000);
        FxAssert.verifyThat(buttonLoad, (Label label) -> { // Hamcrest Matcher
            return label.getText().contains("Press the button!");
        });
        clickOn(TEST_BUTTON_FXID);
        sleep(1000); // just for visualization
        FxAssert.verifyThat(TEST_LABEL_FXID, (Label label) -> {
            return label.getText().contains("Added test nodes!");
        });
    }
    
    @Test
    public void verifyGraphViewIsEmpty() {
        verifyThat(viewPane, NodeMatchers.isNotNull());
        
        clickOn(TEST_BUTTON_FXID);
        
        Pane graphView = find(GRAPH_VIEW_FXID);
        
        Assert.assertEquals(0 , graphView.getChildren().size());
    }
    
    //@Test
    public void ensureMouseIsMoving() {
        clickOn("");
        moveTo(""); // moves the mouse
        sleep(1000); // just for visualization
    }*/
}