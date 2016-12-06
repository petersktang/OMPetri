/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.unibi.agbi.petrinet.model.entity;

/**
 *
 * @author PR
 */
public abstract class Arc extends PNNode
{
    private Place place;
    private Transition transition;
    
    private double weight;
    
    private Type arcType;
    
    public Arc() {
        this.pnNodeType = PNNode.Type.ARC;
    }
    
    public Type getArcType() {
        return arcType;
    }
    
    public enum Type {
        EQUAL, INHIBITORY, READ, RESET, DEFAULT;
    }
}
