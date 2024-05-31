package gg.xp

import discord4j.common.util.Snowflake
import discord4j.core.DiscordClient
import discord4j.core.GatewayDiscordClient
import discord4j.discordjson.json.MessageData
import discord4j.discordjson.json.MessageEditRequest
import groovy.transform.CompileStatic
import reactor.util.Logger
import reactor.util.Loggers

import java.time.Instant

@CompileStatic
class Bot {
	private static final Logger log = Loggers.getLogger Bot
	private final String token;
	private DiscordClient client
	private GatewayDiscordClient gatewayClient
	private long ownUserId

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

		if (!dataDir.isDirectory()) {
			throw new IllegalArgumentException("File '${dataDir}' does not exist or is not a directory")
		}

		dataDir.listFiles().findAll { it.isDirectory() }.each {

			log.info "Starting directory ${it.name}"

			DesiredChannelContents desired = DesiredChannelContents.fromDir it
			List<MessageData> actual = getActualChannelContents desired.channelId()

			int desiredCount = desired.desiredMessages().size()
			int actualCount = actual.size()

			// If count is the same, edit messages in-place
			// If desired > count, edit the first (count) messages, then add the rest
			// If desired < count, edit the first (desired) messages, then delete the rest
			int editCount = Math.min(desiredCount, actualCount)
			for (i in 0..<editCount) {
				def desiredMsg = desired.desiredMessages()[i]
				def actualMsg = actual[i]
				if (desiredMsg.content() != actualMsg.content()) {
					editMessage(actualMsg, desiredMsg)
				}
			}

			log.info "Desired #: ${desiredCount}, Actual #: ${actualCount}"
			log.info "Finished directory ${it.name}"

		}
	}

	List<MessageData> getActualChannelContents(long channelId) {
		def channel = this.client.getChannelById(Snowflake.of(channelId))
		channel.getMessagesBefore(Snowflake.of(Instant.now()))
				.filter { it.author().id().asLong() == this.ownUserId }
				.collectList()
				.block()
				.reversed()
	}

	void editMessage(MessageData messageData, DesiredMessage desiredMessage) {

		def msg = this.client.getMessageById(Snowflake.of(messageData.channelId()), Snowflake.of(messageData.id()))

		msg.edit(MessageEditRequest.builder().with {

			contentOrNull desiredMessage.content()
			build()

		}).block()
	}
}
