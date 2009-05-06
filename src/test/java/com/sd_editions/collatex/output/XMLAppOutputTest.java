package com.sd_editions.collatex.output;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import com.sd_editions.collatex.permutations.CollateCore;
import com.sd_editions.collatex.permutations.MatchUnmatch;

public class XMLAppOutputTest {

  /**
   * The first example from #6 (http://arts-itsee.bham.ac.uk/trac/interedition/ticket/6) (without witness C for now)
   */
  @Test
  public void testSimpleSubstitutionOutput() {
    String xml = collateWitnessStrings("the black cat and the black mat", "the black dog and the black mat");
    assertEquals("<collation>the black <app><rdg wit=\"#A\">cat</rdg><rdg wit=\"#B\">dog</rdg></app> and the black mat</collation>", xml);
  }

  /* ## Simple addition/deletion
  A: the black cat on the white table
  B: the black saw the black cat on the table

  <collation>the <app><rdg wit="#A"/><rdg wit="#B">saw the black</rdg></app> cat on the <app><rdg wit="#A">white</rdg><rdg wit="#B"/></app> table</collation> 

   */
  @Test
  public void testSimpleAddDelOutput() {
    String xml = collateWitnessStrings("the black cat on the white table", "the black saw the black cat on the table");
    assertEquals("<collation>the black <app><rdg wit=\"#A\"/><rdg wit=\"#B\">saw the black</rdg></app> cat on the <app><rdg wit=\"#A\">white</rdg><rdg wit=\"#B\"/></app> table</collation>", xml);
  }

  private String collateWitnessStrings(String witnessA, String witnessB) {
    CollateCore collateCore = new CollateCore(witnessA, witnessB);
    List<MatchUnmatch> matchUnmatchList = collateCore.doCompareWitnesses(collateCore.getWitness(1), collateCore.getWitness(2));

    collateCore.sortPermutationsByUnmatches(matchUnmatchList);

    // FIXME find out which is the best permutation, not just take the first one

    for (MatchUnmatch matchUnmatch : matchUnmatchList) {
      System.out.println(new AppAlignmentTable(matchUnmatch).toXML());
    }

    AppAlignmentTable alignmentTable = new AppAlignmentTable(matchUnmatchList.get(0));
    String xml = alignmentTable.toXML();
    return xml;
  }

}
