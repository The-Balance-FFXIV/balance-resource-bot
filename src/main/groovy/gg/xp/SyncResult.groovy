package gg.xp

import groovy.transform.TupleConstructor


@TupleConstructor(defaults = false)
class SyncResult {
	final ChannelOpsStats stats

	boolean isPending() {
		return stats.pending > 0
	}
}