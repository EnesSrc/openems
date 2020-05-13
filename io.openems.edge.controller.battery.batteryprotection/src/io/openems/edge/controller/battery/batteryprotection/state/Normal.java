package io.openems.edge.controller.battery.batteryprotection.state;

import io.openems.edge.battery.api.Battery;
import io.openems.edge.controller.battery.batteryprotection.IState;
import io.openems.edge.controller.battery.batteryprotection.State;
import io.openems.edge.ess.api.ManagedSymmetricEss;

public class Normal extends BaseState implements IState {

	int warningLowCellVoltage;
	int criticalHighCellVoltage;
	int warningSoC;
	int lowTemperature;
	int highTemperature;

	public Normal(//
			ManagedSymmetricEss ess, //
			Battery bms, //
			int warningLowCellVoltage, //
			int criticalHighCellVoltage, //
			int warningSoC, //
			int lowTemperature, //
			int highTemperature //
	) {
		super(ess, bms);
		this.warningLowCellVoltage = warningLowCellVoltage;
		this.criticalHighCellVoltage = criticalHighCellVoltage;
		this.warningSoC = warningSoC;
		this.lowTemperature = lowTemperature;
		this.highTemperature = highTemperature;
	}

	@Override
	public State getState() {
		return State.NORMAL;
	}

	@Override
	public State getNextState() {
		// According to the state machine the next states can be:
		// NORMAL: Ess is still under normal operation conditions
		// UNDEFINED: at least one important value (soc, cell voltages/temperatures) is
		// not available
		// LIMIT: one important values has reached its limit
		// FULL_CHARGE: ess was not used for defined time
		if (isNextStateUndefined()) {
			return State.UNDEFINED;
		}

		if (getBmsMinCellVoltage() < warningLowCellVoltage) {
			return State.LIMIT;
		}

		if (getBmsMaxCellVoltage() > criticalHighCellVoltage) {
			return State.LIMIT;
		}

		if (getBmsMinCellTemperature() < lowTemperature) {
			return State.LIMIT;
		}

		if (getBmsMaxCellTemperature() > highTemperature) {
			return State.LIMIT;
		}

		if (getBmsSoC() < warningSoC) {
			return State.LIMIT;
		}

		return State.NORMAL;
	}

	@Override
	public void act() {
		// nothing to do
	}
}