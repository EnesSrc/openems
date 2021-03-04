package io.openems.edge.simulator.heatpump;

import org.osgi.service.metatype.annotations.AttributeDefinition;


import org.osgi.service.metatype.annotations.ObjectClassDefinition;

import io.openems.edge.hp.api.Mode;

@ObjectClassDefinition(//
		name = "Simulator HeatPump", //
		description = "")
@interface Config {

	@AttributeDefinition(name = "Component-ID", description = "Unique ID of this Component")
	String id() default "heatpump0";

	@AttributeDefinition(name = "Alias", description = "Human-readable name of this Component; defaults to Component-ID")
	String alias() default "";

	@AttributeDefinition(name = "Is enabled?", description = "Is this Component enabled?")
	boolean enabled() default true;
	
	@AttributeDefinition(name = "Max. Temp [°C]")
	int maxTemp() default 80;
	
	@AttributeDefinition(name = "Max. active Power [W]")
	int activePower() default 5000;
	
	@AttributeDefinition(name = "MODE")
	Mode  Mode() default Mode.ON;
	
	@AttributeDefinition(name = "Datasource-ID", description = "ID of Simulator Datasource.")
	String datasource_id() default "datasource0";
	
	String webconsole_configurationFactory_nameHint() default "io.openems.edge.simulator.heatstorage [{id}]";

}