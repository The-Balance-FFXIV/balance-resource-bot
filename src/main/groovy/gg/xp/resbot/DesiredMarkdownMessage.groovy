package gg.xp.resbot

import groovy.transform.CompileStatic
import groovy.transform.TupleConstructor
import org.commonmark.node.AbstractVisitor
import org.commonmark.node.Document
import org.commonmark.node.Link
import org.commonmark.parser.Parser
import org.commonmark.renderer.markdown.MarkdownRenderer

@CompileStatic
@TupleConstructor(includeFields = true, defaults = false)
abstract class DesiredMarkdownMessage {

	protected final Bot bot

	protected abstract Document getDocument()

	protected abstract File getBaseFile()

	protected abstract String getTitle()

	protected abstract boolean resolveLinks()

	protected static Document parse(String fileContents) {
		def parser = Parser.builder().build()
		return (Document) parser.parse(fileContents)
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
		def renderer = MarkdownRenderer.builder().with {
			nodeRendererFactory(new DiscordMarkdownNodeRendererFactory())
			build()
		}

		def rendered = renderer.render(doc).stripTrailing()
		return new DesiredMessageContent(rendered, pending)
	}
}