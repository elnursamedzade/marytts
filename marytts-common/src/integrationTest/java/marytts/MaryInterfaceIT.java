/**
 * Copyright 2011 DFKI GmbH.
 * All Rights Reserved.  Use is subject to license terms.
 *
 * This file is part of MARY TTS.
 *
 * MARY TTS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, version 3 of the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package marytts;

import java.util.Locale;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

import marytts.datatypes.MaryDataType;
import marytts.exceptions.SynthesisException;
import marytts.util.dom.DomUtils;

import org.w3c.dom.Document;

import org.testng.Assert;
import org.testng.annotations.*;

/**
 * @author marc
 *
 */
public class MaryInterfaceIT {
	MaryInterface mary;

	@BeforeMethod
	public void setUp() throws Exception {
		mary = new LocalMaryInterface();
	}

	@Test
	public void canGetMaryInterface() throws Exception {
		Assert.assertNotNull(mary);
		Assert.assertEquals(MaryDataType.TEXT.name(), mary.getInputType());
		Assert.assertEquals(MaryDataType.AUDIO.name(), mary.getOutputType());
		Assert.assertEquals(Locale.US, mary.getLocale());
	}

	@Test
	public void canSetInputType() throws Exception {
		MaryDataType in = MaryDataType.RAWMARYXML;
		Assert.assertTrue(!in.name().equals(mary.getInputType()));
		mary.setInputType(in.name());
		Assert.assertEquals(in.name(), mary.getInputType());
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void unknownInputType() throws Exception {
		mary.setInputType("something strange");
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void nullInputType() throws Exception {
		mary.setInputType(null);
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void notAnInputType() throws Exception {
		mary.setInputType(MaryDataType.AUDIO.name());
	}

	@Test
	public void canSetOutputType() throws Exception {
		MaryDataType out = MaryDataType.TOKENS;
		Assert.assertTrue(!out.equals(mary.getOutputType()));
		mary.setOutputType(out.name());
		Assert.assertEquals(out.name(), mary.getOutputType());
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void unknownOutputType() throws Exception {
		mary.setOutputType("something strange");
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void nullOutputType() throws Exception {
		mary.setOutputType(null);
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void notAnOutputType() throws Exception {
		mary.setOutputType(MaryDataType.TEXT.name());
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void cannotSetUnsupportedLocale() throws Exception {
		Locale loc = new Locale("abcde");
		mary.setLocale(loc);
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void cannotSetNullLocale() throws Exception {
		mary.setLocale(null);
	}

	@Test
	public void canProcessToTokens() throws Exception {
		// setup
		mary.setOutputType(MaryDataType.TOKENS.name());
		// exercise
		Document tokens = mary.generateXML("Hello world");
		// verify
		Assert.assertNotNull(tokens);
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void nullAudioFileFormat() throws Exception {
		mary.generateAudio("some text");
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void refuseWrongInput1() throws Exception {
		// setup
		mary.setInputType(MaryDataType.RAWMARYXML.name());
		// method with string arg does not match declared input type:
		mary.generateXML("some text");
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void refuseWrongOutput1() throws Exception {
		// requesting xml output but set to default output type AUDIO:
		mary.generateXML("some text");
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void refuseWrongOutput2() throws Exception {
		// setup
		mary.setOutputType(MaryDataType.TOKENS.name());
		// requesting audio putput but set to XML output type:
		mary.generateAudio("some text");
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void refuseWrongOutput3() throws Exception {
		// setup
		mary.setOutputType(MaryDataType.TOKENS.name());
		// requesting text putput but set to XML output type:
		mary.generateText("some text");
	}

}
