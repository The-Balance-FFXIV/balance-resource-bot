package gg.xp.resbot

import gg.xp.resbot.util.FileUtils
import groovy.transform.CompileStatic
import groovy.transform.ImmutableOptions

@CompileStatic
@ImmutableOptions(knownImmutableClasses = ChannelSettings)
record DesiredChannelContents(
		long channelId,
		List<DesiredMarkdownMessage> desiredMessages,
		ChannelSettings channelSettings
) {
	static DesiredChannelContents fromDir(Bot bot, File file) {
		FileUtils.assertIsDir file
		def props = new Properties()
		def propsFile = file.toPath().resolve("channel.properties").toFile()
		if (!propsFile.isFile()) {
			throw new IllegalArgumentException("File '${propsFile}' does not exist or is not a file")
		}
		props.load propsFile.newReader()
		long channelId = props['channel_id'] as long

		def files = file.listFiles({ File f ->
			return f.name.toLowerCase(Locale.ROOT) ==~ FileBasedMarkdownMessage.fileFilter
		} as FileFilter)

		List<FileBasedMarkdownMessage> desiredFiles = files.collect { new FileBasedMarkdownMessage(bot, it) }
				.sort { it.sortOrder }
		List<DesiredMarkdownMessage> desired = []
		desired.addAll(desiredFiles)

		ChannelSettings settings = ChannelSettings.fromProps(props)

		if (settings.tocEnabled) {

			def toc = new TableOfContents(bot, desiredFiles.toList(), file)
			if (settings.tocTop()) {
				desired.add(0, toc)
			}
			if (settings.tocBottom()) {
				desired.add(toc)
			}
		}

		return new DesiredChannelContents(channelId, desired, settings)

	}
}