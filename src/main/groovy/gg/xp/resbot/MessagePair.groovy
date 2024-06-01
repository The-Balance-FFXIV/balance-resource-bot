package gg.xp.resbot

import discord4j.core.object.entity.Message
import groovy.transform.CompileStatic
import groovy.transform.TupleConstructor
import reactor.util.annotation.Nullable

@CompileStatic
@TupleConstructor(includeFields = true)
class MessagePair {
	// TODO: ChannelContext class
	private final Bot bot
	private final ChannelController channel
	private @Nullable DesiredMarkdownMessage desired
	private @Nullable Message actual

	void reconcile() {
//		if (desired != null) {
//			if (actual != null) {
//				// Actual and desired are both present, check if contents equal
//				if (desired.content() != actual.content) {
//					// edit
//					bot.editMessage(actual, desired)
//				}
//			}
//			else {
//				// Desired present but actual is not, create
//				bot.postMessage(channel, desired)
//			}
//		}
//		else {
//			if (actual != null) {
//				// Actual present but desired is not, delete
//				bot.deleteMessage(channel, desired)
//			}
//
//		}
	}



}
