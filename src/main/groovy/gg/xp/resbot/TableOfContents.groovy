package gg.xp.resbot

import groovy.transform.CompileStatic
import org.commonmark.node.*
import org.jetbrains.annotations.Nullable

@CompileStatic
class TableOfContents extends DesiredMarkdownMessage {

	private final List<? extends FileBasedMarkdownMessage> contents
	// TODO: remove baseFile from DesiredMarkdownMessage. Make a ResolvingMarkdownMessage or something
	private final File baseFile

	TableOfContents(Bot bot, List<? extends FileBasedMarkdownMessage> contents, File baseFile) {
		super(bot)
		this.contents = contents
		this.baseFile = baseFile
	}

	@Override
	protected boolean resolveLinks() {
		return false
	}

	@Override
	protected Document getDocument() {
		Document doc = new Document()
		Heading heading = new Heading()
		heading.level = 3
		heading.appendChild(new Text(title))
		doc.appendChild(heading)

		def ol = new OrderedList()
		// Cut the extra empty lines between each list item
		ol.tight = true
		contents.each {
			String title = it.title
			def li = new ListItem()
			def p = new Paragraph()

			Text text = new Text(title)

			@Nullable String destMaybe = bot.getFileLink(it.file)
			if (destMaybe != null) {
				Link link = new Link()
				link.destination = destMaybe
				link.appendChild(text)
				p.appendChild(link)
			}
			else {
				p.appendChild(text)
			}
			li.appendChild(p)
			ol.appendChild(li)
		}
		doc.appendChild(ol)
		return doc
	}

	@Override
	protected File getBaseFile() {
		return baseFile
	}

	@Override
	protected String getTitle() {
		return "Table of Contents"
	}
}
