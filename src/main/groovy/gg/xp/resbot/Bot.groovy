package gg.xp.resbot

import discord4j.common.util.Snowflake
import discord4j.core.DiscordClient
import discord4j.core.GatewayDiscordClient
import discord4j.discordjson.json.MessageData
import discord4j.rest.entity.RestChannel
import discord4j.rest.entity.RestMessage
import gg.xp.resbot.util.FileUtils
import groovy.transform.CompileStatic
import reactor.util.Logger
import reactor.util.Loggers
import reactor.util.annotation.Nullable

import java.nio.file.Path
import java.util.concurrent.ConcurrentHashMap

@CompileStatic
class Bot {
	private static final Logger log = Loggers.getLogger Bot
	private final String token;
	private DiscordClient client
	private GatewayDiscordClient gatewayClient
	private long ownUserId
	private final Map<File, MessageData> fileMessageMap = new ConcurrentHashMap<>()

	private Bot(String token) {
		this.token = token;
	}

	static Bot fromToken(String token) {
		return new Bot(token)
	}

	void start() {
		DiscordClient client = DiscordClient.create token
		this.gatewayClient = client.login().block()
		this.ownUserId = client.getSelf().block().id().asLong()
		this.client = client
	}

	void stop() {
		gatewayClient?.logout()
		this.gatewayClient = null
		this.client = null
	}

	DiscordClient getClient() {
		return client
	}

	GatewayDiscordClient getGatewayClient() {
		return gatewayClient
	}

	void runAll() {

		def dataDir = new File('data')

		FileUtils.assertIsDir dataDir

		dataDir.listFiles().findAll { it.isDirectory() }.each {

			log.info "Starting directory ${it.name}"

			DesiredChannelContents desiredState = DesiredChannelContents.fromDir this, it

			// TODO: this does not resolve cross-channel links
			def cc = new ChannelController(this, getChannel(desiredState.channelId()))
			def sync = new ChannelSync(this, cc, desiredState)
			int attempts = 10
			SyncResult result;
			do {
				result = sync.sync()
			} while (result.pending && (--attempts > 0))
			if (result.pending) {
				throw new RuntimeException("Failed: channel '${it.name}' still has pending hyperlinks!")
			}
			log.info "Finished directory ${it.name}"
		}
	}

	RestChannel getChannel(long channelId) {
		return this.client.getChannelById(Snowflake.of(channelId))
	}

	RestMessage getMessage(long channelId, long messageId) {
		return this.client.getMessageById(Snowflake.of(channelId), Snowflake.of(messageId))
	}

	long getOwnUserId() {
		return ownUserId
	}

	LinkResolution resolveLink(File base, String rawLink) {
		try {
			// Keep real URLs intact
			return new LinkResolution(new URI(rawLink).toURL().toString(), false)
		}
		// If it isn't a real URL, resolve it as a relative file
		catch (Throwable ignored) {
			Path basePath = base.isDirectory() ? base.toPath() : base.toPath().parent
			def destFile = basePath.resolve(rawLink) toFile()

			if (destFile.isFile()) {
				def link = getFileLink(destFile)
				if (link != null) {
					return new LinkResolution(link, false)
				}
				return new LinkResolution(rawLink, true)
			}
			else {
				throw new IllegalStateException("Could not resolve link '${rawLink}' relative to '${base}")
			}
		}
	}

	@Nullable
	String getFileLink(File file) {
		MessageData msg = fileMessageMap[file]
		if (msg) {
			long channelId = msg.channelId().asLong()
			long msgId = msg.id().asLong()
			msg.guildId()
			long guildId = getChannel(channelId).data.block()
					.guildId().toOptional()
					.orElseThrow { throw new IllegalArgumentException("Message ${channelId}/${msgId} has no guild!") }.asLong()
			return "https://discord.com/channels/${guildId}/${channelId}/${msgId}"
		}
		return null
	}

	void setFileMapping(File file, MessageData message) {
		if (file != null) {
			this.fileMessageMap[file] = message
		}
	}
}
