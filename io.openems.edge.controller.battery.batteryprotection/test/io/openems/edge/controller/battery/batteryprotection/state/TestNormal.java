package io.openems.edge.controller.battery.batteryprotection.state;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

//import io.openems.edge.battery.soltaro.ChargeIndication;
import io.openems.edge.controller.battery.batteryprotection.Config;
import io.openems.edge.controller.battery.batteryprotection.IState;
import io.openems.edge.controller.battery.batteryprotection.State;
import io.openems.edge.controller.battery.batteryprotection.helper.Creator;
import io.openems.edge.controller.battery.batteryprotection.helper.DummyBattery;
import io.openems.edge.controller.battery.batteryprotection.helper.DummyComponentManager;
import io.openems.edge.controller.battery.batteryprotection.helper.DummyEss;

public class TestNormal {

	private IState sut;
	private static DummyComponentManager componentManager;
	private static Config config;
	private DummyEss ess;
	private DummyBattery bms;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		config = Creator.createConfig();
		componentManager = new DummyComponentManager();
	}

	@Before
	public void setUp() throws Exception {
		// Always create ess and bms newly to have them in "normal" situation that does
		// nothing
		componentManager.initEss();
		ess = componentManager.getComponent(Creator.ESS_ID);
		componentManager.initBms();
		bms = componentManager.getComponent(Creator.BMS_ID);
		sut = new Normal(ess, bms, config.warningLowCellVoltage(), config.criticalHighCellVoltage(),
				config.warningSoC(), config.lowTemperature(), config.highTemperature());
	}

	@Test
	public final void testGetState() {
		assertEquals(sut.getState(), State.NORMAL);
	}

	@Test
	public final void testGetNextStateWithNoChanges() {
		State next = sut.getNextState();
		assertEquals(State.NORMAL, next);
	}

	@Test
	public final void testGetNextStateUndefinedNoVoltage() {
		bms.setMinimalCellVoltageToUndefined();
		State next = sut.getNextState();
		assertEquals(State.UNDEFINED, next);
	}

	@Test
	public final void testGetNextStateLimitLowCellVoltage() {
		bms.setMinimalCellVoltage(config.warningLowCellVoltage() - 1);
		State next = sut.getNextState();
		assertEquals(State.LIMIT, next);

		bms.setMinimalCellVoltage(config.warningLowCellVoltage());
		next = sut.getNextState();
		assertEquals(State.NORMAL, next);
	}

	@Test
	public final void testGetNextStateLimitHighCellVoltage() {
		bms.setMaximalCellVoltage(config.criticalHighCellVoltage() + 1);
		State next = sut.getNextState();
		assertEquals(State.LIMIT, next);

		bms.setMaximalCellVoltage(config.criticalHighCellVoltage());
		next = sut.getNextState();
		assertEquals(State.NORMAL, next);
	}

	@Test
	public final void testGetNextStateLimitLowCellTemperature() {
		bms.setMinimalCellTemperature(config.lowTemperature() - 1);
		State next = sut.getNextState();
		assertEquals(State.LIMIT, next);

		bms.setMinimalCellTemperature(config.lowTemperature());
		next = sut.getNextState();
		assertEquals(State.NORMAL, next);
	}

	@Test
	public final void testGetNextStateLimitHighCellTemperature() {
		bms.setMaximalCellTemperature(config.highTemperature() + 1);
		State next = sut.getNextState();
		assertEquals(State.LIMIT, next);

		bms.setMaximalCellTemperature(config.highTemperature());
		next = sut.getNextState();
		assertEquals(State.NORMAL, next);
	}

	@Test
	public final void testGetNextStateLimitSoc() {
		bms.setSoc(config.warningSoC() - 1);
		State next = sut.getNextState();
		assertEquals(State.LIMIT, next);

		bms.setSoc(config.warningSoC());
		next = sut.getNextState();
		assertEquals(State.NORMAL, next);
	}

	@Test
	public final void testAct() {
		// act should have no interference on ess
		try {
			int power = 1000;
			ess.setCurrentActivePower(power);
			sut.act();
			assertEquals(power, ess.getCurrentActivePower());

			power = -1000;
			ess.setCurrentActivePower(power);
			sut.act();
			assertEquals(power, ess.getCurrentActivePower());
		} catch (Exception e) {
			fail();
		}
	}

}
