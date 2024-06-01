package gg.xp.resbot

import groovy.transform.CompileStatic
import groovy.transform.TupleConstructor
import org.commonmark.node.Heading
import org.commonmark.node.Link
import org.commonmark.node.Node
import org.commonmark.node.Text
import org.commonmark.renderer.NodeRenderer
import org.commonmark.renderer.markdown.MarkdownNodeRendererContext
import org.commonmark.renderer.markdown.MarkdownWriter
import org.commonmark.text.AsciiMatcher
import org.commonmark.text.CharMatcher

@CompileStatic
@TupleConstructor(includeFields = true, defaults = false)
class DiscordMarkdownNodeRenderer implements NodeRenderer {

	private static final CharMatcher linkDestinationEscapeInAngleBrackets = AsciiMatcher.builder().with {
		c '<' as char
		c '>' as char
		c '\n' as char
		c '\\' as char
		build()
	}
	private final CharMatcher linkTitleEscapeInQuotes = AsciiMatcher.builder().with {
		c '"' as char
		c '\n' as char
		c '\\' as char
		build()
	}
	private final AsciiMatcher textEscape = AsciiMatcher.builder().anyOf("[]`*_&\n\\").anyOf(context.getSpecialCharacters()).build();
	private final AsciiMatcher textEscapeInHeading = AsciiMatcher.builder(textEscape).anyOf("#").build();

	private final MarkdownNodeRendererContext context;

	@Override
	Set<Class<? extends Node>> getNodeTypes() {
		return [Link, Text] as Set<Class<? extends Node>>
	}

	@Override
	void render(Node node) {
		if (node instanceof Link) {
			MarkdownWriter writer = context.writer

			writer.with {
				raw('[');
				visitChildren(node);
				raw(']');
				raw('(');
				raw('<');
				text(node.destination, linkDestinationEscapeInAngleBrackets);
				raw('>');

				String title = node.title
				if (title != null) {
					raw(' ');
					raw('"');
					text(title, linkTitleEscapeInQuotes);
					raw('"');
				}
				raw(')');
			}
		}
		else if (node instanceof Text) {
			if (node.parent instanceof Link) {
				context.writer.text node.literal, linkTitleEscapeInQuotes
			}
			else if (node.parent instanceof Heading) {
				context.writer.text node.literal, textEscapeInHeading
			}
			else {
				context.writer.text node.literal, textEscape
			}
		}
	}

	private void visitChildren(Node parent) {
		Node next
		for (Node node = parent.getFirstChild(); node != null; node = next) {
			next = node.getNext();
			this.context.render(node);
		}
	}
}
