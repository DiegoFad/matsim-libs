/* *********************************************************************** *
 * project: org.matsim.*
 * ExeRunner.java
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2007, 2008 by the members listed in the COPYING,  *
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

package org.matsim.run.gui;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

import javax.swing.JTextArea;

import org.apache.log4j.Logger;
import org.matsim.core.utils.io.IOUtils;


/**
 * A modified version of ExeRunner which sends the output to a text area
 *
 * @author mrieser
 */
public abstract class ExeRunner {

	/*package*/ final static Logger log = Logger.getLogger(ExeRunner.class);

	public static int run(final String[] cmdArgs, final JTextArea textArea, final String workingDirectory) {
		final ExternalExecutor myExecutor = new ExternalExecutor(cmdArgs, textArea, workingDirectory);
		return waitForFinish(myExecutor);
	}
	
	public static int waitForFinish(final ExternalExecutor myExecutor) {
		synchronized (myExecutor) {
			try {
				myExecutor.start();
				myExecutor.join();
			} catch (InterruptedException e) {
				log.info("ExeRunner.run() got interrupted while waiting for timeout", e);
			}
		}
		
		return myExecutor.erg;
	}

	private static class ExternalExecutor extends Thread {
		final String[] cmdArgs;
		final JTextArea textArea;
		final String workingDirectory;
		private Process p = null;

		public int erg = -1;

		public ExternalExecutor (final String[] cmdArgs, final JTextArea textArea, final String workingDirectory) {
			this.cmdArgs = cmdArgs;
			this.textArea = textArea;
			this.workingDirectory = workingDirectory;
		}
		
		
		public void killProcess() {
			if (this.p != null) {
				this.p.destroy();
			}
		}

		@Override
		public void run()  {
			try {
				if (this.workingDirectory == null) {
					this.p = Runtime.getRuntime().exec(this.cmdArgs);
				} else {
					this.p = Runtime.getRuntime().exec(this.cmdArgs, null, new File(this.workingDirectory));
				}
				
				BufferedReader in = new BufferedReader(new InputStreamReader(this.p.getInputStream()));
				BufferedReader err = new BufferedReader(new InputStreamReader(this.p.getErrorStream()));
				
				StreamHandler outputHandler = new StreamHandler(in, this.textArea);
				outputHandler.start();

				StreamHandler errorHandler = new StreamHandler(err, this.textArea);
				errorHandler.start();

				log.info("Starting external exe with command: " + Arrays.toString(this.cmdArgs));
				boolean processRunning = true;
				while (processRunning) {
					try {
						this.p.waitFor();
						this.erg = this.p.exitValue();
						log.info("external exe returned " + this.erg);
						processRunning = false;
					} catch (InterruptedException e) {
						log.info("Thread waiting for external exe to finish was interrupted");
						this.erg = -3;
					}
				}
				try {
					outputHandler.join();
				} catch (InterruptedException e) {
					log.info("got interrupted while waiting for outputHandler to die.", e);
				}
				try {
					errorHandler.join();
				} catch (InterruptedException e) {
					log.info("got interrupted while waiting for errorHandler to die.", e);
				}
			} catch (IOException e) {
				e.printStackTrace();
				this.erg = -2;
			}
		}
	}
	
	static class StreamHandler extends Thread {
		private final BufferedReader in;
		private final JTextArea textArea;

		public StreamHandler(final BufferedReader in, final JTextArea textArea) {
			this.in = in;
			this.textArea = textArea;
		}

		@Override
		public void run() {
			try {
				String line = null;
				while ((line = this.in.readLine()) != null) {
					this.textArea.append(line);
					this.textArea.append(IOUtils.NATIVE_NEWLINE);
					int length = this.textArea.getDocument().getLength();
					this.textArea.setCaretPosition(length);
					
					if (length > 512*1024) {
						this.textArea.setText(this.textArea.getText().substring(256*1024));
					}
				}
			} catch (IOException e) {
				log.info("StreamHandler got interrupted");
				e.printStackTrace();
			}
		}
	}

}