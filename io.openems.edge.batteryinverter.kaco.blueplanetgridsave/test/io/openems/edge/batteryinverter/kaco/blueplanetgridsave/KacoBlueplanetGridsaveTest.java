package io.openems.edge.batteryinverter.kaco.blueplanetgridsave;

import java.lang.reflect.Method;

import org.junit.Test;

import io.openems.common.types.ChannelAddress;
import io.openems.edge.battery.api.Battery;
import io.openems.edge.battery.test.DummyBattery;
<<<<<<< HEAD
import io.openems.edge.batteryinverter.kaco.blueplanetgridsave.KacoSunSpecModel.S64201.S64201_CurrentState;
=======
import io.openems.edge.batteryinverter.kaco.blueplanetgridsave.KacoSunSpecModel.S64201.S64201CurrentState;
>>>>>>> develop
import io.openems.edge.batteryinverter.kaco.blueplanetgridsave.statemachine.State;
import io.openems.edge.bridge.modbus.test.DummyModbusBridge;
import io.openems.edge.common.channel.ChannelId;
import io.openems.edge.common.component.AbstractOpenemsComponent;
import io.openems.edge.common.component.OpenemsComponent;
import io.openems.edge.common.event.EdgeEventConstants;
import io.openems.edge.common.startstop.StartStopConfig;
import io.openems.edge.common.test.AbstractComponentTest.TestCase;
import io.openems.edge.common.test.ComponentTest;
import io.openems.edge.common.test.DummyConfigurationAdmin;
import io.openems.edge.common.test.DummyCycle;

public class KacoBlueplanetGridsaveTest {

<<<<<<< HEAD
	private final static String BATTERY_INVERTER_ID = "batteryInverter0";
	private final static String BATTERY_ID = "battery0";
	private final static String MODBUS_ID = "modbus0";

	private final static ChannelAddress STATE_MACHINE = new ChannelAddress(BATTERY_INVERTER_ID, "StateMachine");

	private final static ChannelAddress CURRENT_STATE = new ChannelAddress(BATTERY_INVERTER_ID,
=======
	private static final String BATTERY_INVERTER_ID = "batteryInverter0";
	private static final String BATTERY_ID = "battery0";
	private static final String MODBUS_ID = "modbus0";

	private static final ChannelAddress STATE_MACHINE = new ChannelAddress(BATTERY_INVERTER_ID, "StateMachine");

	private static final ChannelAddress CURRENT_STATE = new ChannelAddress(BATTERY_INVERTER_ID,
>>>>>>> develop
			KacoSunSpecModel.S64201.CURRENT_STATE.getChannelId().id());

	private static class MyComponentTest extends ComponentTest {

		private final Battery battery = new DummyBattery(BATTERY_ID);

		public MyComponentTest(OpenemsComponent sut) {
			super(sut);
		}

		@Override
		protected void handleEvent(String topic) throws Exception {
			if (topic.equals(EdgeEventConstants.TOPIC_CYCLE_BEFORE_WRITE)) {
<<<<<<< HEAD
				((KacoBlueplanetGridsaveImpl) this.getSut()).apply(this.battery, 0, 0);
=======
				((KacoBlueplanetGridsaveImpl) this.getSut()).run(this.battery, 0, 0);
>>>>>>> develop
			}
			super.handleEvent(topic);
		}

	}

	@Test
	public void testStart() throws Exception {
		KacoBlueplanetGridsaveImpl sut = new KacoBlueplanetGridsaveImpl();

		ComponentTest test = new MyComponentTest(sut) //
				.addReference("cycle", new DummyCycle(1000)) //
				.addReference("cm", new DummyConfigurationAdmin()) //
				.addReference("setModbus", new DummyModbusBridge(MODBUS_ID));

		// TODO implement proper Dummy-Modbus-Bridge with SunSpec support. Till then...
<<<<<<< HEAD
		test.addReference("isSunSpecInitializationFinished", true); //
=======
		test.addReference("isSunSpecInitializationCompleted", true); //
>>>>>>> develop
		Method addChannel = AbstractOpenemsComponent.class.getDeclaredMethod("addChannel", ChannelId.class);
		addChannel.setAccessible(true);
		addChannel.invoke(sut, KacoSunSpecModel.S64203.BAT_SOC_0.getChannelId());
		addChannel.invoke(sut, KacoSunSpecModel.S64203.BAT_SOH_0.getChannelId());
		addChannel.invoke(sut, KacoSunSpecModel.S64203.BAT_TEMP_0.getChannelId());
		addChannel.invoke(sut, KacoSunSpecModel.S64202.DIS_MIN_V_0.getChannelId());
		addChannel.invoke(sut, KacoSunSpecModel.S64202.CHA_MAX_V_0.getChannelId());
		addChannel.invoke(sut, KacoSunSpecModel.S64202.DIS_MAX_A_0.getChannelId());
		addChannel.invoke(sut, KacoSunSpecModel.S64202.CHA_MAX_A_0.getChannelId());
		addChannel.invoke(sut, KacoSunSpecModel.S64202.EN_LIMIT_0.getChannelId());
		addChannel.invoke(sut, KacoSunSpecModel.S64201.REQUESTED_STATE.getChannelId());
		addChannel.invoke(sut, KacoSunSpecModel.S64201.CURRENT_STATE.getChannelId());
<<<<<<< HEAD
=======
		addChannel.invoke(sut, KacoSunSpecModel.S64201.WATCHDOG.getChannelId());
>>>>>>> develop

		test.activate(MyConfig.create() //
				.setId(BATTERY_INVERTER_ID) //
				.setStartStopConfig(StartStopConfig.START) //
				.setModbusId(MODBUS_ID).build()) //
				.next(new TestCase() //
						.output(STATE_MACHINE, State.UNDEFINED)) //
				.next(new TestCase() //
						.output(STATE_MACHINE, State.GO_RUNNING)) //
				.next(new TestCase() //
<<<<<<< HEAD
						.input(CURRENT_STATE, S64201_CurrentState.GRID_CONNECTED)) //
=======
						.input(CURRENT_STATE, S64201CurrentState.GRID_CONNECTED)) //
>>>>>>> develop
				.next(new TestCase() //
						.output(STATE_MACHINE, State.RUNNING)) //
		;
	}

}
