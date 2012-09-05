package edu.stanford.lucene.analysis.cjk;

import java.io.*;
import java.util.Random;

import org.apache.lucene.analysis.*;
import org.apache.lucene.analysis.ReusableAnalyzerBase.TokenStreamComponents;
import org.apache.lucene.analysis.cjk.CJKWidthFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.tokenattributes.*;
import org.junit.Test;

/**
 * @author ndushay
 *
 */
public class TestCJKHopperFilter extends BaseTokenStreamTestCase
{
    static {
    	System.setProperty("tests.asserts.gracious", "true");
    }

	private Analyzer analyzer = new ReusableAnalyzerBase()
	{
		@Override
		protected TokenStreamComponents createComponents(String fieldName, Reader reader)
		{
			Tokenizer t = new StandardTokenizer(TEST_VERSION_CURRENT, reader);
			try
			{
				return new TokenStreamComponents(t, new CJKHopperFilter(t, 0x00, true));
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}

			// Tokenizer source = new MockTokenizer(reader, MockTokenizer.WHITESPACE, false);
			// return new TokenStreamComponents(source, new CJKHopperFilter(t, CJKHopperFilter.HAN, true));
			return null;
		}
	};

	// TODO: test non-CJK only
@Test
	public void testNonCJKPassThru() throws Exception
	{
		assertAnalyzesTo(analyzer, "pass 多くの学生が me 試験に落ちた thru", new String[] { "pass", "me", "thru" }, new int[] { 0, 5, 7 }, new int[] { 4, 6, 11 }, new String[] { "<ALPHANUM>", "<ALPHANUM>", "<ALPHANUM>"}, new int[] { 1, 1, 1 });

//		TokenStream stream = new MockTokenizer(new StringReader("pass  多くの学生が me 試験に落ちた thru"));
//		assertAnalyzesTo(analyzer, "Ｔｅｓｔ １２３４", new String[] { "Test", "1234" }, new int[] { 0, 5 }, new int[] { 4, 9 });
	}

	/*
	 * public static void assertAnalyzesTo(Analyzer a, String input, String[] output, int startOffsets[], int endOffsets[], String types[], int posIncrements[], int posLengths[]) throws IOException {
	 *   assertTokenStreamContents(a.tokenStream("dummy", new StringReader(input)), output, startOffsets, endOffsets, types, posIncrements, posLengths, input.length());
	 * }
	 */

	// test Japanese scripts only

	// test Korean scripts only

	// test Han only

	// test more complex variants

	// testLain1Accents() is a copy of TestLatin1AccentFilter.testU().
	public void testLatin1Accents() throws Exception
	{
		TokenStream stream = new MockTokenizer(new StringReader("Des mot clés À LA CHAÎNE À Á Â Ã Ä Å Æ Ç È É Ê Ë Ì Í Î Ï Ĳ Ð Ñ" + " Ò Ó Ô Õ Ö Ø Œ Þ Ù Ú Û Ü Ý Ÿ à á â ã ä å æ ç è é ê ë ì í î ï ĳ" + " ð ñ ò ó ô õ ö ø œ ß þ ù ú û ü ý ÿ ﬁ ﬂ"), MockTokenizer.WHITESPACE, false);
		ASCIIFoldingFilter filter = new ASCIIFoldingFilter(stream);

		CharTermAttribute termAtt = filter.getAttribute(CharTermAttribute.class);
		filter.reset();
		assertTermEquals("Des", filter, termAtt);
		assertTermEquals("fl", filter, termAtt);
		assertFalse(filter.incrementToken());
	}

	/** blast some random strings through the analyzer */
	public void testRandomStrings() throws Exception
	{
		Analyzer a = new ReusableAnalyzerBase()
		{

			@Override
			protected TokenStreamComponents createComponents(String fieldName, Reader reader)
			{
				Tokenizer tokenizer = new MockTokenizer(reader, MockTokenizer.WHITESPACE, false);
				return new TokenStreamComponents(tokenizer, new ASCIIFoldingFilter(tokenizer));
			}
		};
		checkRandomData(random, a, 10000 * RANDOM_MULTIPLIER);
	}

	public void testEmptyTerm() throws IOException
	{
		Analyzer a = new ReusableAnalyzerBase()
		{
			@Override
			protected TokenStreamComponents createComponents(String fieldName, Reader reader)
			{
				Tokenizer tokenizer = new KeywordTokenizer(reader);
				return new TokenStreamComponents(tokenizer, new ASCIIFoldingFilter(tokenizer));
			}
		};
		checkOneTermReuse(a, "", "");
	}

	public void testHuge() throws Exception
	{
		assertAnalyzesTo(analyzer, "多くの学生が試験に落ちた" + "多くの学生が試験に落ちた" + "多くの学生が試験に落ちた" + "多くの学生が試験に落ちた" + "多くの学生が試験に落ちた" + "多くの学生が試験に落ちた" + "多くの学生が試験に落ちた" + "多くの学生が試験に落ちた" + "多くの学生が試験に落ちた" + "多くの学生が試験に落ちた" + "多くの学生が試験に落ちた", new String[] { "多く", "くの", "の学", "学生", "生が", "が試", "試験", "験に", "に落", "落ち", "ちた", "た多", "多く", "くの", "の学", "学生", "生が", "が試", "試験", "験に", "に落", "落ち", "ちた", "た多", "多く", "くの", "の学", "学生", "生が", "が試", "試験", "験に", "に落", "落ち", "ちた", "た多", "多く", "くの", "の学", "学生", "生が", "が試", "試験", "験に", "に落", "落ち", "ちた", "た多", "多く", "くの", "の学", "学生", "生が", "が試", "試験", "験に", "に落", "落ち", "ちた", "た多", "多く", "くの", "の学", "学生", "生が", "が試", "試験", "験に", "に落", "落ち", "ちた", "た多", "多く", "くの", "の学", "学生", "生が", "が試", "試験", "験に", "に落", "落ち", "ちた", "た多", "多く", "くの", "の学", "学生", "生が", "が試", "試験", "験に", "に落", "落ち", "ちた", "た多", "多く", "くの", "の学", "学生", "生が", "が試", "試験", "験に", "に落", "落ち", "ちた", "た多", "多く", "くの", "の学", "学生", "生が", "が試", "試験", "験に", "に落", "落ち", "ちた", "た多", "多く", "くの", "の学", "学生", "生が", "が試", "試験", "験に", "に落", "落ち", "ちた" });
	}

	public void testHanOnly() throws Exception
	{
		assertAnalyzesTo(analyzer, "多くの学生が試験に落ちた。", new String[] { "多", "く", "の", "学生", "が", "試験", "に", "落", "ち", "た" }, new int[] { 0, 1, 2, 3, 5, 6, 8, 9, 10, 11 }, new int[] { 1, 2, 3, 5, 6, 8, 9, 10, 11, 12 }, new String[] { "<SINGLE>", "<HIRAGANA>", "<HIRAGANA>", "<DOUBLE>", "<HIRAGANA>", "<DOUBLE>", "<HIRAGANA>", "<SINGLE>", "<HIRAGANA>", "<HIRAGANA>", "<SINGLE>" }, new int[] { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 }, new int[] { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 });
	}

	public void testAllScripts() throws Exception
	{
		assertAnalyzesTo(analyzer, "多くの学生が試験に落ちた。", new String[] { "多く", "くの", "の学", "学生", "生が", "が試", "試験", "験に", "に落", "落ち", "ちた" });
	}

	public void testUnigramsAndBigramsAllScripts() throws Exception
	{
		assertAnalyzesTo(analyzer, "多くの学生が試験に落ちた。", new String[] { "多", "多く", "く", "くの", "の", "の学", "学", "学生", "生", "生が", "が", "が試", "試", "試験", "験", "験に", "に", "に落", "落", "落ち", "ち", "ちた", "た" }, new int[] { 0, 0, 1, 1, 2, 2, 3, 3, 4, 4, 5, 5, 6, 6, 7, 7, 8, 8, 9, 9, 10, 10, 11 }, new int[] { 1, 2, 2, 3, 3, 4, 4, 5, 5, 6, 6, 7, 7, 8, 8, 9, 9, 10, 10, 11, 11, 12, 12 }, new String[] { "<SINGLE>", "<DOUBLE>", "<SINGLE>", "<DOUBLE>", "<SINGLE>", "<DOUBLE>", "<SINGLE>", "<DOUBLE>", "<SINGLE>", "<DOUBLE>", "<SINGLE>", "<DOUBLE>", "<SINGLE>", "<DOUBLE>", "<SINGLE>", "<DOUBLE>", "<SINGLE>", "<DOUBLE>", "<SINGLE>", "<DOUBLE>", "<SINGLE>", "<DOUBLE>", "<SINGLE>" }, new int[] { 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1 }, new int[] { 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1 });
	}

	public void testUnigramsAndBigramsHanOnly() throws Exception
	{
		assertAnalyzesTo(analyzer, "多くの学生が試験に落ちた。", new String[] { "多", "く", "の", "学", "学生", "生", "が", "試", "試験", "験", "に", "落", "ち", "た" }, new int[] { 0, 1, 2, 3, 3, 4, 5, 6, 6, 7, 8, 9, 10, 11 }, new int[] { 1, 2, 3, 4, 5, 5, 6, 7, 8, 8, 9, 10, 11, 12 }, new String[] { "<SINGLE>", "<HIRAGANA>", "<HIRAGANA>", "<SINGLE>", "<DOUBLE>", "<SINGLE>", "<HIRAGANA>", "<SINGLE>", "<DOUBLE>", "<SINGLE>", "<HIRAGANA>", "<SINGLE>", "<HIRAGANA>", "<HIRAGANA>", "<SINGLE>" }, new int[] { 1, 1, 1, 1, 0, 1, 1, 1, 0, 1, 1, 1, 1, 1 }, new int[] { 1, 1, 1, 1, 2, 1, 1, 1, 2, 1, 1, 1, 1, 1 });
	}

	public void testUnigramsAndBigramsHuge() throws Exception
	{
		assertAnalyzesTo(analyzer, "多くの学生が試験に落ちた" + "多くの学生が試験に落ちた" + "多くの学生が試験に落ちた" + "多くの学生が試験に落ちた" + "多くの学生が試験に落ちた" + "多くの学生が試験に落ちた" + "多くの学生が試験に落ちた" + "多くの学生が試験に落ちた" + "多くの学生が試験に落ちた" + "多くの学生が試験に落ちた" + "多くの学生が試験に落ちた", new String[] { "多", "多く", "く", "くの", "の", "の学", "学", "学生", "生", "生が", "が", "が試", "試", "試験", "験", "験に", "に", "に落", "落", "落ち", "ち", "ちた", "た", "た多", "多", "多く", "く", "くの", "の", "の学", "学", "学生", "生", "生が", "が", "が試", "試", "試験", "験", "験に", "に", "に落", "落", "落ち", "ち", "ちた", "た", "た多", "多", "多く", "く", "くの", "の", "の学", "学", "学生", "生", "生が", "が", "が試", "試", "試験", "験", "験に", "に", "に落", "落", "落ち", "ち", "ちた", "た", "た多", "多", "多く", "く", "くの", "の", "の学", "学", "学生", "生", "生が", "が", "が試", "試", "試験", "験", "験に", "に", "に落", "落", "落ち", "ち", "ちた", "た", "た多", "多", "多く", "く", "くの", "の", "の学", "学", "学生", "生", "生が", "が", "が試", "試", "試験", "験", "験に", "に", "に落", "落", "落ち", "ち", "ちた", "た", "た多", "多", "多く", "く", "くの", "の", "の学", "学", "学生", "生", "生が", "が", "が試", "試", "試験", "験", "験に", "に", "に落", "落", "落ち", "ち", "ちた", "た", "た多", "多", "多く", "く", "くの", "の", "の学", "学", "学生", "生", "生が", "が", "が試", "試", "試験", "験", "験に", "に", "に落", "落", "落ち", "ち", "ちた", "た", "た多", "多", "多く", "く", "くの", "の", "の学", "学", "学生", "生", "生が", "が", "が試", "試", "試験", "験", "験に", "に", "に落", "落", "落ち", "ち", "ちた", "た", "た多", "多", "多く", "く", "くの", "の", "の学", "学", "学生", "生", "生が", "が", "が試", "試", "試験", "験", "験に", "に", "に落", "落", "落ち", "ち", "ちた", "た", "た多", "多", "多く", "く", "くの", "の", "の学", "学", "学生", "生", "生が", "が", "が試", "試", "試験", "験", "験に", "に", "に落", "落", "落ち", "ち", "ちた", "た", "た多", "多", "多く", "く", "くの", "の", "の学", "学", "学生", "生", "生が", "が", "が試", "試", "試験", "験", "験に", "に", "に落", "落", "落ち", "ち", "ちた", "た" });
	}

	void assertTermEquals(String expected, TokenStream stream, CharTermAttribute termAtt) throws Exception
	{
		assertTrue(stream.incrementToken());
		assertEquals(expected, termAtt.toString());
	}
}
