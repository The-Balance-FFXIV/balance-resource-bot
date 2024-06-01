package gg.xp.resbot

import groovy.transform.CompileStatic

@CompileStatic
record ChannelSettings(
		boolean tocTop,
		boolean tocBottom
) {

	static ChannelSettings fromProps(Properties properties) {
		return new ChannelSettings(
				Boolean.parseBoolean(properties.getProperty("table_of_contents_top", "false")),
				Boolean.parseBoolean(properties.getProperty("table_of_contents_bottom", "false"))
		)
	}

	boolean isTocEnabled() {
		return tocTop || tocBottom
	}

}