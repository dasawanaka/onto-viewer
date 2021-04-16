package org.edmcouncil.spec.fibo.weasel.ontology;

import org.edmcouncil.spec.fibo.weasel.model.details.OwlListDetails;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.edmcouncil.spec.fibo.config.configuration.model.AppConfiguration;
import org.edmcouncil.spec.fibo.config.configuration.model.ConfigKeys;
import org.edmcouncil.spec.fibo.config.configuration.model.impl.element.GroupsItem;
import org.edmcouncil.spec.fibo.config.configuration.model.impl.ViewerCoreConfiguration;
import org.edmcouncil.spec.fibo.weasel.model.module.FiboModule;
import org.edmcouncil.spec.fibo.weasel.model.details.OwlGroupedDetails;
import org.edmcouncil.spec.fibo.weasel.model.PropertyValue;
import org.edmcouncil.spec.fibo.weasel.model.details.OwlDetails;
import org.edmcouncil.spec.fibo.weasel.model.property.OwlAnnotationPropertyValue;
import org.edmcouncil.spec.fibo.weasel.ontology.data.OwlDataHandler;
import org.edmcouncil.spec.fibo.weasel.ontology.generator.DescriptionGenerator;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.edmcouncil.spec.fibo.config.configuration.model.ConfigItem;
import org.edmcouncil.spec.fibo.weasel.changer.ChangerIriToLabel;
import org.edmcouncil.spec.fibo.weasel.exception.NotFoundElementInOntologyException;

/**
 * @author Michał Daniel (michal.daniel@makolab.com)
 * @author Patrycja Miazek (patrycja.miazek@makolab.com)
 */
@Component
public class DetailsManager {

  private static final Logger LOG = LoggerFactory.getLogger(DetailsManager.class);
  private static final String DEFAULT_GROUP_NAME = "other";

  @Autowired
  private OntologyManager ontologyManager;
  @Autowired
  private OwlDataHandler dataHandler;
  @Autowired
  private AppConfiguration config;
  @Autowired
  private ChangerIriToLabel changerIriToLabel;
  @Autowired
  private DescriptionGenerator descriptionGenerator;

  public OWLOntology getOntology() {
    return ontologyManager.getOntology();
  }

  public <T extends OwlDetails> T getDetailsByIri(String iriString) throws NotFoundElementInOntologyException {
    IRI iri = IRI.create(iriString);
    OwlListDetails result = null;
    //FIBO: if '/' is at the end of the URL, we extract the ontolog metadata
    if (iriString.endsWith("/")) {
      LOG.debug("Handle ontology metadata. IRI: {}", iriString);
      OwlListDetails wd = dataHandler.handleOntologyMetadata(iri, getOntology());

      result = wd;
    } else {
      if (ontologyManager.getOntology().containsClassInSignature(iri)) {
        LOG.debug("Handle class data.");
        OwlListDetails wd = dataHandler.handleParticularClass(iri, getOntology());
        result = wd;
      } else if (ontologyManager.getOntology().containsDataPropertyInSignature(iri)) {
        LOG.info("Handle data property.");
        OwlListDetails wd = dataHandler.handleParticularDataProperty(iri, getOntology());
        result = wd;
      } else if (ontologyManager.getOntology().containsObjectPropertyInSignature(iri)) {
        LOG.info("Handle object property.");
        OwlListDetails wd = dataHandler.handleParticularObjectProperty(iri, getOntology());
        result = wd;
      } else if (ontologyManager.getOntology().containsIndividualInSignature(iri)) {
        LOG.info("Handle individual data.");
        OwlListDetails wd = dataHandler.handleParticularIndividual(iri, getOntology());
        result = wd;
      } else if (ontologyManager.getOntology().containsDatatypeInSignature(iri)) {
        LOG.info("Handle datatype");
        OwlListDetails wd = dataHandler.handleParticularDatatype(iri, getOntology());
        result = wd;
      } else if (ontologyManager.getOntology().containsAnnotationPropertyInSignature(iri)) {
        LOG.info("Handle annotation property");
        OwlListDetails wd = dataHandler.handleParticularAnnotationProperty(iri, getOntology());
        result = wd;
      }
      if (result != null) {
        result.setMaturityLevel(dataHandler.getMaturityLevel(iriString, getOntology()));
      }
    }
    if (result == null) {
      throw new NotFoundElementInOntologyException("Not found element in ontology with IRI: " + iriString);
    }

    result.setIri(iriString);

    //Path to element in modules
    List<String> elementLocation = dataHandler.getElementLocationInModules(iriString, ontologyManager.getOntology());
    result.setLocationInModules(elementLocation);

    //Group elements if in the
    if (!config.getViewerCoreConfig().isEmpty()) {
      ViewerCoreConfiguration cfg = config.getViewerCoreConfig();
      if (cfg.isGrouped()) {
        OwlGroupedDetails newResult = groupDetails(result, cfg);
        addGeneratedDescription(newResult);
        return (T) newResult;
      } else {
        sortResults(result);
      }
    }
    return (T) result;
  }

  private OwlGroupedDetails groupDetails(OwlListDetails owlDetails, ViewerCoreConfiguration cfg) {
    OwlGroupedDetails newResult = null;
    OwlGroupedDetails groupedDetails = new OwlGroupedDetails();
    Set<ConfigItem> groups = cfg.getConfiguration().get(ConfigKeys.GROUPS);

    for (Map.Entry<String, List<PropertyValue>> entry : owlDetails.getProperties().entrySet()) {
      String propertyKey = entry.getKey();

      String groupName = null;
      groupName = getGroupName(groups, propertyKey);
      groupName = groupName == null ? DEFAULT_GROUP_NAME : groupName;
      for (PropertyValue property : entry.getValue()) {
        groupedDetails.addProperty(groupName, propertyKey, property);
      }
    }
    groupedDetails.setTaxonomy(owlDetails.getTaxonomy());
    groupedDetails.setLabel(owlDetails.getLabel());
    groupedDetails.setIri(owlDetails.getIri());
    groupedDetails.setLocationInModules(owlDetails.getLocationInModules());
    groupedDetails.setGraph(owlDetails.getGraph());
    groupedDetails.setqName(owlDetails.getqName());
    groupedDetails.setMaturityLevel(owlDetails.getMaturityLevel());

    groupedDetails.sortProperties(groups, cfg);

    //first must be sorted next we need to change keys
    groupedDetails = changerIriToLabel.changeIriKeysInGroupedDetails(groupedDetails);

    for (Map.Entry<String, Map<String, List<PropertyValue>>> entry : groupedDetails.getProperties().entrySet()) {
      LOG.debug(entry.toString());
    }

    owlDetails.release();

    newResult = groupedDetails;
    return newResult;
  }

  private String getGroupName(Set<ConfigItem> groups, String propertyKey) {
    String result = null;
    if (propertyKey == null || propertyKey.isEmpty()) {
      return result;
    }
    for (ConfigItem g : groups) {
      GroupsItem group = (GroupsItem) g;
      if (group.getElements() != null && group.getElements().size() > 0) {
        if (group.contains(propertyKey)) {
          return group.getName();
        }
      }
    }
    return result;
  }

  private void sortResults(OwlListDetails result) {
    Set set = config.getViewerCoreConfig()
        .getConfigVal(ConfigKeys.PRIORITY_LIST);
    if (set == null) {
      return;
    }
    List prioritySortList = new LinkedList();
    result.sortProperties(prioritySortList);
  }

  public List<FiboModule> getAllModulesData() {
    return dataHandler.getAllModulesData(ontologyManager.getOntology());
  }

  private void addGeneratedDescription(OwlGroupedDetails groupedDetails) {
    Optional<List<OwlAnnotationPropertyValue>> description =
        descriptionGenerator.prepareDescriptionString(groupedDetails);

    description.ifPresent(descriptionValueList ->
        descriptionValueList.forEach(descriptionValue ->
            groupedDetails.addProperty(
                "Glossary",
                "generated description",
                descriptionValue)));
  }
}
