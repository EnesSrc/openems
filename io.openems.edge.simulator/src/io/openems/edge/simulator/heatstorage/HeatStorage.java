package io.openems.edge.simulator.heatstorage;


import java.io.IOException;



import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;
import org.osgi.service.metatype.annotations.Designate;

import io.openems.common.channel.Unit;
import io.openems.common.types.OpenemsType;
import io.openems.edge.common.channel.Doc;
import io.openems.edge.common.component.AbstractOpenemsComponent;
import io.openems.edge.common.component.OpenemsComponent;
import io.openems.edge.common.event.EdgeEventConstants;
import io.openems.edge.hs.api.StorageHeat;


@Designate(ocd = Config.class, factory = true)
@Component(//
		name = "Simulator.HeatStorage", //
		immediate = true, //
		configurationPolicy = ConfigurationPolicy.REQUIRE, //
		property = { //
				EventConstants.EVENT_TOPIC + "=" + EdgeEventConstants.TOPIC_CYCLE_BEFORE_PROCESS_IMAGE, //
				"type=PRODUCTION_AND_CONSUMPTION"//
		} //
)
public class HeatStorage extends AbstractOpenemsComponent
		implements  StorageHeat, OpenemsComponent, EventHandler {

	public enum ChannelId implements io.openems.edge.common.channel.ChannelId {
		/**
		 * State of Charge.
		 * 
		 * <ul>
		 * <li>Interface: HS
		 * <li>Type: Integer
		 * <li>Unit: &
		 * <li>Range: 0..100
		 * </ul>
		 */
		SIMULATED_HEATSTORAGE_POWER(Doc.of(OpenemsType.INTEGER)//
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
	 * Minimal Temperature [°C]
	 */
	private int minTemp = 0;
	
	/**
	 * Maximal Temperature [°C]
	 */
	private int maxTemp = 0;
	
	/**
	 * Volume of the HeatStorage [m³]
	 */
	private int volume = 0;
	
	/**
	 * Volume of high Temperature Level of HeatStorage [m³]
	 */
	private int highvolume = 0;
	
	/**
	 * Energy of HeatStorage [Wh]
	 */
	private double energyStorage = 0;
	
	/**
	 * Energy of HeatStorage [kJ] = [Ws]
	 */
	private int activeEnergy = 0;
	
	/**
	 * Memory Level of the HeatStorage [%]
	 */
	private int memoryLevel = 0; 

	/**
	 * specific heat capacity [kJ/(kg/K)]
	 */
	static private double cp = 4180; 
	
	@Reference
	protected ConfigurationAdmin cm;
	
	@Activate
	void activate(ComponentContext context, Config config) throws IOException {
		super.activate(context, config.id(), config.alias(), config.enabled());
		
		this.volume = config.volume();
		this.maxTemp = config.maxTemp();
		this.minTemp = config.minTemp();
		this.memoryLevel = config.memoryLevel();
		
		this.highvolume = this.highvolume * (this.memoryLevel/100);
		this.energyStorage = (this.highvolume * HeatStorage.cp * (this.maxTemp - this.minTemp))/3600;
		
	}
	
	public HeatStorage() {
		super(//
				OpenemsComponent.ChannelId.values(), //
				StorageHeat.ChannelId.values(), //
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

	private void refreshHeatStorage() {
		
		this.highvolume = (int) (this.energyStorage /( HeatStorage.cp * (this.maxTemp - this.minTemp)));
	}
	
	private void updateChannels() {
		
		refreshHeatStorage();
		
		this.channel(ChannelId.SIMULATED_HEATSTORAGE_POWER).setNextValue(this.energyStorage);
		this.getCapacity().setNextValue(this.energyStorage);
		this.getActivePower().setNextValue(this.activeEnergy);
	}
	
	@Override
	public String debugLog() {
	return "E:" + this.energyStorage//
			+ "Pv:" ;//
}


}
