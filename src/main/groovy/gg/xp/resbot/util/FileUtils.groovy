package gg.xp.resbot.util

import groovy.transform.CompileStatic

@CompileStatic
final class FileUtils {

	private FileUtils() {}

	static void assertIsFile(File file) {
		if (!file.isFile()) {
			throw new IllegalArgumentException("File '${file}' does not exist or is not a normal file")
		}
	}

	static void assertIsDir(File file) {
		if (!file.isDirectory()) {
			throw new IllegalArgumentException("File '${file}' does not exist or is not a directory")
		}
	}
}
