package example.test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Taken from
 * http://stackoverflow.com/questions/10572398/how-can-i-easily-compress
 * -and-decompress-strings-to-from-byte-arrays?lq=1
 */
public abstract class StringCompressor {

	public static byte[] compress(String text) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			OutputStream out = new GZIPOutputStream(baos);
			out.write(text.getBytes("UTF-8"));
			out.close();
		} catch (IOException e) {
			throw new AssertionError(e);
		}
		return baos.toByteArray();
	}

	public static String decompress(byte[] bytes) {
		try {
			InputStream in = new GZIPInputStream(new ByteArrayInputStream(bytes));
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			byte[] buffer = new byte[8192];
			int len;
			while ((len = in.read(buffer)) > 0)
				baos.write(buffer, 0, len);
			return new String(baos.toByteArray(), "UTF-8");
		} catch (IOException e) {
			throw new AssertionError(e);
		}
	}
}