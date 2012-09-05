package edu.stanford.lucene.analysis.cjk;

import java.io.IOException;

import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.tokenattributes.*;

/**
 * Emits CJK unigram tokens that are generated from StandardTokenizer
 * or ICUTokenizer, depending on values of flags.
 * <p>
 * CJK types are set by these tokenizers, but you can also use
 * {@link #CJKHopperFilter(TokenStream, int)} to explicitly control which of the
 * CJK scripts are emitted.
 * <p>
 * In all cases, all non-CJK input is passed thru or removed, depending on
 * non-CJK flag.
 *
 * @author Naomi Dushay
 *
 */
public class CJKHopperFilter extends TokenFilter
{
	// configuration
	/** emit flag for Han Ideographs */
	public static final int HAN = 1;
	/** emit flag for Hiragana */
	public static final int HIRAGANA = 2;
	/** emit flag for Katakana */
	public static final int KATAKANA = 4;
	/** emit flag for Hangul */
	public static final int HANGUL = 8;

	/** when we emit a bigram, its then marked as this type */
	// public static final String DOUBLE_TYPE = "<DOUBLE>";
	/** when we emit a unigram, its then marked as this type */
	// public static final String SINGLE_TYPE = "<SINGLE>";

	// the types from StandardTokenizer
	private static final String HAN_TYPE = StandardTokenizer.TOKEN_TYPES[StandardTokenizer.IDEOGRAPHIC];
	private static final String HIRAGANA_TYPE = StandardTokenizer.TOKEN_TYPES[StandardTokenizer.HIRAGANA];
	private static final String KATAKANA_TYPE = StandardTokenizer.TOKEN_TYPES[StandardTokenizer.KATAKANA];
	private static final String HANGUL_TYPE = StandardTokenizer.TOKEN_TYPES[StandardTokenizer.HANGUL];

	// these are set to true if we want to pass the script through; false otherwise
	private final boolean emitHan;
	private final boolean emitHiragana;
	private final boolean emitKatakana;
	private final boolean emitHangul;

	// true if we should emit non CJK script characters
	private final boolean emitNonCJK;

	private final CharTermAttribute termAtt = addAttribute(CharTermAttribute.class);
	private final TypeAttribute typeAtt = addAttribute(TypeAttribute.class);
	private final OffsetAttribute offsetAtt = addAttribute(OffsetAttribute.class);
	private final PositionIncrementAttribute posIncrAtt = addAttribute(PositionIncrementAttribute.class);


	/**
	 * Calls {@link CJKHopperFilter#CJKBigramFilter(TokenStream, int)
	 * CJKHopperFilter(HAN | HIRAGANA | KATAKANA | HANGUL)}
	 * @throws IOException
	 */
	public CJKHopperFilter(TokenStream in) throws IOException
	{
		this(in, HAN | HIRAGANA | KATAKANA | HANGUL);
	}

	/**
	 * Calls {@link CJKHopperFilter#CJKHopperFilter(TokenStream, int, boolean)
	 * CJKHopperFilter(in, flags, false)}
	 * @throws IOException
	 */
	public CJKHopperFilter(TokenStream in, int flags) throws IOException
	{
		this(in, flags, false);
	}

	/**
	 * Create a new CJKHopperFilter, specifying which writing systems should be
	 * emitted,
	 * and whether or not non-CJK scripts should also be output.
	 *
	 * @param flags OR'ed set from {@link CJKHopperFilter#HAN},
	 *            {@link CJKHopperFilter#HIRAGANA},
	 *            {@link CJKHopperFilter#KATAKANA},
	 *            {@link CJKHopperFilter#HANGUL}
	 * @param emitNonCJK true if non-CJK script characters should also be
	 *            output.
	 * @throws IOException
	 */
	public CJKHopperFilter(TokenStream in, int flags, boolean emitNonCJK) throws IOException
	{
		super(in);
		emitHan = (flags & HAN) == 0 ? false : true;
		emitHiragana = (flags & HIRAGANA) == 0 ? false : true;
		emitKatakana = (flags & KATAKANA) == 0 ? false : true;
		emitHangul = (flags & HANGUL) == 0 ? false : true;
		this.emitNonCJK = emitNonCJK;
		readTokenStreamThenReset();
	}


	private boolean haveHan = false;
	private boolean haveHiragana = false;
	private boolean haveKatakana = false;
	private boolean haveHangul = false;
	private boolean haveNonCJK = false;

	private void readTokenStreamThenReset() throws IOException
	{
		State beforeState = input.captureState();
		while (input.incrementToken())
		{
			String type = typeAtt.type();
			if (type == HAN_TYPE)
				haveHan = true;
			else if (type == HIRAGANA_TYPE)
				haveHiragana = true;
			else if (type == KATAKANA_TYPE)
				haveKatakana = true;
			else if (type == HANGUL_TYPE)
				haveHangul = true;
			else
				haveNonCJK = true;
		}
		reset();
//		input.reset();
		input.restoreState(beforeState);


		 @Override
		  public void reset() throws IOException {
		    // reset our internal state
		    bufferIndex = 0;
		    offset = 0;
		    dataLen = 0;
		    finalOffset = 0;
		    ioBuffer.reset(); // make sure to reset the IO buffer!!
		  }


	}


	@Override
	public boolean incrementToken() throws IOException
	{
	    if (input.incrementToken())
	    {
		    int termLen = termAtt.length();
		    String type = typeAtt.type();
		    int startOffset = offsetAtt.startOffset();
		    int endOffset = offsetAtt.endOffset();
		    int posIncr = posIncrAtt.getPositionIncrement();

//		    clearAttributes();
//		    termAtt.setLength();
//	    	offsetAtt.setOffset(startOffset[index], endOffset[index+1]);
//	 	   typeAtt.setType(typeAtt.type());
//	    	posIncrAtt.setPositionIncrement(0);


		    if (emitHan && haveHan)
				return true;
			if (emitHiragana && haveHiragana)
				return true;
			if (emitKatakana && haveKatakana)
				return true;
			if (emitHangul && haveHangul)
				return true;
			if (emitNonCJK && haveNonCJK)
				return true;

			return false;
	    }
	    else
	    	return false;
	}

}
