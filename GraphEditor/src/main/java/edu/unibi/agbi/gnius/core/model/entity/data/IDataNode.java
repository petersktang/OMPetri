/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.unibi.agbi.gnius.core.model.entity.data;

import edu.unibi.agbi.gnius.core.model.entity.graph.IGraphNode;
import edu.unibi.agbi.petrinet.entity.IPN_Node;

import java.util.List;

/**
 *
 * @author PR
 */
public interface IDataNode extends IDataElement , IPN_Node
{
    public List<IGraphNode> getShapes();
}
