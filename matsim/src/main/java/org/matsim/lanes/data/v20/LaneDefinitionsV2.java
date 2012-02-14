/* *********************************************************************** *
 * project: org.matsim.*
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2010 by the members listed in the COPYING,        *
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

package org.matsim.lanes.data.v20;

import java.util.SortedMap;

import org.matsim.api.core.v01.Id;
import org.matsim.core.api.internal.MatsimToplevelContainer;

/**
 * Top level container for lanes within MATSim. See package-info for documentation.
 * @author dgrether
 *
 */
public interface LaneDefinitionsV2 extends MatsimToplevelContainer {

	/**
	 *
	 * @return Map with Link Ids as keys and assignments as values
	 */
	public SortedMap<Id, LanesToLinkAssignmentV2> getLanesToLinkAssignments();

	/**
	 * Adds a LanesToLinkAssignment to the container.
	 * @param assignment
	 */
	public void addLanesToLinkAssignment(LanesToLinkAssignmentV2 assignment);
	/**
	 * Get the factory to create container content.
	 */
	@Override
	public LaneDefinitionsFactoryV2 getFactory();

}