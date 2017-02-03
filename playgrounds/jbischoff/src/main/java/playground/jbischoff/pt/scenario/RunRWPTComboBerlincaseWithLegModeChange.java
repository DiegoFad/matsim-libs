/* *********************************************************************** *
 * project: org.matsim.*
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2016 by the members listed in the COPYING,        *
 *                   LICENSE and WARRANTY file.                            *
 * email           : info at matsim dot org                                *
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *   See also COPYING, LICENSE and WARRANTY file                           *
 *                                                                         *
 * *********************************************************************** */

/**
 * 
 */
package playground.jbischoff.pt.scenario;

import org.matsim.api.core.v01.Scenario;
import org.matsim.contrib.av.intermodal.router.config.*;
import org.matsim.contrib.av.robotaxi.scoring.TaxiFareConfigGroup;
import org.matsim.contrib.dvrp.data.*;
import org.matsim.contrib.dvrp.data.file.VehicleReader;
import org.matsim.contrib.dvrp.trafficmonitoring.VrpTravelTimeModules;
import org.matsim.contrib.dynagent.run.DynQSimModule;
import org.matsim.contrib.taxi.run.*;
import org.matsim.core.config.*;
import org.matsim.core.controler.Controler;
import org.matsim.core.controler.OutputDirectoryHierarchy.OverwriteFileSetting;
import org.matsim.core.scenario.ScenarioUtils;

import playground.jbischoff.pt.strategy.ChangeSingleLegModeWithPredefinedFromModesModule;

/**
 * @author  jbischoff
 *
 */
/**
 *
 */
public class RunRWPTComboBerlincaseWithLegModeChange {
	public static void main(String[] args) {

//		 if (args.length!=1){
//		 throw new RuntimeException("Wrong arguments");
//		 }
		String configfile = "C:/Users/Joschka/Documents/shared-svn/studies/jbischoff/multimodal/berlin/input/modechoice/config_modechoice.xml";

		Config config = ConfigUtils.loadConfig(configfile, new TaxiConfigGroup(), new TaxiFareConfigGroup());
		config.controler().setOverwriteFileSetting(OverwriteFileSetting.deleteDirectoryIfExists);

		TaxiConfigGroup taxiCfg = TaxiConfigGroup.get(config);
		config.addConfigConsistencyChecker(new TaxiConfigConsistencyChecker());
		config.checkConsistency();

		Scenario scenario2 = ScenarioUtils.createScenario(ConfigUtils.createConfig());


		VariableAccessModeConfigGroup walk = new VariableAccessModeConfigGroup();
		walk.setDistance(1000);
		walk.setTeleported(true);
		walk.setMode("walk");

		VariableAccessModeConfigGroup taxi = new VariableAccessModeConfigGroup();
		taxi.setDistance(20000);
		taxi.setTeleported(false);
		taxi.setMode("taxi");
		VariableAccessConfigGroup vacfg = new VariableAccessConfigGroup();
		vacfg.setAccessModeGroup(taxi);
		vacfg.setAccessModeGroup(walk);

		config.addModule(vacfg);

		Scenario scenario = ScenarioUtils.loadScenario(config);
		VrpDataImpl taxiData = new VrpDataImpl();
		new VehicleReader(scenario.getNetwork(), taxiData)
				.readFile(taxiCfg.getTaxisFileUrl(config.getContext()).getFile());
		Controler controler = new Controler(scenario);
		controler.addOverridingModule(new TaxiModule(taxiData));
		double expAveragingAlpha = 0.05;// from the AV flow paper

		controler.addOverridingModule(VrpTravelTimeModules.createTravelTimeEstimatorModule(expAveragingAlpha));
		controler.addOverridingModule(new DynQSimModule<>(TaxiQSimProvider.class));
		controler.addOverridingModule(new ChangeSingleLegModeWithPredefinedFromModesModule());
		controler.run();

	}
}
