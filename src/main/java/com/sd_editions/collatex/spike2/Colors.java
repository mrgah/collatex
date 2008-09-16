package com.sd_editions.collatex.spike2;

import java.util.List;
import java.util.Set;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.sd_editions.collatex.spike2.collate.Addition;
import com.sd_editions.collatex.spike2.collate.Removal;
import com.sd_editions.collatex.spike2.collate.Replacement;
import com.sd_editions.collatex.spike2.collate.Transposition;

public class Colors {

  private final Index index;
  private final List<WitnessIndex> witnessIndexes;

  public Colors(String... witnesses) {
    index = new Index(witnesses);
    this.witnessIndexes = Lists.newArrayList();
    for (String witness : witnesses) {
      WitnessIndex witnessIndex = new WitnessIndex(witness, index);
      witnessIndexes.add(witnessIndex);
    }
  }

  public int numberOfUniqueWords() {
    return index.numberOfEntries();
  }

  public WitnessIndex getWitnessIndex(int i) {
    return witnessIndexes.get(i - 1);
  }

  public Modifications compareWitness(int i, int j) {
    Matches matches = getMatches(i, j);
    List<MisMatch> mismatches = matches.getMisMatches();
    List<Modification> modifications = Lists.newArrayList();
    modifications.addAll(getOmissions(mismatches));
    modifications.addAll(getAdditions(mismatches));
    modifications.addAll(getReplacements(mismatches));
    List<Transposition> transpositions = Lists.newArrayList();
    return new Modifications(modifications, transpositions);
  }

  public List<Addition> getAdditions(List<MisMatch> mismatches) {
    List<MisMatch> mismatches_filter = Lists.newArrayList(Iterables.filter(mismatches, new Predicate<MisMatch>() {
      public boolean apply(MisMatch arg0) {
        return arg0.isAddition();
      }
    }));
    List<Addition> additions = Lists.newArrayList();
    for (MisMatch misMatch : mismatches_filter) {
      additions.add(misMatch.createAddition());
    }
    return additions;
  }

  public List<Removal> getOmissions(List<MisMatch> mismatches) {
    List<MisMatch> mismatches_filter = Lists.newArrayList(Iterables.filter(mismatches, new Predicate<MisMatch>() {
      public boolean apply(MisMatch arg0) {
        return arg0.isOmission();
      }
    }));
    List<Removal> omissions = Lists.newArrayList();
    for (MisMatch misMatch : mismatches_filter) {
      omissions.add(misMatch.createOmission());
    }
    return omissions;
  }

  public List<Replacement> getReplacements(List<MisMatch> mismatches) {
    List<MisMatch> mismatches_filter = Lists.newArrayList(Iterables.filter(mismatches, new Predicate<MisMatch>() {
      public boolean apply(MisMatch arg0) {
        return arg0.isReplacement();
      }
    }));
    List<Replacement> replacements = Lists.newArrayList();
    for (MisMatch misMatch : mismatches_filter) {
      replacements.add(misMatch.createReplacement());
    }
    return replacements;
  }

  public Matches getMatches(int i, int j) {
    return new Matches(getWitnessIndex(i), getWitnessIndex(j));
  }

  public Modifications detectTranspositions(int i, int j) {
    WitnessIndex witnessIndex = getWitnessIndex(i);
    WitnessIndex witnessIndex2 = getWitnessIndex(j);
    Matches matches = new Matches(witnessIndex, witnessIndex2);

    List<MatchSequence> matchSequencesForBase = TranspositionDetection.calculateMatchSequences(witnessIndex, witnessIndex2, matches.matches());
    List<MatchSequence> matchSequencesForWitness = TranspositionDetection.sortSequencesForWitness(matchSequencesForBase);
    List<Tuple2<MatchSequence>> matchSequenceTuples = TranspositionDetection.calculateSequenceTuples(matchSequencesForBase, matchSequencesForWitness);
    List<Tuple2<MatchSequence>> possibleTranspositionTuples = TranspositionDetection.filterAwayRealMatches(matchSequenceTuples);
    List<Transposition> transpositions = TranspositionDetection.calculateTranspositions(possibleTranspositionTuples);
    List<Modification> modifications = Lists.newArrayList();
    return new Modifications(modifications, transpositions);
  }

  public int numberOfWitnesses() {
    return witnessIndexes.size();
  }

  public List<MatchSequence> getMatchSequences(int i, int j) {
    WitnessIndex base = getWitnessIndex(i);
    WitnessIndex witness = getWitnessIndex(j);
    Set<Match> matches = getMatches(i, j).matches();
    return TranspositionDetection.calculateMatchSequences(base, witness, matches);
  }
}
