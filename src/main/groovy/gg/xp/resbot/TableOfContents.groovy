package gg.xp.resbot

import groovy.transform.CompileStatic
import org.commonmark.node.*
import org.jetbrains.annotations.Nullable

@CompileStatic
class TableOfContents extends DesiredMarkdownMessage {

	private final List<? extends FileBasedMarkdownMessage> contents

	TableOfContents(Bot bot, List<? extends FileBasedMarkdownMessage> contents) {
		super(bot)
		this.contents = contents
	}

	@Override
	protected String getTitle() {
		return "Table of Contents"
	}

	@Override
	DesiredMessageContent getDesiredContent() {
		Document doc = new Document()
		boolean pending = false
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
				pending = true
				p.appendChild(text)
			}
			li.appendChild(p)
			ol.appendChild(li)
		}
		doc.appendChild(ol)

		def rendered = renderer.render(doc).stripTrailing()

		return new DesiredMessageContent(rendered, pending)
	}
}
