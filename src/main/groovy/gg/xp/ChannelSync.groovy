package gg.xp

import discord4j.discordjson.json.MessageData
import groovy.transform.CompileStatic
import groovy.transform.TupleConstructor
import reactor.util.Logger
import reactor.util.Loggers

@CompileStatic
@TupleConstructor(includeFields = true, defaults = false)
class ChannelSync {
	private static final Logger log = Loggers.getLogger(ChannelSync)
	private final Bot bot
	private final ChannelController channel
	private final DesiredChannelContents desiredState

	SyncResult sync() {

		log.info "Beginning channel sync for ${channel.name}"
		ChannelOpsStats stats = new ChannelOpsStats()
		List<DesiredMarkdownMessage> desired = desiredState.desiredMessages()
		List<MessageData> actual = channel.getActualChannelContents()

		int desiredCount = desired.size()
		int actualCount = actual.size()

		// If count is the same, edit messages in-place
		// If desired > count, edit the first (count) messages, then add the rest
		// If desired < count, edit the first (desired) messages, then delete the rest
		int editCount = Math.min(desiredCount, actualCount)

		for (i in 0..<editCount) {

			def desiredMsg = desired[i]
			def actualMsg = actual[i]
			// Track the file for linking purposes
			bot.setFileMapping(desiredMsg.file, actualMsg)
			DesiredMessageContent desiredContent = desiredMsg.desiredContent

			if (desiredContent.pending()) {
				stats.pending++
			}
			else if (desiredContent.content() != actualMsg.content()) {
				channel.editMessage(actualMsg, desiredMsg)
				stats.edit++
				// TODO: validate that the desired == actual now
			}
			else {
				stats.noop++
			}
		}
		if (desiredCount > actualCount) {
			for (i in actualCount..<desiredCount) {
				def desiredMsg = desired[i]
				// TODO: validate that the desired == actual now
				def newMsg = channel.postMessage(desiredMsg)
				stats.create++
				bot.setFileMapping(desiredMsg.file, newMsg)
			}
		}
		else if (desiredCount < actualCount) {
			for (i in desiredCount..<actualCount) {
				channel.deleteMessage(actual[i])
				stats.delete++
			}
		}

		log.info "Channel ${channel.name} synced"
		log.info "${stats.format()}"
		log.info "Desired #: ${desiredCount}, Actual #: ${actualCount}"
		return new SyncResult(stats)
	}

}
