package gg.xp

import groovy.transform.CompileStatic

@CompileStatic
record DesiredMessage(String content) {

	static DesiredMessage fromFile(File file) {
		if (file.isFile()) {
			return new DesiredMessage(file.text)
		}
		else {
			throw new IllegalArgumentException("File '${file}' does not exist or is not a normal file")
		}
	}

}