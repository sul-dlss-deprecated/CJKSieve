package edu.stanford.lucene.analysis.cjk;

import java.io.IOException;
import java.util.*;

import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;
import org.apache.lucene.util.AttributeSource;

/**
 * Emits tokens that are generated from StandardTokenizer or ICUTokenizer,
 * depending on value of emitType
 * <p>
 * If emitType is:
 *  HANGUL, then only emit tokens if at least one Hangul character is found.
 *  JAPANESE, then only emit tokens if at least one Hiragana or Katakana character is found
 *  HAN_SOLO, then only emit tokens if at least one Han character is found, and no Hangul, Hiragana or Katakana chars are found
 *  NO_CJK, then only emit tokens if no characters are found in Han, Hiragana, Katakana or Hangul scripts.
 * <p>
 *
 * cache implementation from   org.apache.lucene.analysis.CachingTokenFilter
 *
 * @author Naomi Dushay
 *
 */
public class CJKHopperFilter extends TokenFilter
{
	// the CJK token types from StandardTokenizer
	private static final String HAN_TYPE = StandardTokenizer.TOKEN_TYPES[StandardTokenizer.IDEOGRAPHIC];
	private static final String HIRAGANA_TYPE = StandardTokenizer.TOKEN_TYPES[StandardTokenizer.HIRAGANA];
	private static final String KATAKANA_TYPE = StandardTokenizer.TOKEN_TYPES[StandardTokenizer.KATAKANA];
	private static final String HANGUL_TYPE = StandardTokenizer.TOKEN_TYPES[StandardTokenizer.HANGUL];

	/** under which conditions should this filter emit tokens? */
	private final CJKEmitType emitType;

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
	 * Create a new CJKHopperFilter, emitting tokens per emitType
	 * @param in
	 * @param emitType from {@link CJKEmitType},
	 */
	public CJKHopperFilter(TokenStream in, CJKEmitType emitType)
	{
		super(in);
		this.emitType = emitType;
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

			switch (emitType)
			{
				case HANGUL:
					if (tokensHaveHangul)
						return true;
					break;
				case JAPANESE:
					if (tokensHaveHiragana || tokensHaveKatakana)
						return true;
					break;
				case HAN_SOLO:
					if (tokensHaveHan &&
							! (tokensHaveHangul || tokensHaveHiragana || tokensHaveKatakana))
						return true;
					break;
				case NO_CJK:
					if (! (tokensHaveHan || tokensHaveHangul || tokensHaveHiragana || tokensHaveKatakana))
						return true;
			}

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
