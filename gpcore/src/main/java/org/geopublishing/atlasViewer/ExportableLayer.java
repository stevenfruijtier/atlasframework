/*******************************************************************************
 * Copyright (c) 2010 Stefan A. Tzeggai.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Contributors:
 *     Stefan A. Tzeggai - initial API and implementation
 ******************************************************************************/
package org.geopublishing.atlasViewer;

import java.awt.Component;
import java.io.IOException;

import org.geopublishing.atlasViewer.swing.MapLegend;


/**
 * Objects implementing this Interface can be exported.
 * 
 * @see LayerPaneGroupUI
 * @see MapLegend
 * 
 * @author <a href="mailto:skpublic@wikisquare.de">Stefan Alfons Tzeggai</a>
 * 
 */
public interface ExportableLayer {

	/**
	 * @return true is the Layer can be exported
	 */
	Boolean isExportable();

	/**
	 * Ask the user where to export to and export.
	 * 
	 * @throws IOException
	 */
	void exportWithGUI(Component owner) throws IOException;

}
