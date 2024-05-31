package gg.xp

import groovy.transform.CompileStatic

@CompileStatic
record DesiredChannelContents(
		long channelId,
		List<DesiredMarkdownMessage> desiredMessages
) {

	static DesiredChannelContents fromDir(Bot bot, File file) {
		if (file.isDirectory()) {
			def props = new Properties()
			def propsFile = file.toPath().resolve("channel.properties").toFile()
			if (!propsFile.isFile()) {
				throw new IllegalArgumentException("File '${propsFile}' does not exist or is not a file")
			}
			props.load propsFile.newReader()
			long channelId = props['channel_id'] as long

			def files = file.listFiles ({ File f ->
				return f.name.toLowerCase(Locale.ROOT) ==~ /\d+_[^.]+\.(md|txt)/
			} as FileFilter)

			def desired = files.collect { DesiredMarkdownMessage.fromFile bot, it }

			return new DesiredChannelContents(channelId, desired)

		} else {
			throw new IllegalArgumentException("File '${file}' does not exist or is not a directory")
		}
	}

}