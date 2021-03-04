package io.openems.edge.simulator.heatstorage;

import org.osgi.service.metatype.annotations.AttributeDefinition;


import org.osgi.service.metatype.annotations.ObjectClassDefinition;


@ObjectClassDefinition(//
		name = "Simulator HeatStorage", //
		description = "")
@interface Config {

	@AttributeDefinition(name = "Component-ID", description = "Unique ID of this Component")
	String id() default "heatstorage0";

	@AttributeDefinition(name = "Alias", description = "Human-readable name of this Component; defaults to Component-ID")
	String alias() default "";

	@AttributeDefinition(name = "Is enabled?", description = "Is this Component enabled?")
	boolean enabled() default true;

	@AttributeDefinition(name = "Max. Temp [°C]")
	int maxTemp() default 80;
	
	@AttributeDefinition(name = "Min. Temp [°C]")
	int  minTemp() default 20;
	
	@AttributeDefinition(name = "Memory Level [%]")
	int  memoryLevel() default 50;
	
	@AttributeDefinition(name = "Volumen [m³]")
	int volume() default 1000;
	
	String webconsole_configurationFactory_nameHint() default "io.openems.edge.simulator.heatstorage [{id}]";

}