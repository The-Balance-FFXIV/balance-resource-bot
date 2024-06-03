package gg.xp.resbot

import groovy.transform.CompileStatic

@CompileStatic
class ChannelOpsStats {
	int noop
	int pending
	int edit
	int create
	int delete
	int notSynced

	String format() {
		return "Noop: ${noop}; Pending: ${pending}; Edit: ${edit}; Create: ${create}; Delete: ${delete}, Not Synced: ${notSynced}"
	}
}
