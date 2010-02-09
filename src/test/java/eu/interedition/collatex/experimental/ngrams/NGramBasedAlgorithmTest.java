package eu.interedition.collatex.experimental.ngrams;

import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import eu.interedition.collatex.experimental.ngrams.data.Witness;

public class NGramBasedAlgorithmTest {

  @Test
  public void testNgrams1() {
    // "The black cat", "The black and white cat"
    final Witness a = new Witness("A", "The black cat");
    final List<BiGram> ngrams = BiGrams.calculate(a);
    Assert.assertEquals(4, ngrams.size());
    Assert.assertEquals("# the", ngrams.get(0).getNormalized());
    Assert.assertEquals("the black", ngrams.get(1).getNormalized());
    Assert.assertEquals("black cat", ngrams.get(2).getNormalized());
    Assert.assertEquals("cat #", ngrams.get(3).getNormalized());
  }

  @Test
  public void testNgrams1b() {
    // "The black cat", "The black and white cat"
    final Witness b = new Witness("B", "The black and white cat");
    final List<BiGram> ngrams = BiGrams.calculate(b);
    Assert.assertEquals(6, ngrams.size());
    Assert.assertEquals("# the", ngrams.get(0).getNormalized());
    Assert.assertEquals("the black", ngrams.get(1).getNormalized());
    Assert.assertEquals("black and", ngrams.get(2).getNormalized());
    Assert.assertEquals("and white", ngrams.get(3).getNormalized());
    Assert.assertEquals("white cat", ngrams.get(4).getNormalized());
    Assert.assertEquals("cat #", ngrams.get(5).getNormalized());
  }

  @Test
  public void testOverlappingNGrams2() {
    // "The black cat", "The black and white cat"
    final Witness a = new Witness("A", "The black cat");
    final Witness b = new Witness("B", "The black and white cat");
    final List<Subsegment2> overlappingBiGrams = BiGrams.getOverlappingBiGrams(a, b);
    Assert.assertEquals(3, overlappingBiGrams.size());
    Assert.assertEquals("# the A: 0 B: 0", overlappingBiGrams.get(0).toString());
    Assert.assertEquals("the black A: 1 B: 1", overlappingBiGrams.get(1).toString());
    Assert.assertEquals("cat # A: 3 B: 5", overlappingBiGrams.get(2).toString());
  }

  @Test
  public void testUniqueNGrams3() {
    // "The black cat", "The black and white cat"
    final Witness a = new Witness("A", "The black cat");
    final Witness b = new Witness("B", "The black and white cat");
    final List<Subsegment2> uniqueBiGrams = BiGrams.getUniqueBiGramsForWitnessA(a, b);
    Assert.assertEquals(1, uniqueBiGrams.size());
    Assert.assertEquals("black cat A: 2", uniqueBiGrams.get(0).toString());
  }

  // TODO: getUniqueBiGrams should give back an BiGramIndex
  //

  @Test
  public void testUniqueNGrams3b() {
    // "The black cat", "The black and white cat"
    final Witness a = new Witness("A", "The black cat");
    final Witness b = new Witness("B", "The black and white cat");
    final List<BiGram> uniqueBiGrams = BiGrams.getUniqueBiGramsForWitnessB(a, b);
    Assert.assertEquals(3, uniqueBiGrams.size());
    Assert.assertEquals("black and", uniqueBiGrams.get(0).getNormalized());
    Assert.assertEquals("and white", uniqueBiGrams.get(1).getNormalized());
    Assert.assertEquals("white cat", uniqueBiGrams.get(2).getNormalized());
  }

}
