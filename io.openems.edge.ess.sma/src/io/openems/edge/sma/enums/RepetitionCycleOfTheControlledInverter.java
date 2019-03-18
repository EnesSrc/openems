package io.openems.edge.sma.enums;

import io.openems.edge.common.channel.OptionsEnum;

public enum RepetitionCycleOfTheControlledInverter implements OptionsEnum {
	UNDEFINED(-1, "Undefined"), //
	DAILY(1189, "Daily"), //
	ONCE(2622, "Once"), //
	WEEKLY(2623, "Weekly");

	private final int value;
	private final String name;

	private RepetitionCycleOfTheControlledInverter(int value, String name) {
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