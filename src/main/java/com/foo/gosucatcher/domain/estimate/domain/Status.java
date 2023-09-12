package com.foo.gosucatcher.domain.estimate.domain;

import java.util.Arrays;

public enum Status {
	PENDING(1),
	PROCEEDING(2),
	FINISHED(3);

	private final int sequence;

	Status(int sequence) {
		this.sequence = sequence;
	}

	public static Status findNextStatus(Status status) {
		if (status.sequence == 3) {
			return status;
		}

		int nextSequence = status.sequence + 1;

		return Arrays.stream(values())
			.filter(s -> s.sequence == nextSequence)
			.findAny()
			.orElse(status);
	}
}
