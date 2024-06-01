package gg.xp.resbot

import groovy.transform.CompileStatic
import reactor.util.Loggers

@CompileStatic
static void main(String[] args) {

	def log = Loggers.getLogger(Main)
	log.info "Starting"
	String token = System.getenv "DISCORD_TOKEN"

	if (token.empty) {
		log.error "Discord token not specified"
		System.exit 1
	}
	def bot = Bot.fromToken(token)
	bot.start()
	bot.runAll()

}