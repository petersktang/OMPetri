/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.unibi.agbi.petrinet.model.entity.arc;

import edu.unibi.agbi.petrinet.model.entity.place.Place;
import edu.unibi.agbi.petrinet.model.entity.transition.Transition;
import edu.unibi.agbi.petrinet.model.entity.transition.Transition;

/**
 *
 * @author PR
 */
public class Arc
{
    private Object shape;
    
    private Place place;
    private Transition transition;
    
    private double weight;
}