package edu.stanford.lucene.analysis.cjk;

import java.io.*;
import java.util.Random;

import org.apache.lucene.analysis.*;
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
		assertAnalyzesTo(
				getStdTokenAnalyzer(0x00, false, false, true),
				"pass 多くの学生が me 試験に落ちた thru",
				new String[] { "pass", "me", "thru" },
				new int[] { 0, 5, 7 },   // startOffsets
				new int[] { 4, 6, 11 },  // endOffsets
				new String[] { "<ALPHANUM>", "<ALPHANUM>", "<ALPHANUM>"},
				new int[] { 1, 1, 1 });  // positionIncrements

//		TokenStream stream = new MockTokenizer(new StringReader("pass  多くの学生が me 試験に落ちた thru"));
//		assertAnalyzesTo(analyzer, "Ｔｅｓｔ １２３４", new String[] { "Test", "1234" }, new int[] { 0, 5 }, new int[] { 4, 9 });
	}

	/*
	 * public static void assertAnalyzesTo(Analyzer a, String input, String[] output, int startOffsets[], int endOffsets[], String types[], int posIncrements[], int posLengths[]) throws IOException {
	 *   assertTokenStreamContents(a.tokenStream("dummy", new StringReader(input)), output, startOffsets, endOffsets, types, posIncrements, posLengths, input.length());
	 * }
	 */

@Test
	public void testJapaneseEmitsIfHiraganaPresent() throws Exception
	{
		// another possible test string:   を知るための is hiragana except 知 which is kanji
		assertAnalyzesTo( getStdTokenAnalyzer(0x00, false, true, false),
			"近世仮名遣い論の研究 -- chars 6, 8: い の are hiragana",
			new String[] { "近", "世", "仮", "名", "遣", "い", "論", "の", "研", "究", "chars", "6", "8", "い", "の", "are", "hiragana" },
			new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 14, 20, 23, 26, 28, 30, 34 },   // startOffsets
			new int[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 19, 21, 24, 27, 29, 33, 42 },  // endOffsets
			new String[] { "<IDEOGRAPHIC>", "<IDEOGRAPHIC>", "<IDEOGRAPHIC>", "<IDEOGRAPHIC>", "<IDEOGRAPHIC>", "<HIRAGANA>", "<IDEOGRAPHIC>", "<HIRAGANA>", "<IDEOGRAPHIC>", "<IDEOGRAPHIC>", "<ALPHANUM>", "<NUM>", "<NUM>", "<HIRAGANA>", "<HIRAGANA>", "<ALPHANUM>", "<ALPHANUM>"},
			new int[] { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1});  // positionIncrements
	}

@Test
	public void testJapaneseEmitsIfKatakanaPresent() throws Exception
	{
		assertAnalyzesTo( getStdTokenAnalyzer(0x00, false, true, false),
			"マンガ is katakana",
			new String[] { "マンガ", "is",  "katakana" },
			new int[] { 0, 4, 7 },   // startOffsets
			new int[] { 3, 6, 15 },  // endOffsets
			new String[] { "<KATAKANA>", "<ALPHANUM>", "<ALPHANUM>" },
			new int[] { 1, 1, 1});  // positionIncrements
	}

@Test
	public void testJapaneseEmitsIfBothPresent() throws Exception
	{
		assertAnalyzesTo( getStdTokenAnalyzer(0x00, false, true, false),
			"日本マンガを知るためのブック・ガイド",
			new String[] { "日", "本", "マンガ", "を", "知", "る", "た", "め", "の", "ブック", "ガイド" },
			new int[] { 0, 1, 2, 5, 6, 7, 8, 9, 10, 11, 15 },   // startOffsets
			new int[] { 1, 2, 5, 6, 7, 8, 9, 10, 11, 14, 18 },  // endOffsets
			new String[] { "<IDEOGRAPHIC>", "<IDEOGRAPHIC>", "<KATAKANA>", "<HIRAGANA>", "<IDEOGRAPHIC>", "<HIRAGANA>", "<HIRAGANA>", "<HIRAGANA>", "<HIRAGANA>", "<KATAKANA>", "<KATAKANA>" },
			new int[] { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 });  // positionIncrements
	}

@Test
	public void testJapaneseNothingOutIfScriptsAbsent() throws Exception
	{
		Analyzer a = getStdTokenAnalyzer(0x00, false, true, false);
		assertTokenStreamContents(a.tokenStream("dummy", new StringReader("南滿洲鐵道株式會社 traditional han only")), new String[] {});
		assertTokenStreamContents(a.tokenStream("dummy", new StringReader("Simplified  中国地方志集成")), new String[] {});
		assertTokenStreamContents(a.tokenStream("dummy", new StringReader("No CJK here ... Des mot clés À LA CHAÎNE À Á ")), new String[] {});
	}


@Test
	public void testEmitIfHangulPresent() throws Exception
	{
		assertAnalyzesTo( getStdTokenAnalyzer(0x00, true, false, false),
			"한국경제 hangul",
			new String[] { "한국경제", "hangul" },
			new int[] { 0, 5 },   // startOffsets
			new int[] { 4, 11 },  // endOffsets
			new String[] { "<HANGUL>", "<ALPHANUM>" },
			new int[] { 1, 1 });  // positionIncrements
		assertAnalyzesTo( getStdTokenAnalyzer(0x00, true, false, false),
			"한국사 의 壇君 인식 hangul and hancha",
			new String[] { "한국사", "의", "壇", "君", "인식", "hangul", "and", "hancha" },
			new int[] { 0, 4, 6, 7, 9, 12, 19, 23 },   // startOffsets
			new int[] { 3, 5, 7, 8, 11, 18, 22, 29 },  // endOffsets
			new String[] { "<HANGUL>", "<HANGUL>", "<IDEOGRAPHIC>", "<IDEOGRAPHIC>", "<HANGUL>", "<ALPHANUM>", "<ALPHANUM>", "<ALPHANUM>" },
			new int[] { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 });  // positionIncrements
	}

	// test Han only
@Test
	public void testTraditionalHanOnly() throws Exception
	{
		assertAnalyzesTo( getStdTokenAnalyzer(0x00, true, false, false),
			"南滿洲鐵道株式會社 traditional han",
			new String[] { "南", "滿", "洲", "鐵", "道", "株", "式", "會", "社", "traditional", "han" },
			new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 11, 23 },   // startOffsets
			new int[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 12, 26 },  // endOffsets
			new String[] { "<IDEOGRAPHIC>", "<IDEOGRAPHIC>", "<IDEOGRAPHIC>", "<IDEOGRAPHIC>", "<IDEOGRAPHIC>", "<IDEOGRAPHIC>", "<IDEOGRAPHIC>", "<IDEOGRAPHIC>", "<IDEOGRAPHIC>", "<ALPHANUM>", "<ALPHANUM>" },
			new int[] { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 });  // positionIncrements
	}

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
		fail("here for reference");
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
		fail("here for reference");
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
		fail("here for reference");
	}

	public void testHuge() throws Exception
	{
		assertAnalyzesTo(analyzer, "多くの学生が試験に落ちた" + "多くの学生が試験に落ちた" + "多くの学生が試験に落ちた" + "多くの学生が試験に落ちた" + "多くの学生が試験に落ちた" + "多くの学生が試験に落ちた" + "多くの学生が試験に落ちた" + "多くの学生が試験に落ちた" + "多くの学生が試験に落ちた" + "多くの学生が試験に落ちた" + "多くの学生が試験に落ちた", new String[] { "多く", "くの", "の学", "学生", "生が", "が試", "試験", "験に", "に落", "落ち", "ちた", "た多", "多く", "くの", "の学", "学生", "生が", "が試", "試験", "験に", "に落", "落ち", "ちた", "た多", "多く", "くの", "の学", "学生", "生が", "が試", "試験", "験に", "に落", "落ち", "ちた", "た多", "多く", "くの", "の学", "学生", "生が", "が試", "試験", "験に", "に落", "落ち", "ちた", "た多", "多く", "くの", "の学", "学生", "生が", "が試", "試験", "験に", "に落", "落ち", "ちた", "た多", "多く", "くの", "の学", "学生", "生が", "が試", "試験", "験に", "に落", "落ち", "ちた", "た多", "多く", "くの", "の学", "学生", "生が", "が試", "試験", "験に", "に落", "落ち", "ちた", "た多", "多く", "くの", "の学", "学生", "生が", "が試", "試験", "験に", "に落", "落ち", "ちた", "た多", "多く", "くの", "の学", "学生", "生が", "が試", "試験", "験に", "に落", "落ち", "ちた", "た多", "多く", "くの", "の学", "学生", "生が", "が試", "試験", "験に", "に落", "落ち", "ちた", "た多", "多く", "くの", "の学", "学生", "生が", "が試", "試験", "験に", "に落", "落ち", "ちた" });
		fail("here for reference");
	}

	public void testHanOnly() throws Exception
	{
		assertAnalyzesTo(analyzer, "多くの学生が試験に落ちた。", new String[] { "多", "く", "の", "学生", "が", "試験", "に", "落", "ち", "た" }, new int[] { 0, 1, 2, 3, 5, 6, 8, 9, 10, 11 }, new int[] { 1, 2, 3, 5, 6, 8, 9, 10, 11, 12 }, new String[] { "<SINGLE>", "<HIRAGANA>", "<HIRAGANA>", "<DOUBLE>", "<HIRAGANA>", "<DOUBLE>", "<HIRAGANA>", "<SINGLE>", "<HIRAGANA>", "<HIRAGANA>", "<SINGLE>" }, new int[] { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 }, new int[] { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 });
		fail("here for reference");
	}

	public void testAllScripts() throws Exception
	{
		assertAnalyzesTo(analyzer, "多くの学生が試験に落ちた。", new String[] { "多く", "くの", "の学", "学生", "生が", "が試", "試験", "験に", "に落", "落ち", "ちた" });
		fail("here for reference");
	}

	public void testUnigramsAndBigramsAllScripts() throws Exception
	{
		assertAnalyzesTo(analyzer, "多くの学生が試験に落ちた。", new String[] { "多", "多く", "く", "くの", "の", "の学", "学", "学生", "生", "生が", "が", "が試", "試", "試験", "験", "験に", "に", "に落", "落", "落ち", "ち", "ちた", "た" }, new int[] { 0, 0, 1, 1, 2, 2, 3, 3, 4, 4, 5, 5, 6, 6, 7, 7, 8, 8, 9, 9, 10, 10, 11 }, new int[] { 1, 2, 2, 3, 3, 4, 4, 5, 5, 6, 6, 7, 7, 8, 8, 9, 9, 10, 10, 11, 11, 12, 12 }, new String[] { "<SINGLE>", "<DOUBLE>", "<SINGLE>", "<DOUBLE>", "<SINGLE>", "<DOUBLE>", "<SINGLE>", "<DOUBLE>", "<SINGLE>", "<DOUBLE>", "<SINGLE>", "<DOUBLE>", "<SINGLE>", "<DOUBLE>", "<SINGLE>", "<DOUBLE>", "<SINGLE>", "<DOUBLE>", "<SINGLE>", "<DOUBLE>", "<SINGLE>", "<DOUBLE>", "<SINGLE>" }, new int[] { 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1 }, new int[] { 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1 });
		fail("here for reference");
	}

	public void testUnigramsAndBigramsHanOnly() throws Exception
	{
		assertAnalyzesTo(analyzer, "多くの学生が試験に落ちた。", new String[] { "多", "く", "の", "学", "学生", "生", "が", "試", "試験", "験", "に", "落", "ち", "た" }, new int[] { 0, 1, 2, 3, 3, 4, 5, 6, 6, 7, 8, 9, 10, 11 }, new int[] { 1, 2, 3, 4, 5, 5, 6, 7, 8, 8, 9, 10, 11, 12 }, new String[] { "<SINGLE>", "<HIRAGANA>", "<HIRAGANA>", "<SINGLE>", "<DOUBLE>", "<SINGLE>", "<HIRAGANA>", "<SINGLE>", "<DOUBLE>", "<SINGLE>", "<HIRAGANA>", "<SINGLE>", "<HIRAGANA>", "<HIRAGANA>", "<SINGLE>" }, new int[] { 1, 1, 1, 1, 0, 1, 1, 1, 0, 1, 1, 1, 1, 1 }, new int[] { 1, 1, 1, 1, 2, 1, 1, 1, 2, 1, 1, 1, 1, 1 });
		fail("here for reference");
	}

	public void testUnigramsAndBigramsHuge() throws Exception
	{
		assertAnalyzesTo(analyzer, "多くの学生が試験に落ちた" + "多くの学生が試験に落ちた" + "多くの学生が試験に落ちた" + "多くの学生が試験に落ちた" + "多くの学生が試験に落ちた" + "多くの学生が試験に落ちた" + "多くの学生が試験に落ちた" + "多くの学生が試験に落ちた" + "多くの学生が試験に落ちた" + "多くの学生が試験に落ちた" + "多くの学生が試験に落ちた", new String[] { "多", "多く", "く", "くの", "の", "の学", "学", "学生", "生", "生が", "が", "が試", "試", "試験", "験", "験に", "に", "に落", "落", "落ち", "ち", "ちた", "た", "た多", "多", "多く", "く", "くの", "の", "の学", "学", "学生", "生", "生が", "が", "が試", "試", "試験", "験", "験に", "に", "に落", "落", "落ち", "ち", "ちた", "た", "た多", "多", "多く", "く", "くの", "の", "の学", "学", "学生", "生", "生が", "が", "が試", "試", "試験", "験", "験に", "に", "に落", "落", "落ち", "ち", "ちた", "た", "た多", "多", "多く", "く", "くの", "の", "の学", "学", "学生", "生", "生が", "が", "が試", "試", "試験", "験", "験に", "に", "に落", "落", "落ち", "ち", "ちた", "た", "た多", "多", "多く", "く", "くの", "の", "の学", "学", "学生", "生", "生が", "が", "が試", "試", "試験", "験", "験に", "に", "に落", "落", "落ち", "ち", "ちた", "た", "た多", "多", "多く", "く", "くの", "の", "の学", "学", "学生", "生", "生が", "が", "が試", "試", "試験", "験", "験に", "に", "に落", "落", "落ち", "ち", "ちた", "た", "た多", "多", "多く", "く", "くの", "の", "の学", "学", "学生", "生", "生が", "が", "が試", "試", "試験", "験", "験に", "に", "に落", "落", "落ち", "ち", "ちた", "た", "た多", "多", "多く", "く", "くの", "の", "の学", "学", "学生", "生", "生が", "が", "が試", "試", "試験", "験", "験に", "に", "に落", "落", "落ち", "ち", "ちた", "た", "た多", "多", "多く", "く", "くの", "の", "の学", "学", "学生", "生", "生が", "が", "が試", "試", "試験", "験", "験に", "に", "に落", "落", "落ち", "ち", "ちた", "た", "た多", "多", "多く", "く", "くの", "の", "の学", "学", "学生", "生", "生が", "が", "が試", "試", "試験", "験", "験に", "に", "に落", "落", "落ち", "ち", "ちた", "た", "た多", "多", "多く", "く", "くの", "の", "の学", "学", "学生", "生", "生が", "が", "が試", "試", "試験", "験", "験に", "に", "に落", "落", "落ち", "ち", "ちた", "た" });
		fail("here for reference");
	}

	void assertTermEquals(String expected, TokenStream stream, CharTermAttribute termAtt) throws Exception
	{
		assertTrue(stream.incrementToken());
		assertEquals(expected, termAtt.toString());
	}


	/**
	 * @param flags OR'ed set from {@link CJKHopperFilter#HAN},
	 *            {@link CJKHopperFilter#HIRAGANA},
	 *            {@link CJKHopperFilter#KATAKANA},
	 *            {@link CJKHopperFilter#HANGUL}
	 * @param emitIfNoCJK true if non-CJK script characters should also be output.
	 */
	private Analyzer getStdTokenAnalyzer(final int flags, final boolean emitIfHangul, final boolean emitIfJapanese, final boolean emitIfNoCJK) {
		Analyzer analyzer = new ReusableAnalyzerBase()
		{
			protected TokenStreamComponents createComponents(String fieldName, Reader reader)
			{
				Tokenizer t = new StandardTokenizer(TEST_VERSION_CURRENT, reader);
				try
				{
					return new TokenStreamComponents(t, new CJKHopperFilter(t, flags, emitIfHangul, emitIfJapanese, emitIfNoCJK));
				}
				catch (IOException e) { e.printStackTrace(); }
				return null;
			}
		};

		return analyzer;
	}
}
