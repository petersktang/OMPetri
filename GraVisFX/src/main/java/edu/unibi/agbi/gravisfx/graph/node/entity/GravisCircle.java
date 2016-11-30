/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.unibi.agbi.gravisfx.graph.node.entity;

import edu.unibi.agbi.gravisfx.PropertiesController;
import edu.unibi.agbi.gravisfx.graph.node.IGravisNode;
import edu.unibi.agbi.gravisfx.graph.node.IGravisEdge;
import edu.unibi.agbi.gravisfx.graph.node.IGravisSelectable;
import edu.unibi.agbi.gravisfx.presentation.layer.NodeLayer;

import java.util.ArrayList;
import java.util.List;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.BooleanPropertyBase;
import javafx.css.PseudoClass;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Shape;

/**
 *
 * @author PR
 */
public class GravisCircle extends Circle implements IGravisNode, IGravisSelectable
{
    private final List<IGravisNode> children = new ArrayList();
    private final List<IGravisNode> parents = new ArrayList();
    private final List<IGravisEdge> edges = new ArrayList();
    
    private static final String PSEUDO_CLASS_IDENT = "selected";
    private static final PseudoClass SELECTED_PSEUDO_CLASS = PseudoClass.getPseudoClass(PSEUDO_CLASS_IDENT);
    
    private final BooleanProperty isSelected = new BooleanPropertyBase(false) {
        @Override 
        protected void invalidated() {
            pseudoClassStateChanged(SELECTED_PSEUDO_CLASS , get());
        }
        @Override
        public Object getBean() {
            return GravisCircle.this;
        }
        @Override
        public String getName() {
            return PSEUDO_CLASS_IDENT;
        }
    };
    
    private Object relatedObject;
    
    public GravisCircle() {
        super();
        setRadius(PropertiesController.CIRCLE_RADIUS);
    }
    
    public GravisCircle(Object relatedObject) {
        this();
        this.relatedObject = relatedObject;
    }
    
    @Override
    public Shape getShape() {
        return this;
    }
    
    @Override
    public void setRelatedObject(Object object) {
        relatedObject = object;
    }
    
    @Override
    public Object getRelatedObject() {
        return relatedObject;
    }
    
    /**
     * Position the center of the node at the given coordinates.
     * @param centerX
     * @param centerY 
     */
    @Override
    public void setTranslate(double centerX , double centerY) {
        
        setTranslateX(centerX);
        setTranslateY(centerY);
    }

    @Override
    public double getOffsetX() {
        return 0; // position is fixed to shape center
    }

    @Override
    public double getOffsetY() {
        return 0; // position is fixed to shape center
    }
    
    @Override
    public void addParentNode(IGravisNode parent) {
        parents.add(parent);
    }
    
    @Override
    public void addChildNode(IGravisNode child) {
        children.add(child);
    }
    
    @Override
    public void addEdge(IGravisEdge edge) {
        edges.add(edge);
    }
    
    @Override
    public boolean removeChild(IGravisNode node) {
        return children.remove(node);
    }
    
    @Override
    public boolean removeParent(IGravisNode node) {
        return parents.remove(node);
    }
    
    @Override
    public boolean removeEdge(IGravisEdge edge) {
        return edges.remove(edge);
    }
    
    @Override
    public List<IGravisNode> getParents() {
        return parents;
    }
    
    @Override
    public List<IGravisNode> getChildren() {
        return children;
    }
    
    @Override
    public List<IGravisEdge> getEdges() {
        return edges;
    }

    @Override
    public void setHighlight(boolean value) {
        isSelected.set(value);
    }
    
    @Override
    public void putOnTop() {
        NodeLayer nodeLayer = (NodeLayer) getParent();
        nodeLayer.getChildren().remove(this);
        nodeLayer.getChildren().add(this);
    }
}