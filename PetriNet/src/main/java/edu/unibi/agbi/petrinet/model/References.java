/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.unibi.agbi.petrinet.model;

import edu.unibi.agbi.petrinet.entity.IElement;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Container for references between IElement data objects and filter variables
 * used in OpenModelica simulations.
 *
 * @author PR
 */
public class References
{
    private String pathSimulationExecutable; // temporary storage of the executable path
    
    private final Map<String, IElement> filterToElementReferences;
    private final Map<IElement, Set<String>> elementToFilterReferences;

    public References() {
        filterToElementReferences = new HashMap();
        elementToFilterReferences = new HashMap();
    }

    /**
     * Adds a reference, mapping a filter variable to an element.
     *
     * @param filter  String representing a filter variable
     * @param element IElement data object
     * @throws IOException
     */
    public void addFilterReference(String filter, IElement element) throws IOException {
        if (filterToElementReferences.containsKey(filter)) {
            if (!filterToElementReferences.get(filter).getId().contentEquals(element.getId())) {
                throw new IOException("Cannot set filter '" + filter + "' to reference element '" + element.getId() + "'. It is already used to reference element '" + filterToElementReferences.get(filter).getId() + "'.");
            }
        } else {
            filterToElementReferences.put(filter, element);
        }
    }

    /**
     * Adds a reference, mapping an element to one or multiple filter variables.
     *
     * @param element IElement data object
     * @param filter  String representing a filter variable
     */
    public void addElementReference(IElement element, String filter) {
        if (!elementToFilterReferences.containsKey(element)) {
            elementToFilterReferences.put(element, new HashSet());
        }
        elementToFilterReferences.get(element).add(filter);
    }

    public Map<String, IElement> getFilterToElementReferences() {
        return filterToElementReferences;
    }

    public Map<IElement, Set<String>> getElementToFilterReferences() {
        return elementToFilterReferences;
    }

    public void setSimulationExecutablePath(String pathSimulationExecutable) {
        this.pathSimulationExecutable = pathSimulationExecutable;
    }

    public String getSimulationExectuablePath() {
        return pathSimulationExecutable;
    }
}
