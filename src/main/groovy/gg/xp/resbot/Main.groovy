package gg.xp.resbot

import groovy.transform.CompileStatic
import reactor.util.Loggers

@CompileStatic
class Main {
	static void main(String[] args) {
		def log = Loggers.getLogger(Main)
		log.info 'Starting'
		def bot = Bot.build {
			tokenFromEnv()
			dataDir('./data')
		}
		bot.start()
		bot.runAll()
		bot.stop()
	}

}