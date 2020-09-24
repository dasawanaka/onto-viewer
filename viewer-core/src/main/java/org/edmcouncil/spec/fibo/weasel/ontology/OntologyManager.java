package org.edmcouncil.spec.fibo.weasel.ontology;

import org.semanticweb.owlapi.model.OWLOntology;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 *
 * @author Michał Daniel (michal.daniel@makolab.com)
 */
@Component
public class OntologyManager {

  private static final Logger LOG = LoggerFactory.getLogger(OntologyManager.class);

  private OWLOntology ontology;
  
  public OWLOntology getOntology() {
    return ontology;
  }

  public void updateOntology(OWLOntology ont) {
    this.ontology = ont;
  }
}
