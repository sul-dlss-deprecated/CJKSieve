package edu.stanford.solr.analysis;

import java.util.Map;

import org.apache.lucene.analysis.TokenStream;
import org.apache.solr.analysis.BaseTokenFilterFactory;

import edu.stanford.lucene.analysis.cjk.CJKHopperFilter;
import edu.stanford.lucene.analysis.cjk.CJKEmitType;

/**
 * Factory for {@link CJKHopperFilter}.
 *
 * Example 1: IFF you detect Hiragana or Katakana script, you want to use
 *  Japanese morphological analyzer:
 * <pre class="prettyprint" >
 * &lt;fieldType name="text_ja" class="solr.TextField"&gt;
 *   &lt;analyzer&gt;
 *     &lt;tokenizer class="solr.JapaneseTokenizerFactory" mode="search"/&gt;
 *     &lt;filter class="solr.CJKHopperFilterFactory" emitIf="japanese"/&gt;
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
 *     &lt;filter class="solr.CJKHopperFilterFactory" emitIf="hangul"/&gt;
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
public class CJKHopperFilterFactory extends BaseTokenFilterFactory
{
	CJKEmitType emitType;

	@Override
	public void init(Map<String,String> args)
	{
	    super.init(args);

	    String emitIfStr = args.get("emitIf");
	    if (emitIfStr == null)
	      throw new RuntimeException("Configuration Error: missing parameter 'emitIf' for CJKHopperFilterFactory (must be one of:  japanese, hangul, han_solo, no_cjk");

	    if (emitIfStr.equals("japanese"))
	      emitType = CJKEmitType.JAPANESE;
	    else if (emitIfStr.equals("hangul"))
	      emitType = CJKEmitType.HANGUL;
	    else if (emitIfStr.equals("han_solo"))
	      emitType = CJKEmitType.HAN_SOLO;
	    else if (emitIfStr.equals("no_cjk"))
	      emitType = CJKEmitType.NO_CJK;
	    else
	      throw new RuntimeException("Configuration Error: parameter 'emitIf' for CJKHopperFilterFactory must be one of:  japanese, hangul, han_solo, no_cjk");
	  }

	  public TokenStream create(TokenStream input) {
	    return new CJKHopperFilter(input, emitType);
	  }
}
