package edu.stanford.solr.analysis;

import java.io.Reader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import org.apache.lucene.analysis.*;
import org.apache.lucene.analysis.standard.StandardTokenizer;

import org.junit.Test;

/**
 * @author Naomi Dushay
 *
 */
public class TestCJKHopperFilterFactory extends BaseTokenStreamTestCase
{
    static {
    	System.setProperty("tests.asserts.gracious", "true");
    }

@Test
    public void testJapanese() throws Exception
	{
		Reader reader = new StringReader("マンガ is katakana");
		TokenStream stream = getCJKHopperFilterFactory("japanese").create(new StandardTokenizer(TEST_VERSION_CURRENT, reader));
		assertTokenStreamContents(stream, new String[] { "マンガ", "is", "katakana" });
	}

@Test
	public void testHanSolo() throws Exception
	{
		Reader reader = new StringReader("壇君");
		TokenStream stream = getCJKHopperFilterFactory("han_solo").create(new StandardTokenizer(TEST_VERSION_CURRENT, reader));
		assertTokenStreamContents(stream,
		    new String[] { "壇", "君" });
	}

@Test
	public void testHangul() throws Exception
	{
		Reader reader = new StringReader("한국경제 hangul only");
		TokenStream stream = getCJKHopperFilterFactory("hangul").create(new StandardTokenizer(TEST_VERSION_CURRENT, reader));
		assertTokenStreamContents(stream, new String[] { "한국경제", "hangul", "only" });

	}

@Test
	public void testNoCJK() throws Exception
	{
		Reader reader = new StringReader("no cjk");
		TokenStream stream = getCJKHopperFilterFactory("no_cjk").create(new StandardTokenizer(TEST_VERSION_CURRENT, reader));
		assertTokenStreamContents(stream,
		    new String[] { "no", "cjk" });

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
