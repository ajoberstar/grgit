package org.ajoberstar.grgit

import groovy.transform.Immutable

@Immutable
class BranchStatus {
	Branch branch

	int aheadCount

	int behindCount
}
