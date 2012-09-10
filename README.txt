CJKSieve

{<img src="https://secure.travis-ci.org/sul-dlss/CJKSieve.png?branch=master" alt="Build Status" />}[http://travis-ci.org/sul-dlss/CJKSieve]

This is a Lucene filter and Solr filter factory to only emit tokens when
certain combinations of Chinese, Japanese and Korean scripts are present/absent.

This is useful when you have a multi-lingual collection of documents that
contain text that can be separated into tokens with some form of whitespace
analysis (including StandardTokenizer, ICUTokenizer, etc.) AND you have Chinese,
Japenese and/or Korean text intermingled.  You would like to analyze the
Chinese text with a Chinese analysis chain, and the Japanese text with a
Japanese analysis chain, but you have no way of pre-determining which language
you have -- you only have script detection available, and perhaps your text is
not long enough for decent language analysis.

Example 1: IFF you detect Hiragana or Katakana script, you want to use Japanese morphological analyzer:

 <fieldType name="text_ja" class="solr.TextField">
   <analyzer>
     <tokenizer class="solr.JapaneseTokenizerFactory" mode="search"/>
     <filter class="solr.CJKSieveFilterFactory" emitIf="japanese"/>
     <filter class="solr.JapaneseBaseFormFilterFactory"/>
     <filter class="solr.JapanesePartOfSpeechStopFilterFactory" tags="lang/stoptags_ja.txt" enablePositionIncrements="true"/>
     <filter class="solr.ICUFoldingFilterFactory"/>
     <filter class="solr.StopFilterFactory" ignoreCase="true" words="lang/stopwords_ja.txt" enablePositionIncrements="true"/>
     <filter class="solr.JapaneseKatakanaStemFilterFactory" minimumLength="4" />
   </analyzer>
 </fieldType>

Example 2: IFF you detect Hangul script, you want to Bigram:
 <fieldType name="text_ko" class="solr.TextField">
   <analyzer>
     <tokenizer class="solr.StandardTokenizerFactory"/>
     <filter class="solr.CJKSieveFilterFactory" emitIf="hangul"/>
     <filter class="solr.ICUFoldingFilterFactory"/>
     <filter class="solr.LowerCaseFilterFactory"/>
     <filter class="solr.CJKBigramFilterFactory"
       han="true" hiragana="false"
       katakana="false" hangul="true" outputUnigrams="true" />
   </analyzer>
 </fieldType>

"emitIf" possible values:
   japanese: emit only if Hiragana and/or Katakana script characters are present
   hangul:   emit only if Hangul script characters are present
   han_solo: emit only if Han script characters are present and there are no Hangul, Hiragana or Katakana chars present
   no_cjk:   emit only if no Han, Hiragana, Katakana or Hangul script characters are present

To Contribute:

1. Fork it
2. Create your feature branch (`git checkout -b my-new-feature`)
3. Commit your changes (`git commit -am 'Added some feature'`)
4. Push to the branch (`git push origin my-new-feature`)
5. Create new Pull Request
