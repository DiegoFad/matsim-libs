/* *********************************************************************** *
 * project: org.matsim.*
 * LanesTurnInfoBuilder
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2012 by the members listed in the COPYING,        *
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
package org.matsim.lanes.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.matsim.api.core.v01.Id;
import org.matsim.core.network.algorithms.NetworkExpandNode.TurnInfo;
import org.matsim.lanes.data.v20.LaneDataV2;
import org.matsim.lanes.data.v20.LaneDefinitionsV2;
import org.matsim.lanes.data.v20.LanesToLinkAssignmentV2;

/**
 * Creates TurnInfo objects from lanes data.
 * 
 * @author dgrether
 * 
 */
public class LanesTurnInfoBuilder {

	public Map<Id, List<TurnInfo>> createTurnInfos(LaneDefinitionsV2 laneDefs) {
		Map<Id, List<TurnInfo>> inLinkIdTurnInfoMap = new HashMap<Id, List<TurnInfo>>();
		Set<Id> toLinkIds = new HashSet<Id>();
		for (LanesToLinkAssignmentV2 l2l : laneDefs.getLanesToLinkAssignments().values()) {
			toLinkIds.clear();
			for (LaneDataV2 lane : l2l.getLanes().values()) {
				if (lane.getToLinkIds() != null
						&& (lane.getToLaneIds() == null || lane.getToLaneIds().isEmpty())) { // make sure that it is a lane at the
																																									// end of a link
					toLinkIds.addAll(lane.getToLinkIds());
				}
			}
			if (!toLinkIds.isEmpty()) {
				List<TurnInfo> turnInfoList = new ArrayList<TurnInfo>();
				for (Id toLinkId : toLinkIds) {
					turnInfoList.add(new TurnInfo(l2l.getLinkId(), toLinkId));
				}
				inLinkIdTurnInfoMap.put(l2l.getLinkId(), turnInfoList);
			}
		}

		return inLinkIdTurnInfoMap;
	}

}
