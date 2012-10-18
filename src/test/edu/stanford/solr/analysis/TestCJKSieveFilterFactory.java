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

import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import org.apache.lucene.analysis.*;
import org.apache.lucene.analysis.standard.StandardTokenizer;

import org.junit.Test;

/**
 * Simple tests for CJKSieveFilterFactory.
 *
 * @author Naomi Dushay
 */
public class TestCJKSieveFilterFactory extends BaseTokenStreamTestCase
{
    static {
    	System.setProperty("tests.asserts.gracious", "true");
    }

@Test
    public void testJapanese() throws Exception
	{
		CJKSieveFilterFactory f = getCJKSieveFilterFactory("japanese");
		TokenStream stream = f.create(new StandardTokenizer(TEST_VERSION_CURRENT, new StringReader("マンガ is katakana")));
		assertTokenStreamContents(stream, new String[] { "マンガ", "is", "katakana" });
		stream = f.create(new StandardTokenizer(TEST_VERSION_CURRENT, new StringReader("한국경제 hangul only")));
		assertTokenStreamContents(stream, new String[] {});
	}

@Test
	public void testHanSolo() throws Exception
	{
		CJKSieveFilterFactory f = getCJKSieveFilterFactory("han_solo");
		TokenStream stream = f.create(new StandardTokenizer(TEST_VERSION_CURRENT, new StringReader("壇君")));
		assertTokenStreamContents(stream, new String[] { "壇", "君" });
		stream = f.create(new StandardTokenizer(TEST_VERSION_CURRENT, new StringReader("한국경제 hangul only")));
		assertTokenStreamContents(stream, new String[] {});
	}

@Test
	public void testHangul() throws Exception
	{
		CJKSieveFilterFactory f = getCJKSieveFilterFactory("hangul");
		TokenStream stream = f.create(new StandardTokenizer(TEST_VERSION_CURRENT, new StringReader("한국경제 hangul only")));
		assertTokenStreamContents(stream, new String[] { "한국경제", "hangul", "only" });
		stream = f.create(new StandardTokenizer(TEST_VERSION_CURRENT, new StringReader("マンガ is katakana")));
		assertTokenStreamContents(stream, new String[] {});
	}

@Test
	public void testCJ() throws Exception
	{
		CJKSieveFilterFactory f = getCJKSieveFilterFactory("cj");
		TokenStream stream = f.create(new StandardTokenizer(TEST_VERSION_CURRENT, new StringReader("マンガ is katakana")));
		assertTokenStreamContents(stream, new String[] { "マンガ", "is", "katakana" });
		stream = f.create(new StandardTokenizer(TEST_VERSION_CURRENT, new StringReader("壇君")));
		assertTokenStreamContents(stream, new String[] { "壇", "君" });
		stream = f.create(new StandardTokenizer(TEST_VERSION_CURRENT, new StringReader("한국경제 hangul only")));
		assertTokenStreamContents(stream, new String[] {});
	}

@Test
	public void testNoCJK() throws Exception
	{
		CJKSieveFilterFactory f = getCJKSieveFilterFactory("no_cjk");
		TokenStream stream = f.create(new StandardTokenizer(TEST_VERSION_CURRENT, new StringReader("no cjk")));
		assertTokenStreamContents(stream, new String[] { "no", "cjk" });
		stream = f.create(new StandardTokenizer(TEST_VERSION_CURRENT, new StringReader("マンガ is katakana")));
		assertTokenStreamContents(stream, new String[] {});
	}

@Test
	public void testAnyCJK() throws Exception
	{
		CJKSieveFilterFactory f = getCJKSieveFilterFactory("any_cjk");
		TokenStream stream = f.create(new StandardTokenizer(TEST_VERSION_CURRENT, new StringReader("壇君")));
		assertTokenStreamContents(stream, new String[] { "壇", "君" });
		stream = f.create(new StandardTokenizer(TEST_VERSION_CURRENT, new StringReader("マンガ is katakana")));
		assertTokenStreamContents(stream, new String[] { "マンガ", "is", "katakana" });
		stream = f.create(new StandardTokenizer(TEST_VERSION_CURRENT, new StringReader("한국경제 hangul only")));
		assertTokenStreamContents(stream, new String[] { "한국경제", "hangul", "only" });
		stream = f.create(new StandardTokenizer(TEST_VERSION_CURRENT, new StringReader("no cjk")));
		assertTokenStreamContents(stream, new String[] {});
	}

@Test
	public void testEmitArgBad() throws Exception
	{
		try
		{
			CJKSieveFilterFactory f = getCJKSieveFilterFactory("invalid");
			fail("no RuntimeException thrown when missing emitIf parameter");
		}
		catch (RuntimeException e)
		{
			assertEquals("Configuration Error: parameter 'emitIf' for CJKSieveFilterFactory must be one of:  japanese, hangul, han_solo, cj, any_cjk, no_cjk", e.getMessage());
		}
	}

@Test
	public void testEmitArgMissing() throws Exception
	{
		try
		{
			CJKSieveFilterFactory f = getCJKSieveFilterFactory(null);
			fail("no RuntimeException thrown when missing emitIf parameter");
		}
		catch (RuntimeException e)
		{
			assertEquals("Configuration Error: missing parameter 'emitIf' for CJKSieveFilterFactory (must be one of:  japanese, hangul, han_solo, cj, any_cjk, no_cjk", e.getMessage());
		}
	}

	private CJKSieveFilterFactory getCJKSieveFilterFactory(String emitIf) throws Exception
	{
		CJKSieveFilterFactory factory = new CJKSieveFilterFactory();
		Map<String,String> args = new HashMap<String,String>();
		args.put("emitIf", emitIf);
		factory.init(args);
		return factory;
	}
}
