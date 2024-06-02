package org.commonmark.internal.inline;

/**
 * Nullifies the class of the same package/name in CommonMark
 */
public class AutolinkInlineParser implements InlineContentParser {

	@Override
	public ParsedInline tryParse(InlineParserState inlineParserState) {
		return ParsedInline.none();
	}
}
