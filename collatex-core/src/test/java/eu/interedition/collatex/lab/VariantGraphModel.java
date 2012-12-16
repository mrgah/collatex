package eu.interedition.collatex.lab;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import eu.interedition.collatex.VariantGraph;
import eu.interedition.collatex.Witness;

import java.util.Collections;
import java.util.Map;

/**
 * @author <a href="http://gregor.middell.net/" title="Homepage">Gregor Middell</a>
 */
public class VariantGraphModel extends DirectedSparseGraph<VariantGraphVertexModel, VariantGraphEdgeModel> {

  private VariantGraphVertexModel start;
  private VariantGraphVertexModel end;

  public VariantGraphVertexModel getStart() {
    return start;
  }

  public void setStart(VariantGraphVertexModel start) {
    this.start = start;
  }

  public VariantGraphVertexModel getEnd() {
    return end;
  }

  public void setEnd(VariantGraphVertexModel end) {
    this.end = end;
  }

  public void update(VariantGraph pvg) {
    for (VariantGraphEdgeModel edgeModel : Lists.newArrayList(getEdges())) {
      removeEdge(edgeModel);
    }
    for (VariantGraphVertexModel vertexModel : Lists.newArrayList(getVertices())) {
      removeVertex(vertexModel);
    }

    final Map<VariantGraph.Vertex, VariantGraphVertexModel> vertexMap = Maps.newHashMap();
    for (VariantGraph.Vertex pv : pvg.vertices()) {
      final VariantGraphVertexModel v = new VariantGraphVertexModel(pv.tokens(), pv.getRank());
      addVertex(v);
      vertexMap.put(pv, v);
      if (pvg.getStart().equals(pv)) {
        setStart(v);
      } else if (pvg.getEnd().equals(pv)) {
        setEnd(v);
      }
    }
    for (VariantGraph.Edge pe : pvg.edges()) {
      addEdge(new VariantGraphEdgeModel(pe.witnesses()), vertexMap.get(pe.from()), vertexMap.get(pe.to()));
    }
    
    for (VariantGraph.Transposition t : pvg.transpositions()) {
      addEdge(new VariantGraphEdgeModel(Collections.<Witness>emptySet()), vertexMap.get(t.from()), vertexMap.get(t.to()));
    }
  }
}
