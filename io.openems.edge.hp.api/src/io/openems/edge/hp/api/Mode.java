package io.openems.edge.hp.api;

import io.openems.common.types.OptionsEnum;

public enum Mode implements OptionsEnum {
	UNDEFINED(-1, "UNDEFINED"), //
	ON(0, "ON"), //
	OFF(1, "OFF"), //
	PART_OPERATION(2, "PART_OPERATION");

	private final int value;
	private final String name;

	private Mode(int value, String name) {
		this.value = value;
		this.name = name;
	}

	@Override
	public int getValue() {
		return value;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public OptionsEnum getUndefined() {
		return UNDEFINED;
	}
}
