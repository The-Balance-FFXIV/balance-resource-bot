package gg.xp.resbot

import gg.xp.resbot.util.FileUtils
import groovy.transform.CompileStatic

@CompileStatic
class BotBuilder {

	private String token
	private final List<File> baseDataDirs = []

	void token(String token) {
		this.token = token
	}

	void tokenFromEnv(String env) {
		String tokenFromEnv = System.getenv env
		if (tokenFromEnv == null || tokenFromEnv.empty) {
			throw new IllegalStateException("Specify discord token in ${env} env var")
		}
		token(tokenFromEnv)
	}

	void tokenFromEnv() {
		tokenFromEnv "DISCORD_TOKEN"
	}

	void dataDir(File file) {
		FileUtils.assertIsDir file
		baseDataDirs.add file
	}

	void dataDir(String path) {
		dataDir(path as File)
	}

	Bot build() {
		if (this.token == null || this.token.empty) {
			throw new IllegalArgumentException("Discord token not specified")
		}
		if (this.baseDataDirs.empty) {
			throw new IllegalArgumentException("No data directories specified")
		}
		return new Bot(token, baseDataDirs)
	}
}
