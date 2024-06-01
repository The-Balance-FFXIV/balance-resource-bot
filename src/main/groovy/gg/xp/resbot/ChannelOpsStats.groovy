package gg.xp.resbot

class ChannelOpsStats {
	int noop
	int pending
	int edit
	int create
	int delete

	String format() {
		return "Noop: ${noop}; Pending: ${pending}; Edit: ${edit}; Create: ${create}; Delete: ${delete}"
	}
}
