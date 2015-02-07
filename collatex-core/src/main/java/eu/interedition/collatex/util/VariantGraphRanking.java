/*
 * Copyright (c) 2013 The Interedition Development Group.
 *
 * This file is part of CollateX.
 *
 * CollateX is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * CollateX is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with CollateX.  If not, see <http://www.gnu.org/licenses/>.
 */

package eu.interedition.collatex.util;

import eu.interedition.collatex.Token;
import eu.interedition.collatex.VariantGraph;
import eu.interedition.collatex.VariantGraph.Vertex;
import eu.interedition.collatex.Witness;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author <a href="http://gregor.middell.net/" title="Homepage">Gregor Middell</a>
 * @author Ronald Haentjens Dekker
 */
public class VariantGraphRanking implements Iterable<Set<VariantGraph.Vertex>>, Function<Vertex,Integer> {

  private final Map<VariantGraph.Vertex, Integer> byVertex = new HashMap<>();
  private final SortedMap<Integer, Set<Vertex>> byRank = new TreeMap<>();
  private final VariantGraph graph;
  private final Set<Witness> witnesses;

  VariantGraphRanking(VariantGraph graph, Set<Witness> witnesses) {
    this.graph = graph;
    this.witnesses = witnesses;
  }

  public static VariantGraphRanking of(VariantGraph graph) {
    return of(graph, null);
  }

  public static VariantGraphRanking of(VariantGraph graph, Set<Witness> witnesses) {
    final VariantGraphRanking ranking = new VariantGraphRanking(graph, witnesses);
    for (VariantGraph.Vertex v : graph.vertices(witnesses)) {
      int rank = -1;
      for (VariantGraph.Edge e : v.incoming(witnesses)) {
        rank = Math.max(rank, ranking.byVertex.get(e.from()));
      }
      rank++;
      ranking.byVertex.put(v, rank);
      ranking.byRank.computeIfAbsent(rank, r -> new HashSet<>()).add(v);
    }
    return ranking;
  }

  public static VariantGraphRanking ofOnlyCertainVertices(VariantGraph graph, Set<Witness> witnesses, Set<VariantGraph.Vertex> vertices) {
    final VariantGraphRanking ranking = new VariantGraphRanking(graph, witnesses);
    for (VariantGraph.Vertex v : graph.vertices(witnesses)) {
      int rank = -1;
      for (VariantGraph.Edge e : v.incoming(witnesses)) {
        rank = Math.max(rank, ranking.byVertex.get(e.from()));
      }
      if (vertices.contains(v)) {
        rank++;
      }
      ranking.byVertex.put(v, rank);
      ranking.byRank.computeIfAbsent(rank, r -> new HashSet<>()).add(v);
    }
    return ranking;
  }

  public Set<Witness> witnesses() {
    return Optional.ofNullable(witnesses).orElse(graph.witnesses());
  }

  public Map<VariantGraph.Vertex, Integer> getByVertex() {
    return Collections.unmodifiableMap(byVertex);
  }

  public Map<Integer, Set<VariantGraph.Vertex>> getByRank() {
    return Collections.unmodifiableMap(byRank);
  }

  public int size() {
    return byRank.keySet().size();
  }

  @Override
  public Iterator<Set<VariantGraph.Vertex>> iterator() {
    return byRank.values().iterator();
  }

  public List<SortedMap<Witness, Set<Token>>> asTable() {
    return byRank.values().stream()
            .filter(rank -> rank.stream().flatMap(v -> v.tokens(witnesses).stream()).findFirst().isPresent())
            .map(vertices -> {
              final SortedMap<Witness, Set<Token>> row = new TreeMap<>(Witness.SIGIL_COMPARATOR);
              vertices.stream().flatMap(v -> v.tokens(witnesses).stream()).forEach(token -> row.computeIfAbsent(token.getWitness(), w -> new HashSet<>()).add(token));
              return row;
            })
            .collect(Collectors.toList());
  }

  public VariantGraph.Vertex[][] asArray() {
    final VariantGraph.Vertex[][] arr = new VariantGraph.Vertex[byRank.size()][];
    byRank.forEach((rank, vertices) -> arr[rank] = vertices.toArray(new Vertex[vertices.size()]));
    return arr;
  }

  @Override
  public Integer apply(VariantGraph.Vertex vertex) {
    return byVertex.get(vertex);
  }

  public Comparator<VariantGraph.Vertex> comparator() {
    return Comparator.comparingInt(byVertex::get);
  }
}
