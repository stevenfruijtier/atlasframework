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
package org.geopublishing.atlasStyler.swing;

import java.awt.Component;
import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JOptionPane;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.geopublishing.atlasStyler.ASUtil;
import org.geopublishing.atlasStyler.AtlasStyler;
import org.geopublishing.atlasViewer.swing.Icons;
import org.geotools.styling.StyledLayer;

import de.schmitzm.geotools.styling.StyledLayerInterface;
import de.schmitzm.geotools.styling.StylingUtil;
import de.schmitzm.io.IOUtil;
import de.schmitzm.swing.ExceptionDialog;
import de.schmitzm.versionnumber.ReleaseUtil;

/**
 * Action to save the SLD of the {@link StyledLayer} to a {@link File}. Two
 * versions of the SLD are saved. The normal one, and an optimized one for
 * production use. The later can not be properly reimported yet.
 */
public class AtlasStylerSaveLayerToSLDAction extends AbstractAction {
	private static final long serialVersionUID = 4726448851995462364L;

	static private final Logger LOGGER = Logger
			.getLogger(AtlasStylerSaveLayerToSLDAction.class);;

	private final StyledLayerInterface<?> styledLayer;

	private final Component owner;

	private JButton open;

	public AtlasStylerSaveLayerToSLDAction(Component owner,
			StyledLayerInterface<?> styledLayer) {
		super(ASUtil.R("AtlasStylerGUI.saveToSLDFile"), Icons.ICON_EXPORT);
		this.owner = owner;
		this.styledLayer = styledLayer;

		// This action in only enabled if the SLD changed AND a SLD file has
		// been associated AND the file can be saved.
		setEnabled(IOUtil.canWriteOrCreate(styledLayer.getSldFile())
		// && StylingUtil.isStyleDifferent(styledLayer.getStyle(),
		// styledLayer.getSldFile())
		);
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		boolean backup = false;

		if (styledLayer.getSldFile().exists()) {

			// if (StylingUtil.isStyleDifferent(styledLayer.getStyle(),
			// styledLayer.getSldFile())){
			// return;
			// }

			try {
				FileUtils.copyFile(styledLayer.getSldFile(), IOUtil
						.changeFileExt(styledLayer.getSldFile(), "sld.bak"));
				backup = true;
			} catch (IOException e1) {
				LOGGER.warn("could not create a backup of the existing .sld",
						e1);
				return;
			}
		}

		try {
			StylingUtil.saveStyleToSld(styledLayer.getStyle(),
					styledLayer.getSldFile());
			StylingUtil.saveStyleToSld(
					styledLayer.getStyle(),
					ASUtil.changeToOptimizedFilename(styledLayer.getSldFile()),
					true,
					"AtlasStyler "
							+ ReleaseUtil.getVersionInfo(AtlasStyler.class)
							+ ", Layer:" + styledLayer.getTitle()
							+ ", Export-Mode: PRODUCTION");

			Object[] options = { "OK",
					ASUtil.R("AtlasStylerSaveLayerToSLD.OpenFile"),
					ASUtil.R("AtlasStylerSaveLayerToSLD.OpenProductive") };
			int dialogValue = 0;
			if (backup) {
				dialogValue = JOptionPane.showOptionDialog(owner, ASUtil.R(
						"AtlasStylerGUI.saveToSLDFileSuccessAndBackedUp",
						IOUtil.escapePath(styledLayer.getSldFile())), "",
						JOptionPane.YES_NO_OPTION,
						JOptionPane.INFORMATION_MESSAGE, null, options,
						options[0]);
			} else {
				dialogValue = JOptionPane.showOptionDialog(
						owner,
						ASUtil.R("AtlasStylerGUI.saveToSLDFileSuccess",
								IOUtil.escapePath(styledLayer.getSldFile())),
						"", JOptionPane.YES_NO_OPTION,
						JOptionPane.INFORMATION_MESSAGE, null, options,
						options[0]);
			}
			if (dialogValue == JOptionPane.NO_OPTION) {
				Desktop desktop = Desktop.getDesktop();
				desktop.open(styledLayer.getSldFile());
			}
			if (dialogValue == JOptionPane.CANCEL_OPTION) {
				Desktop desktop = Desktop.getDesktop();
				desktop.open(ASUtil.changeToOptimizedFilename(styledLayer
						.getSldFile()));
			}
			List<Exception> es = StylingUtil.validateSld(new FileInputStream(
					styledLayer.getSldFile()));
			if (es.size() > 0) {
				ExceptionDialog.show(
						owner,
						new IllegalStateException(ASUtil.R(
								"AtlasStylerExport.WarningSLDNotValid",
								IOUtil.escapePath(styledLayer.getSldFile())),
								es.get(0)));
			}

		} catch (Exception e1) {
			LOGGER.error("saveStyleToSLD", e1);
			ExceptionDialog.show(owner, e1);
			return;
		}
	}
}
