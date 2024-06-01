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

	protected abstract String getTitle()

	abstract DesiredMessageContent getDesiredContent()

	protected MarkdownRenderer getRenderer() {
		return bot.renderer
	}
}