package com.ohhoonim.para;

public record Page(
		Integer totalCount,
		Integer limit,
		Object lastSeenKey) {
	public Page {
		if (limit == null || limit.equals(0)) {
			limit = 10;
		}
	}

	public Page() {
		this(null, null, null);
	}

	public Page(Object lastSeenKey) {
		this(null, 20, lastSeenKey);
	}

}