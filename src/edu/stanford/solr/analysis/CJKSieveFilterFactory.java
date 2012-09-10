/*
Copyright (c) 2012.
The Board of Trustees of the Leland Stanford Junior University.
All rights reserved.

Redistribution and use of this distribution in source and binary forms, with or
without modification, are permitted provided that: The above copyright notice
and this permission notice appear in all copies and supporting documentation;
The name, identifiers, and trademarks of The Board of Trustees of the Leland
Stanford Junior University are not used in advertising or publicity without the
express prior written permission of The Board of Trustees of the Leland Stanford
Junior University; Recipients acknowledge that this distribution is made
available as a research courtesy, "as is", potentially with defects, without
 any obligation on the part of The Board of Trustees of the Leland Stanford
 Junior University to provide support, services, or repair;

THE BOARD OF TRUSTEES OF THE LELAND STANFORD JUNIOR UNIVERSITY DISCLAIMS ALL
WARRANTIES, EXPRESS OR IMPLIED, WITH REGARD TO THIS SOFTWARE, INCLUDING WITHOUT
LIMITATION ALL IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
PARTICULAR PURPOSE, AND IN NO EVENT SHALL THE BOARD OF TRUSTEES OF THE LELAND
STANFORD JUNIOR UNIVERSITY BE LIABLE FOR ANY SPECIAL, INDIRECT OR CONSEQUENTIAL
DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS,
WHETHER IN AN ACTION OF CONTRACT, TORT (INCLUDING NEGLIGENCE) OR STRICT
LIABILITY, ARISING OUT OF OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS
SOFTWARE.
*/

package edu.stanford.solr.analysis;

import java.util.Map;

import org.apache.lucene.analysis.TokenStream;
import org.apache.solr.analysis.BaseTokenFilterFactory;

import edu.stanford.lucene.analysis.cjk.CJKSieveFilter;
import edu.stanford.lucene.analysis.cjk.CJKEmitType;

/**
 * Factory for {@link CJKSieveFilter}.
 *
 * Example 1: IFF you detect Hiragana or Katakana script, you want to use
 *  Japanese morphological analyzer:
 * <pre class="prettyprint" >
 * &lt;fieldType name="text_ja" class="solr.TextField"&gt;
 *   &lt;analyzer&gt;
 *     &lt;tokenizer class="solr.JapaneseTokenizerFactory" mode="search"/&gt;
 *     &lt;filter class="edu.stanford.solr.analysis.CJKSieveFilterFactory" emitIf="japanese"/&gt;
 *     &lt;filter class="solr.JapaneseBaseFormFilterFactory"/&gt;
 *     &lt;filter class="solr.JapanesePartOfSpeechStopFilterFactory" tags="lang/stoptags_ja.txt" enablePositionIncrements="true"/&gt;
 *     &lt;filter class="solr.ICUFoldingFilterFactory"/&gt;
 *     &lt;filter class="solr.StopFilterFactory" ignoreCase="true" words="lang/stopwords_ja.txt" enablePositionIncrements="true"/&gt;
 *     &lt;filter class="solr.JapaneseKatakanaStemFilterFactory" minimumLength="4" /&gt;
 *   &lt;/analyzer&gt;
 * &lt;/fieldType&gt;</pre>
 *
 * Example 2: IFF you detect Hangul script, you want to Bigram:
 * <pre class="prettyprint" >
 * &lt;fieldType name="text_ko" class="solr.TextField"&gt;
 *   &lt;analyzer&gt;
 *     &lt;tokenizer class="solr.StandardTokenizerFactory"/&gt;
 *     &lt;filter class="edu.stanford.solr.analysis.CJKSieveFilterFactory" emitIf="hangul"/&gt;
 *     &lt;filter class="solr.ICUFoldingFilterFactory"/&gt;
 *     &lt;filter class="solr.LowerCaseFilterFactory"/&gt;
 *     &lt;filter class="solr.CJKBigramFilterFactory"
 *       han="true" hiragana="false"
 *       katakana="false" hangul="true" outputUnigrams="true" /&gt;
 *   &lt;/analyzer&gt;
 * &lt;/fieldType&gt;</pre>
 *
 * @author Naomi Dushay
 *
 */
public class CJKSieveFilterFactory extends BaseTokenFilterFactory
{
	CJKEmitType emitType;

	@Override
	public void init(Map<String,String> args)
	{
	    super.init(args);

	    String emitIfStr = args.get("emitIf");
	    if (emitIfStr == null)
	      throw new RuntimeException("Configuration Error: missing parameter 'emitIf' for CJKSieveFilterFactory (must be one of:  japanese, hangul, han_solo, no_cjk");

	    if (emitIfStr.equals("japanese"))
	      emitType = CJKEmitType.JAPANESE;
	    else if (emitIfStr.equals("hangul"))
	      emitType = CJKEmitType.HANGUL;
	    else if (emitIfStr.equals("han_solo"))
	      emitType = CJKEmitType.HAN_SOLO;
	    else if (emitIfStr.equals("no_cjk"))
	      emitType = CJKEmitType.NO_CJK;
	    else
	      throw new RuntimeException("Configuration Error: parameter 'emitIf' for CJKSieveFilterFactory must be one of:  japanese, hangul, han_solo, no_cjk");
	  }

	  public TokenStream create(TokenStream input) {
	    return new CJKSieveFilter(input, emitType);
	  }
}
