package com.wapitia.common.csv;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;

public class FileUtilities {

	public static final String ISO_8859_1 = "ISO-8859_1";
	public static final String UTF_8 = "UTF-8";
	
//	public static String DEFAULT_CHAR_ENCODING = ISO_8859_1;
	public static String DEFAULT_CHAR_ENCODING = UTF_8;

	/**
	 * Return the contents of a named text file as a CharSequence, backed by a CharBuffer.
	 * 
	 * @param textFileName
	 * @return a CharSequence that treats the file as a Text file.
	 * @throws IOException
	 * 
	 * @see {@link CharBuffer}
	 * @see {@link #fromFile(FileInputStream)}
	 */
	public static CharSequence fromFile(String textFileName) throws IOException {

		FileInputStream fis = null;
		try {
			fis = new FileInputStream(textFileName);
			return fromFile(fis);
		} 
		catch (IOException e) {
			if (fis != null) {
				fis.close();
			}
			throw e;
		}
	}

	/**
	 * Return the contents of File Input Stream as a CharSequence, backed by a CharBuffer.
	 * This does not close the stream.
	 * 
	 * @param textFileName
	 * @return a CharSequence that treats the file as a Text file.
	 * @throws IOException
	 * 
	 * @see {@link CharBuffer}
	 * @see {@link #fromFile(String)}
	 */
	public static CharSequence fromFile(FileInputStream fis) throws IOException, CharacterCodingException {
		
		final FileChannel fc = fis.getChannel();
		final ByteBuffer bbuf = fc.map(FileChannel.MapMode.READ_ONLY, 0, (int) fc.size());
		final CharBuffer cbuf = Charset.forName(DEFAULT_CHAR_ENCODING).newDecoder().decode(bbuf);
		return cbuf;
	}

}
