/*******************************************************************************
 * Copyright (c) 2010 Stefan A. Krüger (soon changing to Stefan A. Tzeggai).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Contributors:
 *     Stefan A. Krüger (soon changing to Stefan A. Tzeggai) - initial API and implementation
 ******************************************************************************/
package org.geopublishing.geopublisher;

import org.geopublishing.atlasViewer.AVUtil;
import org.geopublishing.atlasViewer.swing.BugReportmailer;


public class GPBugReportmailer extends BugReportmailer {
	
	public final static String GEOPUBLISHERLOG = "geopublisher.log"; 

	public GPBugReportmailer() {
		super(GEOPUBLISHERLOG);
	}

	@Override
	protected String getBody() {
		return GeopublisherGUI.R("SendLogToAuthor.Email.Body", logFileLocation);
	}

	@Override
	protected String getSubject() {
		return GeopublisherGUI.R("SendLogToAuthor.Email.Subject", AVUtil
				.getVersionInfo());
	}

}