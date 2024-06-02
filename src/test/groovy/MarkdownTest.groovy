import gg.xp.resbot.markdown.MarkdownUtils
import groovy.transform.CompileStatic
import org.commonmark.parser.Parser
import org.commonmark.renderer.markdown.MarkdownRenderer
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

@CompileStatic
class MarkdownTest {

	private static final MarkdownRenderer renderer = MarkdownUtils.renderer
	private static final Parser parser = MarkdownUtils.parser

	private static String parseAndRender(String input) {
		def parsed = parser.parse input
		return renderer.render(parsed)
	}

	@Test
	void testBasicText() {
		def input = "This is standard text\n"
		def result = parseAndRender input
		Assertions.assertEquals input, result
	}

	@Test
	void testBasicFormatting() {
		def input = "This text has _italicized text_, __underlined text__, **bold text**\n"
		def result = parseAndRender input
		Assertions.assertEquals input, result
	}

	@Test
	void testMoreFormatting() {
		def input = "This text has *italicized text*, ***bold+italic text***, ~~strikethrough text~~\n"
		def result = parseAndRender input
		Assertions.assertEquals input, result
	}

	@Test
	void testEscapes() {
		def input = "This text has _italicized\\_text_, __underlined text__, **bold text**, escape \\_ outside of formatting\n"
		def result = parseAndRender input
		Assertions.assertEquals input, result
	}

	@Test
	void testNoEscapeInLink() {
		def input = "Link contains underscore https://xivapi.com/i/013000/013528_hr1.png and text after and another link http://foo\n"
		def result = parseAndRender input
		Assertions.assertEquals input, result
	}

	@Test
	void testNonEmbeddingLink() {
		def input = "Link contains underscore <https://xivapi.com/i/013000/013528_hr1.png> and text after and another link http://foo\n"
		def result = parseAndRender input
		Assertions.assertEquals input, result
	}

	@Test
	void codeBlock() {
		def input = "This text has `_italicized\\_text_, __underlined text__, **bold text**, escape \\_ outside of formatting` in a code block\n"
		def result = parseAndRender input
		Assertions.assertEquals input, result
	}
}
