package edu.stanford.solr.analysis;

import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import org.apache.lucene.analysis.*;
import org.apache.lucene.analysis.standard.StandardTokenizer;

import org.junit.Test;

/**
 * Simple tests for CJKHopperFilterFactory.
 *
 * @author Naomi Dushay
 */
public class TestCJKHopperFilterFactory extends BaseTokenStreamTestCase
{
    static {
    	System.setProperty("tests.asserts.gracious", "true");
    }

@Test
    public void testJapanese() throws Exception
	{
		CJKHopperFilterFactory f = getCJKHopperFilterFactory("japanese");
		TokenStream stream = f.create(new StandardTokenizer(TEST_VERSION_CURRENT, new StringReader("マンガ is katakana")));
		assertTokenStreamContents(stream, new String[] { "マンガ", "is", "katakana" });
		stream = f.create(new StandardTokenizer(TEST_VERSION_CURRENT, new StringReader("한국경제 hangul only")));
		assertTokenStreamContents(stream, new String[] {});
	}

@Test
	public void testHanSolo() throws Exception
	{
		CJKHopperFilterFactory f = getCJKHopperFilterFactory("han_solo");
		TokenStream stream = f.create(new StandardTokenizer(TEST_VERSION_CURRENT, new StringReader("壇君")));
		assertTokenStreamContents(stream, new String[] { "壇", "君" });
		stream = f.create(new StandardTokenizer(TEST_VERSION_CURRENT, new StringReader("한국경제 hangul only")));
		assertTokenStreamContents(stream, new String[] {});
	}

@Test
	public void testHangul() throws Exception
	{
		CJKHopperFilterFactory f = getCJKHopperFilterFactory("hangul");
		TokenStream stream = f.create(new StandardTokenizer(TEST_VERSION_CURRENT, new StringReader("한국경제 hangul only")));
		assertTokenStreamContents(stream, new String[] { "한국경제", "hangul", "only" });
		stream = f.create(new StandardTokenizer(TEST_VERSION_CURRENT, new StringReader("マンガ is katakana")));
		assertTokenStreamContents(stream, new String[] {});
	}

@Test
	public void testNoCJK() throws Exception
	{
		CJKHopperFilterFactory f = getCJKHopperFilterFactory("no_cjk");
		TokenStream stream = f.create(new StandardTokenizer(TEST_VERSION_CURRENT, new StringReader("no cjk")));
		assertTokenStreamContents(stream, new String[] { "no", "cjk" });
		stream = f.create(new StandardTokenizer(TEST_VERSION_CURRENT, new StringReader("マンガ is katakana")));
		assertTokenStreamContents(stream, new String[] {});
	}

@Test
	public void testEmitArgBad() throws Exception
	{
		try
		{
			CJKHopperFilterFactory f = getCJKHopperFilterFactory("invalid");
			fail("no RuntimeException thrown when missing emitIf parameter");
		}
		catch (RuntimeException e)
		{
			assertEquals("Configuration Error: parameter 'emitIf' for CJKHopperFilterFactory must be one of:  japanese, hangul, han_solo, no_cjk", e.getMessage());
		}
	}

@Test
	public void testEmitArgMissing() throws Exception
	{
		try
		{
			CJKHopperFilterFactory f = getCJKHopperFilterFactory(null);
			fail("no RuntimeException thrown when missing emitIf parameter");
		}
		catch (RuntimeException e)
		{
			assertEquals("Configuration Error: missing parameter 'emitIf' for CJKHopperFilterFactory (must be one of:  japanese, hangul, han_solo, no_cjk", e.getMessage());
		}
	}

	private CJKHopperFilterFactory getCJKHopperFilterFactory(String emitIf) throws Exception
	{
		CJKHopperFilterFactory factory = new CJKHopperFilterFactory();
		Map<String,String> args = new HashMap<String,String>();
		args.put("emitIf", emitIf);
		factory.init(args);
		return factory;
	}
}
