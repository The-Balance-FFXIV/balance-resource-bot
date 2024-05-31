package gg.xp

import discord4j.common.util.Snowflake
import discord4j.discordjson.json.MessageData
import discord4j.discordjson.json.MessageEditRequest
import discord4j.rest.entity.RestChannel
import discord4j.rest.entity.RestMessage
import groovy.transform.CompileStatic
import groovy.transform.TupleConstructor

import java.time.Instant

@CompileStatic
@TupleConstructor(includeFields = true, defaults = false)
class ChannelController {
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

	void editMessage(MessageData messageData, DesiredMarkdownMessage desiredMessage) {

		def msg = getMessage messageData.id().asLong()

		msg.edit(MessageEditRequest.builder().with {
			contentOrNull desiredMessage.desiredContent.content()
			build()
		}).block()
	}

	MessageData postMessage(DesiredMarkdownMessage desiredMessage) {
		return channel.createMessage(desiredMessage.desiredContent.content()).block()
	}

	void deleteMessage(MessageData messageData) {
		def msg = getMessage(messageData.id().asLong())
		msg.delete "No longer needed" block()
	}

	String getName() {
		return channel.getData().block().name().toOptional().orElse(channel.id.toString())
	}
}
