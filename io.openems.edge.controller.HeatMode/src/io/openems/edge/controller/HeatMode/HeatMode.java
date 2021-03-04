package io.openems.edge.controller.HeatMode;

import java.time.Clock;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAmount;

import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.openems.common.channel.Level;
import io.openems.common.exceptions.OpenemsError.OpenemsNamedException;
import io.openems.edge.common.channel.Doc;
import io.openems.edge.common.component.AbstractOpenemsComponent;
import io.openems.edge.common.component.ComponentManager;
import io.openems.edge.common.component.OpenemsComponent;
import io.openems.edge.controller.api.Controller;
import io.openems.edge.hs.api.StorageHeat;
import io.openems.edge.hp.api.hp;
import io.openems.edge.hp.api.Mode;

@Designate(ocd = Config.class, factory = true)
@Component(//
		name = "Controller.HeatStorage.HeatMode", //
		immediate = true, //
		configurationPolicy = ConfigurationPolicy.REQUIRE //
)
public class HeatMode extends AbstractOpenemsComponent implements Controller, OpenemsComponent {


	private final Clock clock;

	@Reference
	protected ComponentManager componentManager;

	private final TemporalAmount hysteresis = Duration.ofSeconds(5);
	private LocalDateTime lastStateChange = LocalDateTime.MIN;
	
	private String hsId;
	private String hpId;
	private int maxLevel = 0;
	private Mode mode = Mode.UNDEFINED;

	public enum ChannelId implements io.openems.edge.common.channel.ChannelId {
		MODE_MACHINE(Doc.of(Mode.values()) //
				.text("Current State of Mode-Machine")), //
		AWAITING_HYSTERESIS(Doc.of(Level.INFO) //
				.text("Would change State, but hysteresis is active")); //

		private final Doc doc;

		private ChannelId(Doc doc) {
			this.doc = doc;
		}

		@Override
		public Doc doc() {
			return this.doc;
		}
	}

	public HeatMode() {
		this(Clock.systemDefaultZone());
	}

	protected HeatMode(Clock clock) {
		super(//
				OpenemsComponent.ChannelId.values(), //
				Controller.ChannelId.values(), //
				ChannelId.values() //
		);
		this.clock = clock;
	}

	@Activate
	void activate(ComponentContext context, Config config) {
		super.activate(context, config.id(), config.alias(), config.enabled());

		this.hsId = config.hs_id();
		this.hpId = config.hp_id();
		this.maxLevel = config.maxLevel();
	}

	@Deactivate
	protected void deactivate() {
		super.deactivate();
	}

	@Override
	public void run() throws OpenemsNamedException {
		StorageHeat hs = this.componentManager.getComponent(this.hsId);
		hp hp = this.componentManager.getComponent(this.hpId);
		
		int COP = hp.getCOP().value().get() ;
		
		do {
			if(COP > this.maxLevel)
			{	
				this.mode = Mode.OFF;
				hp.getMode().setNextValue(this.mode);
			}
			else if(COP < this.maxLevel)
			{	
				this.mode = Mode.ON;
				hp.getMode().setNextValue(this.mode);
			}
			else
			{
				this.mode = Mode.ON;
				hp.getMode().setNextValue(this.mode);
			}
		this.channel(ChannelId.MODE_MACHINE).setNextValue(this.mode);
		}while(changeMode(this.mode));
	}
	
	/**
	 * Changes the state if hysteresis time passed, to avoid too quick changes.
	 * 
	 * @param nextState the target state
	 * @return whether the state was changed
	 */
	private boolean changeMode(Mode nextState) {
		if (this.mode.getValue() != nextState.getValue()) {
			if (this.lastStateChange.plus(this.hysteresis).isBefore(LocalDateTime.now(this.clock))) {
				this.mode = nextState;
				this.lastStateChange = LocalDateTime.now(this.clock);
				this.channel(ChannelId.AWAITING_HYSTERESIS).setNextValue(false);
				return true;
			} else {
				this.channel(ChannelId.AWAITING_HYSTERESIS).setNextValue(true);
				return false;
			}
		} else {
			this.channel(ChannelId.AWAITING_HYSTERESIS).setNextValue(false);
			return false;
		}
	}
	@Override
	public String debugLog() {
	return "MODE:" + this.mode;//
}

	}