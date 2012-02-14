/* *********************************************************************** *
 * project: org.matsim.*
 * MatsimLaneDefinitionWriter
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2009 by the members listed in the COPYING,        *
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
package org.matsim.lanes.data;

import org.matsim.core.api.internal.MatsimSomeWriter;
import org.matsim.core.utils.io.MatsimJaxbXmlWriter;
import org.matsim.lanes.data.v11.LaneDefinitions;
import org.matsim.lanes.data.v11.LaneDefinitionsWriterV11;
import org.matsim.lanes.data.v20.LaneDefinitionsV2;
import org.matsim.lanes.data.v20.LaneDefinitionsWriterV20;


/**
 * Writes the lane definitions according to
 * the http://www.matsim.org/files/dtd/laneDefinitions_v*.xsd
 * grammar.
 * @author dgrether
 *
 */
public class MatsimLaneDefinitionsWriter implements MatsimSomeWriter {
	
	
	 
	/**
	 * Writes the file with the default format for 
	 * LaneDefinitions within MATSim.
	 * @param lanedefs
	 */
	public MatsimLaneDefinitionsWriter(){
	}
	
	
	public void writeFileV20(String filename, LaneDefinitionsV2 lanedefs){
		MatsimJaxbXmlWriter writerDelegate = new LaneDefinitionsWriterV20(lanedefs);
		writerDelegate.write(filename);
	}
	
	public void writeFileV11(String filename, LaneDefinitions lanedefs){
		MatsimJaxbXmlWriter writerDelegate = new LaneDefinitionsWriterV11(lanedefs);
		writerDelegate.write(filename);
	}
	
	
}
