package playground.vsp.ev;

import com.google.inject.Inject;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.events.ActivityStartEvent;
import org.matsim.api.core.v01.events.PersonArrivalEvent;
import org.matsim.api.core.v01.events.handler.ActivityStartEventHandler;
import org.matsim.api.core.v01.events.handler.PersonArrivalEventHandler;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.population.Person;
import org.matsim.core.controler.IterationCounter;
import org.matsim.core.controler.OutputDirectoryHierarchy;
import org.matsim.core.mobsim.framework.events.MobsimBeforeCleanupEvent;
import org.matsim.core.mobsim.framework.listeners.MobsimBeforeCleanupListener;
import org.matsim.core.network.NetworkUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author diego
 */
public class ChargingDetourHandler implements ActivityStartEventHandler, PersonArrivalEventHandler, MobsimBeforeCleanupListener {

	@Inject
	OutputDirectoryHierarchy controlerIO;
	@Inject
	IterationCounter iterationCounter;
	@Inject
	Scenario scenario;

	//alle Personen mit EV
	private final List <Id<Person>> personsWithEV = new ArrayList<>();
	private final Map<Id<Person>, Id<Link>> personToChargingLink = new HashMap<>();
	private final Network network;


	@Inject
	public ChargingDetourHandler(Network network, Scenario scenario){
		this.network = network;
		this.scenario = scenario;
	}
	@Override
	public void handleEvent(ActivityStartEvent event){
		if (event.getActType().endsWith("car plugin interaction")){
			personsWithEV.add(event.getPersonId());
			var chargingLink = event.getLinkId();
			personToChargingLink.put(event.getPersonId(), event.getLinkId());

		}
	}

	@Override
	public void handleEvent(PersonArrivalEvent event) {
		var activityLink = event.getLinkId();
		var chargingLink = personToChargingLink.get(event.getPersonId());
		if(event.getLegMode().equals("walk")){
			double detour = NetworkUtils.getEuclideanDistance(network.getLinks().get(chargingLink).getCoord(),
					network.getLinks().get(activityLink).getCoord());
			System.out.println("Detour:" + detour + "," + activityLink + "," + chargingLink);
		}
		else{
			System.out.println("Fehler in handle PersonArrivalEvent");
		}
	}

	@Override
	public void notifyMobsimBeforeCleanup(MobsimBeforeCleanupEvent e) {

	}
}


