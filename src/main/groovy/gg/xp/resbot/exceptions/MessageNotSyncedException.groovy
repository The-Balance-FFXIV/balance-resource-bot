package gg.xp.resbot.exceptions

import discord4j.discordjson.json.MessageData
import gg.xp.resbot.DesiredMessageContent
import groovy.transform.CompileStatic

@CompileStatic
class MessageNotSyncedException extends RuntimeException {

	final long channelId
	final long messageId
	final String expected
	final String actual
	final String action

	MessageNotSyncedException(long channelId, long messageId, String expected, String actual, String action) {
		super("Message ${channelId}/${messageId} was not as expected after ${action}. Expected:\n${expected}\n------\nActual:\n${actual}\n------\n")
		this.channelId = channelId
		this.messageId = messageId
		this.expected = expected
		this.actual = actual
		this.action = action
	}

	MessageNotSyncedException(DesiredMessageContent expected, MessageData actual, String action) {
		this(actual.channelId().asLong(), actual.id().asLong(), expected.content(), actual.content(), action)
	}


}
