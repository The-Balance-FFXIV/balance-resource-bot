package gg.xp.resbot


import groovy.transform.CompileStatic
import org.commonmark.node.Document
import reactor.util.Logger
import reactor.util.Loggers
import reactor.util.annotation.Nullable

import java.util.regex.Pattern

@CompileStatic
class FileBasedMarkdownMessage extends DesiredMarkdownMessage {

	private static final Logger log = Loggers.getLogger FileBasedMarkdownMessage
	public static Pattern fileFilter = ~/(\d+)_([^.]+)\.md/

	private final @Nullable
	File file
	final int sortOrder
	final String title

	FileBasedMarkdownMessage(Bot bot, File file) {
		super(bot)
		this.file = file
		def match = file.name =~ fileFilter
		if (match.matches()) {
			this.sortOrder = match.group(1) as int
			this.title = match.group(2).replaceAll("_", " ")
		}
		else {
			log.error "Failed to parse filename ${file.name}"
			this.sortOrder = 999_999
			this.title = file.name
		}
	}

	@Override
	protected Document getDocument() {
		return parse(file.text)
	}

	@Override
	protected File getBaseFile() {
		return file
	}

	@Nullable
	File getFile() {
		return file
	}

	@Override
	protected boolean resolveLinks() {
		return true
	}
}
