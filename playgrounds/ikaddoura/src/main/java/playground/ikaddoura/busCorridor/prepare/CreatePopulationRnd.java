/* *********************************************************************** *
 * project: org.matsim.*
 * CreatePopulation.java
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2011 by the members listed in the COPYING,        *
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

package playground.ikaddoura.busCorridor.prepare;


import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.TransportMode;
import org.matsim.api.core.v01.population.Activity;
import org.matsim.api.core.v01.population.Leg;
import org.matsim.api.core.v01.population.Person;
import org.matsim.api.core.v01.population.Plan;
import org.matsim.api.core.v01.population.Population;
import org.matsim.api.core.v01.population.PopulationWriter;
import org.matsim.core.basic.v01.IdImpl;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.core.config.ConfigUtils;

public class CreatePopulationRnd implements Runnable {
	private Map<String, Coord> zoneGeometries = new HashMap<String, Coord>();
	private Scenario scenario;
	private Population population;
		
	public static void main(String[] args) {
		CreatePopulationRnd potsdamPopulation = new CreatePopulationRnd();
		potsdamPopulation.run();
		
	}

	public void run(){
		scenario = ScenarioUtils.createScenario(ConfigUtils.createConfig());
		population = scenario.getPopulation();
		
		fillZoneData();
		
		generatePopulation();
		
		PopulationWriter populationWriter = new PopulationWriter(scenario.getPopulation(), scenario.getNetwork());
		populationWriter.write("../../shared-svn/studies/ihab/busCorridor/input_test/population_rnd.xml");
	}

	private void fillZoneData() {
		zoneGeometries.put("0", scenario.createCoord(0, 0));
		zoneGeometries.put("3", scenario.createCoord(1500, 0));

	}
	
	private void generatePopulation() {
		generateHomeWorkHomeTripsPt("0", "3", 30); // home, work, anzahl
		generateHomeWorkHomeTripsCar("0", "3", 30); // home, work, anzahl

	}

	private void generateHomeWorkHomeTripsPt(String zone1, String zone2, int quantity) {
		for (int i=0; i<quantity; ++i) {
			
			Coord homeLocation = blur(zone1);
			Coord workLocation = blur(zone2);
			
			Person person = population.getFactory().createPerson(createId(zone1, zone2, i, TransportMode.pt));
			Plan plan = population.getFactory().createPlan();
			
			plan.addActivity(createHome(homeLocation));
			plan.addLeg(createDriveLegPt());
			plan.addActivity(createWork(workLocation));
			plan.addLeg(createDriveLegPt());
			Activity homeActivity1 = (Activity) plan.getPlanElements().get(0);
			double homeEndTime = homeActivity1.getEndTime();
			Activity homeActivity2 = homeActivity1;
			homeActivity2.setEndTime(homeEndTime);
			plan.addActivity(homeActivity2);
			person.addPlan(plan);
			population.addPerson(person);
		}
	}
	
	private void generateHomeWorkHomeTripsCar(String zone1, String zone2, int quantity) {
		for (int i=0; i<quantity; ++i) {
			
			Coord homeLocation = blur(zone1);
			Coord workLocation = blur(zone2);
			
			Person person = population.getFactory().createPerson(createId(zone1, zone2, i, TransportMode.car));
			Plan plan = population.getFactory().createPlan();
			
			plan.addActivity(createHome(homeLocation));
			plan.addLeg(createDriveLegCar());
			plan.addActivity(createWork(workLocation));
			plan.addLeg(createDriveLegCar());
			Activity homeActivity1 = (Activity) plan.getPlanElements().get(0);
			double homeEndTime = homeActivity1.getEndTime();
			Activity homeActivity2 = homeActivity1;
			homeActivity2.setEndTime(homeEndTime);
			plan.addActivity(homeActivity2);
			person.addPlan(plan);
			population.addPerson(person);
		}
	}
		
	private Coord blur(String zone) {
		Random rnd = new Random();
		double xCoord = zoneGeometries.get(zone).getX()+rnd.nextDouble()*50-rnd.nextDouble()*50;
		double yCoord = zoneGeometries.get(zone).getY()+rnd.nextDouble()*50-rnd.nextDouble()*50;
		Coord zoneCoord = scenario.createCoord(xCoord, yCoord);
		return zoneCoord;
	}
	
	private Leg createDriveLegPt() {
		Leg leg = population.getFactory().createLeg(TransportMode.pt);
		return leg;
	}

	private Leg createDriveLegCar() {
		Leg leg = population.getFactory().createLeg(TransportMode.car);
		return leg;
	}
	
	private Activity createWork(Coord workLocation) {
		Random rnd = new Random();
		Activity activity = population.getFactory().createActivityFromCoord("work", workLocation);
		activity.setEndTime((16*60*60)+(rnd.nextDouble()*60*60)-(rnd.nextDouble()*60*60));
		return activity;
	}

	private Activity createHome(Coord homeLocation) {
		Random rnd = new Random();
		Activity activity = population.getFactory().createActivityFromCoord("home", homeLocation);
		activity.setEndTime(8*60*60+(rnd.nextDouble()*60*60)-(rnd.nextDouble()*60*60));
		return activity;
	}

	private Id createId(String zone1, String zone2, int i, String transportMode) {
		return new IdImpl(transportMode + "_" + zone1 + "_" + zone2 + "_" + i);
	}

}

