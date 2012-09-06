package edu.stanford.lucene.analysis.cjk;

import java.io.IOException;
import java.util.*;

import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;
import org.apache.lucene.util.AttributeSource;

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
 * cache implementation from   org.apache.lucene.analysis.CachingTokenFilter
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

	/** true if we should emit tokens when no CJK script characters are present */
	private final boolean emitIfNoCJK;

	/** true if we should emit tokens when Hiragana or Katakana script characters are present */
	private final boolean emitIfJapanese;

	private boolean tokensHaveHan = false;
	private boolean tokensHaveHiragana = false;
	private boolean tokensHaveKatakana = false;
	private boolean tokensHaveHangul = false;

	private final TypeAttribute typeAtt = addAttribute(TypeAttribute.class);

	// used for token cache implementation
	private List<AttributeSource.State> cache = null;
	private Iterator<AttributeSource.State> iterator = null;
	private AttributeSource.State finalState;

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
		this.emitIfNoCJK = emitNonCJK;
		this.emitIfJapanese = false;
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
	public CJKHopperFilter(TokenStream in, int flags, boolean emitIfJapanese, boolean emitNonCJK) throws IOException
	{
		super(in);
		emitHan = (flags & HAN) == 0 ? false : true;
		emitHiragana = (flags & HIRAGANA) == 0 ? false : true;
		emitKatakana = (flags & KATAKANA) == 0 ? false : true;
		emitHangul = (flags & HANGUL) == 0 ? false : true;
		this.emitIfNoCJK = emitNonCJK;
		this.emitIfJapanese = emitIfJapanese;
	}

	@Override
	public final boolean incrementToken() throws IOException
	{
		if (cache == null)
		{
			// fill cache lazily
			cache = new LinkedList<AttributeSource.State>();
			fillCache();
			iterator = cache.iterator();
		}

		if (iterator.hasNext())
		{
			// Since the TokenFilter can be reset, the tokens need to be preserved as immutable.
			restoreState(iterator.next());
			String type = typeAtt.type();

			if (tokensHaveHan && emitHan)

			if (emitHan && tokensHaveHan)
				return true;
			if (emitHiragana && tokensHaveHiragana)
				return true;
			if (emitKatakana && tokensHaveKatakana)
				return true;
			if (emitHangul && tokensHaveHangul)
				return true;
			if (emitIfJapanese && (tokensHaveHiragana || tokensHaveKatakana))
				return true;
			if (emitIfNoCJK && !tokensHaveHan && !tokensHaveHangul && !tokensHaveHiragana && !tokensHaveKatakana)
				return true;

			return false;
		}

		// else the cache is exhausted, return false
		return false;
	}

	@Override
	public final void end() throws IOException
	{
		if (finalState != null)
			restoreState(finalState);
	}

	@Override
	public void reset() throws IOException
	{
		if (cache != null)
			iterator = cache.iterator();
	}

	private void fillCache() throws IOException
	{
		while(input.incrementToken())
		{
			cache.add(captureState());
			String type = typeAtt.type();
			if (type == HAN_TYPE)
				tokensHaveHan = true;
			else if (type == HIRAGANA_TYPE)
				tokensHaveHiragana = true;
			else if (type == KATAKANA_TYPE)
				tokensHaveKatakana = true;
			else if (type == HANGUL_TYPE)
				tokensHaveHangul = true;
		}
		// capture final state
		input.end();
		finalState = captureState();
	}

}
