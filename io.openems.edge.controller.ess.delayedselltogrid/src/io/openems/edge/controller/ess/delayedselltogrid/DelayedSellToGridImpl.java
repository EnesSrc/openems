package io.openems.edge.controller.ess.delayedselltogrid;

import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferencePolicyOption;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.openems.common.exceptions.OpenemsError.OpenemsNamedException;
import io.openems.edge.common.component.AbstractOpenemsComponent;
import io.openems.edge.common.component.ComponentManager;
import io.openems.edge.common.component.OpenemsComponent;
import io.openems.edge.common.sum.GridMode;
import io.openems.edge.controller.api.Controller;
import io.openems.edge.ess.api.ManagedSymmetricEss;
import io.openems.edge.ess.power.api.Power;
import io.openems.edge.meter.api.SymmetricMeter;

@Designate(ocd = Config.class, factory = true)
@Component(//
		name = "Controller.Ess.DelayedSellToGrid", //
		immediate = true, //
		configurationPolicy = ConfigurationPolicy.REQUIRE //
)
public class DelayedSellToGridImpl extends AbstractOpenemsComponent
		implements DelayedSellToGrid, Controller, OpenemsComponent {

	private final Logger log = LoggerFactory.getLogger(DelayedSellToGridImpl.class);
	private State state = State.DO_NOTHING;

	@Reference
	protected ComponentManager componentManager;

	@Reference
	protected ConfigurationAdmin cm;

	@Reference
	protected Power power;

	@Reference(policy = ReferencePolicy.STATIC, policyOption = ReferencePolicyOption.GREEDY, cardinality = ReferenceCardinality.MANDATORY)
	private ManagedSymmetricEss ess;

	@Reference(policy = ReferencePolicy.STATIC, policyOption = ReferencePolicyOption.GREEDY, cardinality = ReferenceCardinality.MANDATORY)
	private SymmetricMeter meter;

	private Config config;

	public DelayedSellToGridImpl() {
		super(//
				OpenemsComponent.ChannelId.values(), //
				Controller.ChannelId.values(), //
				DelayedSellToGrid.ChannelId.values() //
		);
	}

	@Activate
	void activate(ComponentContext context, Config config) {
		super.activate(context, config.id(), config.alias(), config.enabled());
		this.config = config;
		if (OpenemsComponent.updateReferenceFilter(this.cm, this.servicePid(), "meter", config.meter_id())) {
			return;
		}
		if (OpenemsComponent.updateReferenceFilter(this.cm, this.servicePid(), "ess", config.ess_id())) {
			return;
		}
	}

	@Deactivate
	protected void deactivate() {
		super.deactivate();
	}

	@Override
	public void run() throws OpenemsNamedException {
		GridMode gridMode = this.ess.getGridMode();
		if (gridMode.isUndefined()) {
			this.logWarn(this.log, "Grid-Mode is [UNDEFINED]");
		}
		switch (gridMode) {
		case ON_GRID:
		case UNDEFINED:
			break;
		case OFF_GRID:
			return;
		}

		int calculatedPower = 0;
		int gridPower = this.meter.getActivePower().orElse(0);

		boolean stateChanged;

		do {
			stateChanged = false;
			switch (this.state) {
			case ABOVE_SELL_TO_GRID_LIMIT:
				if (-gridPower < this.config.continuousSellToGridPower()) {
					stateChanged = this.changeState(State.UNDER_CONTINUOUS_SELL_TO_GRID);
					break;
				}
				if (-gridPower > this.config.continuousSellToGridPower()
						&& -gridPower < this.config.sellToGridPowerLimit()) {
					stateChanged = this.changeState(State.DO_NOTHING);
					break;
				}
				calculatedPower = gridPower + this.config.sellToGridPowerLimit();
				break;

			case DO_NOTHING:
				if (-gridPower > this.config.sellToGridPowerLimit()) {
					stateChanged = this.changeState(State.ABOVE_SELL_TO_GRID_LIMIT);
					break;
				}
				if (-gridPower < this.config.continuousSellToGridPower()) {
					stateChanged = this.changeState(State.UNDER_CONTINUOUS_SELL_TO_GRID);
					break;
				}

				calculatedPower = 0;
				break;

			case UNDER_CONTINUOUS_SELL_TO_GRID:
				if (-gridPower > this.config.sellToGridPowerLimit()) {
					stateChanged = this.changeState(State.ABOVE_SELL_TO_GRID_LIMIT);
					break;
				}

				if (-gridPower > this.config.continuousSellToGridPower()
						&& -gridPower < this.config.sellToGridPowerLimit()) {
					stateChanged = this.changeState(State.DO_NOTHING);
					break;
				}
				calculatedPower = this.config.continuousSellToGridPower() - Math.abs(gridPower);
				break;
			}
		} while (stateChanged);

		// Set calculate power channel
		this.channel(DelayedSellToGrid.ChannelId.CALCULATED_POWER).setNextValue(calculatedPower);

		// Set the power
		this.setResult(calculatedPower);

		// set the State machine
		this.channel(DelayedSellToGrid.ChannelId.STATE_MACHINE).setNextValue(this.state);
	}

	protected void setResult(int calculatedPower) throws OpenemsNamedException {
		this.ess.setActivePowerEquals(calculatedPower);
		this.ess.setReactivePowerEquals(0);
	}

	/**
	 * Changes the state updated, to avoid too quick changes.
	 * 
	 * @param nextState the target state
	 * @return whether the state was changed
	 */
	private boolean changeState(State nextState) {
		if (this.state != nextState) {
			this.state = nextState;
			return true;
		} else {
			return false;
		}
	}
}
