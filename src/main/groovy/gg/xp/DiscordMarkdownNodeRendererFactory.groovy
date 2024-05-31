package gg.xp

import groovy.transform.CompileStatic
import org.commonmark.renderer.NodeRenderer
import org.commonmark.renderer.markdown.MarkdownNodeRendererContext
import org.commonmark.renderer.markdown.MarkdownNodeRendererFactory

@CompileStatic
class DiscordMarkdownNodeRendererFactory implements MarkdownNodeRendererFactory {

	@Override
	NodeRenderer create(MarkdownNodeRendererContext markdownNodeRendererContext) {
		return new DiscordMarkdownNodeRenderer(markdownNodeRendererContext)
	}

	@Override
	Set<Character> getSpecialCharacters() {
		return Collections.emptySet()
	}

}
