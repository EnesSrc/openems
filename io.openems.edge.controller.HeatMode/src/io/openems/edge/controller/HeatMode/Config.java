package io.openems.edge.controller.HeatMode;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(//
		name = "Controller HeatStorage HeatMode", //
		description = "")
@interface Config {

	@AttributeDefinition(name = "Component-ID", description = "Unique ID of this Component")
	String id() default "limitheat0";

	@AttributeDefinition(name = "Alias", description = "Human-readable name of this Component; defaults to Component-ID")
	String alias() default "";

	@AttributeDefinition(name = "Is enabled?", description = "Is this Component enabled?")
	boolean enabled() default true;
	
	@AttributeDefinition(name = "Hs-ID", description = "ID of Hs device.")
	String hs_id() default "heatstorage0";

	@AttributeDefinition(name = "Hp-ID", description = "ID of Hp device.")
	String hp_id() default "heatpump0";
	
	@AttributeDefinition(name = "Max Level [%]", description = "Maximum Level of HeatStorage, for turn HeatPump OFF .")
	int maxLevel() default 80; //OFF
	
	String webconsole_configurationFactory_nameHint() default "Controller io.openems.edge.controller.HeatMode [{id}]";

}