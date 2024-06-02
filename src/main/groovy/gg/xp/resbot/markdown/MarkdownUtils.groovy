package gg.xp.resbot.markdown

import groovy.transform.CompileStatic
import org.commonmark.parser.Parser
import org.commonmark.renderer.markdown.MarkdownRenderer

@CompileStatic
class MarkdownUtils {
	static final Parser parser = Parser.builder().with {
		build()
	}
	static final MarkdownRenderer renderer = MarkdownRenderer.builder().with {
		nodeRendererFactory(new DiscordMarkdownNodeRendererFactory())
		build()
	}
}
