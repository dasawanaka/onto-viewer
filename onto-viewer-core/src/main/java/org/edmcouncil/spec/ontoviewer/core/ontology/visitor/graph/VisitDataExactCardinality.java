package org.edmcouncil.spec.ontoviewer.core.ontology.visitor.graph;

import java.util.Map;
import java.util.Set;
import org.edmcouncil.spec.ontoviewer.core.model.graph.GraphNode;
import org.edmcouncil.spec.ontoviewer.core.model.graph.GraphNodeType;
import org.edmcouncil.spec.ontoviewer.core.model.graph.GraphRelation;
import org.edmcouncil.spec.ontoviewer.core.model.graph.OntologyGraph;
import org.edmcouncil.spec.ontoviewer.core.ontology.data.extractor.OwlDataExtractor;
import org.edmcouncil.spec.ontoviewer.core.ontology.data.graph.GraphObjGenerator;
import org.edmcouncil.spec.ontoviewer.core.ontology.data.label.provider.LabelProvider;
import org.edmcouncil.spec.ontoviewer.core.ontology.visitor.ExpressionReturnedClass;
import org.semanticweb.owlapi.model.DataRangeType;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLDataExactCardinality;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class VisitDataExactCardinality {

    private static final Logger LOG = LoggerFactory.getLogger(VisitDataExactCardinality.class);
    

    public static Map<GraphNode, Set<ExpressionReturnedClass>> visit(OWLDataExactCardinality axiom, Map<GraphNode, Set<ExpressionReturnedClass>> returnedVal, OntologyGraph vg, GraphNode node, GraphNodeType type, Boolean equivalentTo, Boolean not, LabelProvider labelProvider) {
        GraphObjGenerator gog = new GraphObjGenerator(vg, labelProvider);
        LOG.debug("visit OWLDataExactCardinality: {}", axiom.toString());
        String propertyIri = null;
        int cardinality = axiom.getCardinality();
        propertyIri = OwlDataExtractor.extractAxiomPropertyIri(axiom);
        DataRangeType objectType = axiom.getFiller().getDataRangeType();

        switch (objectType) {
            case DATATYPE:
                IRI datatypeIri = axiom.getFiller().signature().findFirst().get().getIRI();
                GraphNode endNode = gog.createNode(datatypeIri.toString(), type, not);
                GraphRelation rel = gog.createCardinalityRelation(node, propertyIri, endNode, type, cardinality, cardinality);
                rel.setIri(propertyIri);
                vg.addNode(endNode);
                vg.addRelation(rel);
                return null;

            default:
                LOG.debug("Unsupported switch case (DataRangeType): {}", objectType);
        }

        return returnedVal;

    }
}
