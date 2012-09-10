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
	public void testNoCJK() throws Exception
	{
		CJKSieveFilterFactory f = getCJKSieveFilterFactory("no_cjk");
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
			CJKSieveFilterFactory f = getCJKSieveFilterFactory("invalid");
			fail("no RuntimeException thrown when missing emitIf parameter");
		}
		catch (RuntimeException e)
		{
			assertEquals("Configuration Error: parameter 'emitIf' for CJKSieveFilterFactory must be one of:  japanese, hangul, han_solo, no_cjk", e.getMessage());
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
			assertEquals("Configuration Error: missing parameter 'emitIf' for CJKSieveFilterFactory (must be one of:  japanese, hangul, han_solo, no_cjk", e.getMessage());
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
