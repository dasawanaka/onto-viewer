package org.edmcouncil.spec.fibo.view.controller;

import org.edmcouncil.spec.fibo.view.model.ErrorResult;
import org.edmcouncil.spec.fibo.view.service.OntologySearcherService;
import org.edmcouncil.spec.fibo.view.service.TextSearchService;
import org.edmcouncil.spec.fibo.view.util.UrlChecker;
import org.edmcouncil.spec.fibo.weasel.exception.ViewerException;
import org.edmcouncil.spec.fibo.weasel.ontology.searcher.model.SearcherResult;
import org.edmcouncil.spec.fibo.weasel.ontology.updater.UpdateBlocker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import java.util.Arrays;
import java.util.Optional;

/**
 * @author Michał Daniel (michal.daniel@makolab.com)
 */
@Controller
@RequestMapping("/api/search")
public class SearchApiController {

  private static final Logger LOG = LoggerFactory.getLogger(SearchApiController.class);

  private final TextSearchService textSearchService;
  private final OntologySearcherService ontologySearcher;
  private final UpdateBlocker blocker;

  private static final Integer DEFAULT_MAX_SEARCH_RESULT_COUNT = 20;
  private static final Integer DEFAULT_RESULT_PAGE = 1;

  public SearchApiController(TextSearchService textSearchService,
                             OntologySearcherService ontologySearcher,
                             UpdateBlocker blocker) {
    this.textSearchService = textSearchService;
    this.ontologySearcher = ontologySearcher;
    this.blocker = blocker;
  }

  @PostMapping(value = {"", "/max/{max}", "/page/{page}", "/max/{max}/page/{page}"})
  public ResponseEntity<SearcherResult> searchJson(
      @RequestBody String query,
      @PathVariable Optional<Integer> max,
      @PathVariable Optional<Integer> page) {
    LOG.info("[REQ] POST : api / search / max / {{}} / page /{{}} | RequestBody = {{}}", query, max, page);

    if (!blocker.isInitializeAppDone()) {
      LOG.debug("Application initialization has not completed");
      return new ResponseEntity<>(HttpStatus.SERVICE_UNAVAILABLE);
    }

    SearcherResult result;
    try {

      if (UrlChecker.isUrl(query)) {
        LOG.info("URL detected - searching for specific element.");

        result = ontologySearcher.search(query, 0);
      } else {
        LOG.info("String detected - searching for elements with the given label.");

        Integer maxResults = max.orElse(DEFAULT_MAX_SEARCH_RESULT_COUNT);
        Integer currentPage = page.orElse(DEFAULT_RESULT_PAGE);
        result = textSearchService.search(query, maxResults, currentPage);
      }

    } catch (ViewerException ex) {
      return getError(ex);
    }

    return ResponseEntity.ok(result);
  }

  private ResponseEntity getError(ViewerException ex) {
    LOG.info("Handle NotFoundElementInOntologyException. Message: '{}'", ex.getMessage());
    LOG.trace(Arrays.toString(ex.getStackTrace()));
    ErrorResult er = new ErrorResult();
    er.setExMessage(ex.getMessage());
    er.setMessage("Element Not Found.");
    return ResponseEntity.badRequest().body(er);
  }
}
