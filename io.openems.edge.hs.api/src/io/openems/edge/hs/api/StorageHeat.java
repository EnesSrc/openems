package io.openems.edge.hs.api;

import org.osgi.annotation.versioning.ProviderType;

import io.openems.common.channel.AccessMode;
import io.openems.common.channel.Unit;
import io.openems.common.types.OpenemsType;
import io.openems.edge.common.channel.Channel;
import io.openems.edge.common.channel.Doc;
import io.openems.edge.common.component.OpenemsComponent;
import io.openems.edge.common.modbusslave.ModbusSlaveNatureTable;
import io.openems.edge.common.modbusslave.ModbusType;


@ProviderType
public interface StorageHeat extends OpenemsComponent {
	
	public static final String POWER_DOC_TEXT = "Negative values for Charge; positive for Discharge";
	
	public enum ChannelId implements io.openems.edge.common.channel.ChannelId {
				
		/**
		 * Capacity.
		 * 
		 * <ul>
		 * <li>Interface: Hs
		 * <li>Type: Integer
		 * <li>Unit: Wh
		 * </ul>
		 * 
		 * @since 2019.5.0
		 */
		CAPACITY(Doc.of(OpenemsType.INTEGER) //
				.unit(Unit.WATT_HOURS)),
		
		/**
		 * Active Power.
		 * 
		 * <ul>
		 * <li>Interface: Hs
		 * <li>Type: Integer
		 * <li>Unit: W
		 * <li>Range: negative values for Charge; positive for Discharge
		 * </ul>
		 */
		ACTIVE_POWER(Doc.of(OpenemsType.INTEGER) //
				.unit(Unit.WATT) //
				.text(POWER_DOC_TEXT) //
		),
		/**
		 * Min Layer Temperature.
		 * 
		 * <ul>
		 * <li>Interface: Hs
		 * <li>Type: Integer
		 * <li>Unit: Â°C
		 * <li>Range: -273 to positive infinity
		 * </ul>
		 * 
		 * @since 2019.17.0
		 */
		MIN_LAYER_TEMPERATURE(Doc.of(OpenemsType.INTEGER) //
				.unit(Unit.DEGREE_CELSIUS) //
		),		
		/**
		 * Avg Layer Temperature.
		 * 
		 * <ul>
		 * <li>Interface: Hs
		 * <li>Type: Integer
		 * <li>Unit: Â°C
		 * <li>Range: -273 to positive infinity
		 * </ul>
		 * 
		 * @since 2019.17.0
		 */
		AVG_LAYER_TEMPERATURE(Doc.of(OpenemsType.INTEGER) //
				.unit(Unit.DEGREE_CELSIUS) //
		),
		/**
		 * Max Layer Temperature.
		 * 
		 * <ul>
		 * <li>Interface: Hs
		 * <li>Type: Integer
		 * <li>Unit: Â°C
		 * <li>Range: -273 to positive infinity
		 * </ul>
		 * 
		 * @since 2019.17.0
		 */
		MAX_LAYER_TEMPERATURE(Doc.of(OpenemsType.INTEGER) //
				.unit(Unit.DEGREE_CELSIUS) //
		);

		private final Doc doc;

		private ChannelId(Doc doc) {
			this.doc = doc;
		}

		@Override
		public Doc doc() {
			return this.doc;
		}

	}

	public static ModbusSlaveNatureTable getModbusSlaveNatureTable(AccessMode accessMode) {
		return ModbusSlaveNatureTable.of(StorageHeat.class, accessMode, 100) //
				.channel(2, ChannelId.ACTIVE_POWER, ModbusType.FLOAT32) //
				.channel(4, ChannelId.MIN_LAYER_TEMPERATURE, ModbusType.UINT16) //
				.channel(6, ChannelId.MAX_LAYER_TEMPERATURE, ModbusType.UINT16) //
				.channel(8, ChannelId.CAPACITY, ModbusType.FLOAT32) //
				.build();
	}


	/**
	 * Gets the (usable) capacity of the Battery in [Wh].
	 * 
	 * @return the Channel
	 */
	default Channel<Integer> getCapacity() {
		return this.channel(ChannelId.CAPACITY);
	}


	/**
	 * Gets the Active Power in [W]. Negative values for Charge; positive for
	 * Discharge.
	 * 
	 * @return the Channel
	 */
	default Channel<Integer> getActivePower() {
		return this.channel(ChannelId.ACTIVE_POWER);
	}
	
	/**
	 * Gets the minimum layer temperature in [Â°C].
	 * 
	 * @return the Channel
	 */
	default Channel<Integer> getMinLayerTemperature() {
		return this.channel(ChannelId.MIN_LAYER_TEMPERATURE);
	}
	
	/**
	 * Gets the maximum layer temperature in [Â°C].
	 * 
	 * @return the Channel
	 */
	default Channel<Integer> getMaxLayerTemperature() {
		return this.channel(ChannelId.MAX_LAYER_TEMPERATURE);
	}
	
	/**
	 * Gets the average layer temperature in [Â°C].
	 * 
	 * @return the Channel
	 */
	default Channel<Integer> getAvgLayerTemperature() {
		return this.channel(ChannelId.AVG_LAYER_TEMPERATURE);
	}
}
