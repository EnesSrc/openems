package io.openems.edge.simulator.heatpump;


import java.io.IOException;



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
import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;
import org.osgi.service.metatype.annotations.Designate;

import io.openems.common.channel.Unit;
import io.openems.common.types.ChannelAddress;
import io.openems.common.types.OpenemsType;
import io.openems.edge.common.channel.Doc;
import io.openems.edge.common.component.AbstractOpenemsComponent;
import io.openems.edge.common.component.OpenemsComponent;
import io.openems.edge.common.event.EdgeEventConstants;
import io.openems.edge.hp.api.Mode;
import io.openems.edge.hp.api.hp;
import io.openems.edge.simulator.datasource.api.SimulatorDatasource;


@Designate(ocd = Config.class, factory = true)
@Component(//
		name = "Heatpump.HeatStorage", //
		immediate = true, //
		configurationPolicy = ConfigurationPolicy.REQUIRE, //
		property = { //
				EventConstants.EVENT_TOPIC + "=" + EdgeEventConstants.TOPIC_CYCLE_BEFORE_PROCESS_IMAGE, //
				"type=PRODUCTION_AND_CONSUMPTION"//
		} //
)
public class Heatpump extends AbstractOpenemsComponent
		implements  hp, OpenemsComponent, EventHandler {

	public enum ChannelId implements io.openems.edge.common.channel.ChannelId {
		/**
		 * State of Charge.
		 * 
		 * <ul>
		 * <li>Interface: HP
		 * <li>Type: Integer
		 * <li>Unit: &
		 * <li>Range: 0..100
		 * </ul>
		 */
		SIMULATED_HEATPUMP_POWER(Doc.of(OpenemsType.INTEGER)//
				.unit(Unit.WATT));
		

		private final Doc doc;

		private ChannelId(Doc doc) {
			this.doc = doc;
		}
		
		public Doc doc() {
			return this.doc;
		}
	}
	
	/**
	 * Max. Power of HeatPump [kJ] = [Ws]
	 */
	private int activePower = 0;
	
	/**
	 * Maximal Temperature [°C]
	 */
	private int maxTemp = 0;
	
	/**
	 * COP of HeatPump
	 */
	private double COP = 0;
	
	/**
	 * specific heat capacity [kJ/(kg/K)]
	 */
	static private double cp = 4180; 
	
	private Mode mode = null;
	
	@Reference
	protected ConfigurationAdmin cm;
	
	@Reference(policy = ReferencePolicy.STATIC, policyOption = ReferencePolicyOption.GREEDY, cardinality = ReferenceCardinality.MANDATORY)
	protected SimulatorDatasource datasource;
	
	@Activate
	void activate(ComponentContext context, Config config) throws IOException {
		super.activate(context, config.id(), config.alias(), config.enabled());
		
		
		// update filter for 'datasource'
		if (OpenemsComponent.updateReferenceFilter(cm, this.servicePid(), "datasource", config.datasource_id())) {
			return;
		}
		
		this.maxTemp = config.maxTemp();
		this.activePower = config.activePower();
		this.mode = config.Mode();
				
		this.getMode().setNextValue(config.Mode());
	}
	
	public Heatpump() {
		super(//
				OpenemsComponent.ChannelId.values(), //
				hp.ChannelId.values(), //
				ChannelId.values() //
		);
	}

	@Deactivate
	protected void deactivate() {
		super.deactivate();
	}
	
	
	@Override
	public void handleEvent(Event event) {
		switch (event.getTopic()) {
		case EdgeEventConstants.TOPIC_CYCLE_BEFORE_PROCESS_IMAGE:
			this.updateChannels();
			break;
		}
	}
	
	public void heat() {
	}
	
	public void getcurrentCOP() {
		int T_PVinput = this.datasource.getValue(OpenemsType.INTEGER,
				new ChannelAddress(this.id(), "Temperature"));
		this.COP = 6.08 - 0.09 * (this.maxTemp - T_PVinput) + 0.0005 * ((this.maxTemp - T_PVinput)*(this.maxTemp - T_PVinput));
	}
	
	public void looseactiveEnergy() {
		
	}
	
	public void SimulatedEnergy() {
		int T_PVinput = this.datasource.getValue(OpenemsType.INTEGER,
				new ChannelAddress(this.id(), "Temperature"));
		this.activePower = (int) (Heatpump.cp * (this.maxTemp - T_PVinput));
	}
	
	private void updateChannels() {
		
		this.mode = this.getMode().getNextValue().asEnum();
		switch(this.mode.getName()) {
		case "ON":
			SimulatedEnergy();
			break;
		case "OFF":
			break;
		case "PART_OPERATION":
			break;
		}
		
		getcurrentCOP();
		this.getActivePower().setNextValue(this.activePower);
		this.getCOP().setNextValue(this.COP);
	}
	
	@Override
	public String debugLog() {
	return "E:" //
			+ "Pv:"; //
}


}
