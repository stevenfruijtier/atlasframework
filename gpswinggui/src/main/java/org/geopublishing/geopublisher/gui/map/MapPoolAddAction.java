/*******************************************************************************
 * Copyright (c) 2010 Stefan A. Tzeggai.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Contributors:
 *     Stefan A. Tzeggai - initial API and implementation
 ******************************************************************************/
package org.geopublishing.geopublisher.gui.map;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import org.geopublishing.atlasViewer.map.Map;
import org.geopublishing.atlasViewer.map.MapPool;
import org.geopublishing.atlasViewer.swing.Icons;
import org.geopublishing.geopublisher.gui.internal.GPDialogManager;
import org.geopublishing.geopublisher.swing.GeopublisherGUI;



public class MapPoolAddAction extends AbstractAction {

	private MapPoolJTable mapPoolJTable;
	private MapPool mapPool;

	public MapPoolAddAction(MapPoolJTable mapPoolJTable) {
		super(GeopublisherGUI.R("MapPoolWindow.Button_AddMap_label"),
				Icons.ICON_ADD_SMALL);

		this.mapPoolJTable = mapPoolJTable;
		this.mapPool = mapPoolJTable.getMapPool();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
//
//		// Setup empty translations
//		Translation name, desc, keywords;
//		List<String> languages = mapPoolJTable.getAce().getLanguages();
//		name = new Translation(languages, Geopublisher
//				.R("MapPool.DummyMapName"));
//		desc = new Translation(languages, Geopublisher
//				.R("MapPool.DummyMapDescription"));
//		keywords = new Translation();
//
//		final TranslationEditJPanel namePanel = new TranslationEditJPanel(
//				Geopublisher.R("MapPreferences_translateTheMapsName"), name,
//				languages);
//		final TranslationEditJPanel descPanel = new TranslationEditJPanel(
//				Geopublisher.R("MapPreferences_translateTheMapsDescription"),
//				desc, languages);
//		final TranslationEditJPanel keywordsPanel = new TranslationEditJPanel(
//				Geopublisher.R("MapPreferences_translateTheMapsKeywords"),
//				desc, languages);
//
//		TranslationAskJDialog translationAskJDialog = new TranslationAskJDialog(
//				mapPoolJTable, namePanel, descPanel, keywordsPanel);
//		translationAskJDialog.setVisible(true);
//		if (translationAskJDialog.isCancelled()) {
//			return;
//		}

		// The map is created and automatically added to the mappool.
		Map newMap = new Map(mapPoolJTable.getAce());
		mapPool.put(newMap);

		if (mapPool.size() == 1) {
			mapPool.setStartMapID(newMap.getId());
		}
		
		mapPoolJTable.select(newMap.getId());
		
		GPDialogManager.dm_EditMapEntry.getInstanceFor(newMap, this.mapPoolJTable, newMap);
		
		// TODO Try to select the new map/row. But how?!

	}

}
