package edu.stanford.lucene.analysis.cjk;

/**
 * CJKSieve emit types
 * @author - Naomi Dushay
 */
public enum CJKEmitType
{
	/** emit only if Hangul script characters are present */
	HANGUL,
	/** emit only if Hiragana or Katakana script characters are present */
	JAPANESE,
	/** emit only if Han script characters are present and there are no Hangul, Hiragana or Katakana chars present */
	HAN_SOLO,
	/** emit only if no Han, Hiragana, Katakana or Hangul script characters are present */
	NO_CJK;
}
