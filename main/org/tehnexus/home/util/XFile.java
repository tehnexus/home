package org.tehnexus.home.util;

import java.util.Arrays;

public class XFile {

	public XFile() {

	}

	public enum Type {
		PDF(new byte[] { 0x25, 0x50, 0x44, 0x46 }),
		PNG(new byte[] { (byte) 0x89, 0x50, 0x4e, 0x47 }),
		JPEG(new byte[] { (byte) 0xff, (byte) 0xd8, (byte) 0xff, (byte) 0xdb }),
		JFIF(new byte[] { (byte) 0xff, (byte) 0xd8, (byte) 0xff, (byte) 0xe0, 0x00, 0x10, 0x4a, 0x46, 0x49, 0x46, 0x00,
				0x01 }),
		EXIF(new byte[] { (byte) 0xff, (byte) 0xd8, (byte) 0xff, (byte) 0xe1, 0x0d, 0x15, 0x45, 0x78, 0x69, 0x66, 0x00,
				0x00 }),

		UNKNOWN(new byte[] {});

		private byte[] headers;

		Type(byte[] headers) {
			this.headers = headers;
		}

		public byte[] getHeaders() {
			return headers;
		}

		public static Type getType(byte[] byteArray) {
			for (Type type : Type.values()) { // loop types
				byte[] typeHead = type.getHeaders();
				byte[] typeFile = Arrays.copyOfRange(byteArray, 0, typeHead.length);
				if (Arrays.equals(typeFile, typeHead))
					return type;
			}
			return UNKNOWN;
		}

	}

}
