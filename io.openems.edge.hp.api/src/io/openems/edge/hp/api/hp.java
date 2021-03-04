package io.openems.edge.hp.api;

import org.osgi.annotation.versioning.ProviderType;


import io.openems.common.channel.AccessMode;
import io.openems.common.channel.Unit;
import io.openems.common.types.OpenemsType;
import io.openems.edge.common.channel.Channel;
import io.openems.edge.common.channel.Doc;
import io.openems.edge.common.component.OpenemsComponent;
import io.openems.edge.common.modbusslave.ModbusSlaveNatureTable;
import io.openems.edge.common.modbusslave.ModbusType;
import io.openems.edge.hp.api.hp;

@ProviderType
public interface hp extends OpenemsComponent {

public static final String POWER_DOC_TEXT = "Negative values for Charge; positive for Discharge";
	
	public enum ChannelId implements io.openems.edge.common.channel.ChannelId {
		/**
		 * State of Mode.
		 * 
		 * <ul>
		 * <li>Interface: Hs
		 * <li>Type: Integer
		 * <li>Unit: %
		 * <li>Range: 0..100
		 * </ul>
		 */
		MODE(Doc.of(Mode.values())), //
				
		/**
		 * Capacity.
		 * 
		 * <ul>
		 * <li>Interface: Hs
		 * <li>Type: Integer
		 * <li>Unit: percent
		 * </ul>
		 * 
		 * @since 2019.5.0
		 */
		COP(Doc.of(OpenemsType.INTEGER) //
				.unit(Unit.PERCENT)),
		
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
		return ModbusSlaveNatureTable.of(hp.class, accessMode, 100) //
				.channel(0, ChannelId.MODE, ModbusType.UINT16) //
				.channel(2, ChannelId.ACTIVE_POWER, ModbusType.FLOAT32) //
				.channel(4, ChannelId.COP, ModbusType.UINT16) //
				.build();
	}

	/**
	 * Gets the enum Mode : 
	 * UNDEFINED(-1, "UNDEFINED"),
	 * NORMAL(0, "ON"),
	 * FOCUS_HEAT(1, "OFF"),
	 * FOCUS_POWER(2, "PART_OPERATION");
	 * 
	 * @return the Channel
	 */
	default Channel<Integer> getMode() {
		return this.channel(ChannelId.MODE);
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
	 * Get COP of HeatPump.
	 * 
	 * @return the Channel
	 */
	default Channel<Integer> getCOP() {
		return this.channel(ChannelId.COP);
	}
}
