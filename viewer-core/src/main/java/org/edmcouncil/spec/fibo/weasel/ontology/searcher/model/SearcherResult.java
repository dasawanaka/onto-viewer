package org.edmcouncil.spec.fibo.weasel.ontology.searcher.model;

/**
 * @param <T> Type of result
 * @author Michał Daniel (michal.daniel@makolab.com)
 */
public abstract class SearcherResult<T> {

  private final Type type;
  private final T result;

  SearcherResult(Type type, T result) {
    this.type = type;
    this.result = result;
  }

  public Type getType() {
    return type;
  }

  public T getResult() {
    return result;
  }

  public enum Type {
    list, details
  }
}
