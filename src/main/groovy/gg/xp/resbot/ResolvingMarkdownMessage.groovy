package gg.xp.resbot

import groovy.transform.CompileStatic
import org.commonmark.node.AbstractVisitor
import org.commonmark.node.Document
import org.commonmark.node.Link

@CompileStatic
abstract class ResolvingMarkdownMessage extends DesiredMarkdownMessage {
	ResolvingMarkdownMessage(Bot bot) {
		super(bot)
	}

	protected abstract File getBaseFile()

	protected abstract Document getDocument()

	@SuppressWarnings('GrMethodMayBeStatic')
	protected boolean resolveLinks() {
		return true
	}

	DesiredMessageContent getDesiredContent() {
		boolean pending = false
		def doc = document
		if (resolveLinks()) {
			doc.accept(new AbstractVisitor() {
				@Override
				void visit(Link link) {
					def resolved = bot.resolveLink(baseFile, link.destination)
					if (resolved.pending()) {
						pending = true
					}
					link.destination = "${resolved.value()}"
				}
			})
		}

		def rendered = renderer.render(doc).stripTrailing()
		return new DesiredMessageContent(rendered, pending)
	}
}
