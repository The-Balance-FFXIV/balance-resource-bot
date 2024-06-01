package gg.xp.resbot

import discord4j.common.util.Snowflake
import discord4j.discordjson.json.MessageCreateRequest
import discord4j.discordjson.json.MessageData
import discord4j.discordjson.json.MessageEditRequest
import discord4j.rest.entity.RestChannel
import discord4j.rest.entity.RestMessage
import groovy.transform.CompileStatic
import groovy.transform.TupleConstructor
import reactor.util.Logger
import reactor.util.Loggers

import java.time.Instant

@CompileStatic
@TupleConstructor(includeFields = true, defaults = false)
class ChannelController {
	private static final Logger log = Loggers.getLogger(ChannelController)
	private final Bot bot
	private final RestChannel channel

	List<MessageData> getActualChannelContents() {
		channel.getMessagesBefore(Snowflake.of(Instant.now()))
				.filter { it.author().id().asLong() == bot.ownUserId }
				.collectList()
				.block()
				.reversed()
	}

	RestMessage getMessage(long messageId) {
		return (bot.getMessage channel.id.asLong(), messageId)
	}

	MessageData editMessage(MessageData messageData, DesiredMarkdownMessage desiredMessage) {

		log.info "Editing message ${channel.id.asLong()}/${messageData.id().asLong()}"
		def msg = getMessage messageData.id().asLong()

		return msg.edit(MessageEditRequest.builder().with {
			contentOrNull desiredMessage.desiredContent.content()
			build()
		}).block()
	}

	MessageData postMessage(DesiredMarkdownMessage desiredMessage) {
		log.info "Posting message to ${channel.id.asLong()}"
		return channel.createMessage(MessageCreateRequest.builder().with {
			content desiredMessage.desiredContent.content()
			embeds([])
			build()
		}).block()
	}

	void deleteMessage(MessageData messageData) {
		log.info "Deleting message ${channel.id.asLong()}/${messageData.id().asLong()}"
		def msg = getMessage(messageData.id().asLong())
		msg.delete "No longer needed" block()
	}

	String getName() {
		return channel.getData().block().name().toOptional().orElse(channel.id.toString())
	}
}
