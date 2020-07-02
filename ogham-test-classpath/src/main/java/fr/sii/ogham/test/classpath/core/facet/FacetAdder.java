package fr.sii.ogham.test.classpath.core.facet;

import java.util.List;

import fr.sii.ogham.test.classpath.core.Project;
import fr.sii.ogham.test.classpath.core.exception.AddFacetException;

public interface FacetAdder {
	void addFacet(Project<?> project, List<Facet> facets) throws AddFacetException;
}
