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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;

import net.miginfocom.swing.MigLayout;

import org.apache.log4j.Logger;
import org.geopublishing.atlasStyler.ASUtil;
import org.geopublishing.atlasStyler.AbstractRuleList;
import org.geopublishing.atlasStyler.AtlasStyler;
import org.geopublishing.atlasStyler.AtlasStyler.LANGUAGE_MODE;
import org.geopublishing.atlasStyler.SingleRuleList;
import org.geopublishing.atlasStyler.StyleChangeListener;
import org.geopublishing.atlasStyler.StyleChangedEvent;
import org.geopublishing.atlasStyler.TextRuleList;
import org.geopublishing.atlasViewer.swing.AVSwingUtil;
import org.geotools.data.DefaultQuery;
import org.geotools.feature.FeatureCollection;
import org.geotools.styling.TextSymbolizer;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.Filter;

import schmitzm.lang.LangUtil;
import schmitzm.swing.JPanel;
import schmitzm.swing.SwingUtil;
import skrueger.i8n.LanguagesComboBox;
import skrueger.swing.CancellableDialogAdapter;
import skrueger.swing.ThinButton;

/**
 * This GUI defines all the buttons to define the {@link TextSymbolizer}
 * 
 * @author SK
 */
public class TextRuleListGUI extends JPanel {
	protected Logger LOGGER = ASUtil.createLogger(this);

	private JCheckBox jCheckBoxEnabled = null;

	private JPanel jPanelClass = null;

	private JComboBox jComboBoxClass = null;

	private JCheckBox jCheckBoxClassEnabled = null;

	private JButton jButtonClassAdd = null;

	private JButton jButtonClassDelete = null;

	private JButton jButtonClassRename = null;

	private JButton jButtonClassFromSymbols = null;

	private TextSymbolizerEditGUI jPanelEditTextSymbolizer = null;

	private final TextRuleList rulesList;

	private final AtlasStyler atlasStyler;

	private ThinButton jButtonClassLangCopy;

	/**
	 * This is the default constructor
	 */
	public TextRuleListGUI(final TextRuleList rulesList,
			final AtlasStyler atlasStyler) {
		this.rulesList = rulesList;
		this.atlasStyler = atlasStyler;
		rulesList.addDefaultClass();

		// Create components
		initialize();
		// Set the componets according to the presentation oin the Style
		updateGUI();

		atlasStyler.addListener(new StyleChangeListener() {

			public void changed(StyleChangedEvent e) {
				jButtonClassFromSymbols.setEnabled(!(atlasStyler
						.getLastChangedRuleList() instanceof SingleRuleList));
			}

		});
		jButtonClassFromSymbols.setEnabled(!(atlasStyler
				.getLastChangedRuleList() instanceof SingleRuleList));

	}

	/**
	 * Called when the text symbolizer CLASS has been changed.
	 */
	protected void updateGUI() {

		// The new symbolizer to present
		getJPanelEditTextSymbolizer().updateGui(rulesList.getSymbolizer());

		// Update the tool-tip
		jComboBoxClass.setToolTipText("<html>"
				+ rulesList.getClassFilter(
						getJComboBoxClass().getSelectedIndex()).toString()
				+ "</html>");

		/***********************************************************************
		 * Class management buttons
		 */
		jButtonClassDelete
				.setEnabled(getJComboBoxClass().getSelectedIndex() != 0);
		jButtonClassRename
				.setEnabled(getJComboBoxClass().getSelectedIndex() != 0);

		// Change the state of the class enabled CB
		jCheckBoxClassEnabled.setSelected(rulesList.isClassEnabled(rulesList
				.getSelIdx()));
		reactToClassEnabledChange();
	}

	/**
	 * This method initializes this
	 * 
	 * @param features
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setLayout(new MigLayout("wrap 1, gap 1, inset 1, top", "grow"));
		this.add(getJCheckBoxEnabled(), "top");
		this.add(getJPanelClass());
	}

	/**
	 * This method initializes jCheckBox
	 * 
	 * @return javax.swing.JCheckBox
	 */
	private JCheckBox getJCheckBoxEnabled() {
		if (jCheckBoxEnabled == null) {
			jCheckBoxEnabled = new JCheckBox();
			jCheckBoxEnabled.setAction(new AbstractAction(AtlasStyler
					.R("TextRulesList.Labels.Checkbox")) {

				public void actionPerformed(ActionEvent e) {
					rulesList.setEnabled(jCheckBoxEnabled.isSelected());
					TextRuleListGUI.this.setEnabled(jCheckBoxEnabled
							.isSelected());
					jCheckBoxEnabled.setEnabled(true); // THIS CB has to stay
														// enabled
					reactToClassEnabledChange();
				}

			});
			jCheckBoxEnabled.setSelected(rulesList.isEnabled());
			TextRuleListGUI.this.setEnabled(jCheckBoxEnabled.isSelected());
			jCheckBoxEnabled.setEnabled(true); // THIS CB has to stay enabled
			reactToClassEnabledChange();
		}
		return jCheckBoxEnabled;
	}

	/**
	 * This method initializes jPanel
	 * 
	 * @param features
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelClass() {
		if (jPanelClass == null) {

			jPanelClass = new JPanel(new MigLayout("nogrid, gap 1, inset 1"));

			jPanelClass.add(new JLabel(AtlasStyler
					.R("TextRulesList.Labelclass") + ":"));
			jPanelClass.add(getJComboBoxClass());
			jPanelClass.add(getJCheckBoxClassEnabled(), "wrap");

			// jPanelClass.add(getJButtonClassAdd());
			jPanelClass.add(getJButtonClassDelete());
			jPanelClass.add(getJButtonClassRename());

			// Only in GP mode
			if (atlasStyler.getLanguageMode() == LANGUAGE_MODE.ATLAS_MULTILANGUAGE)
				jPanelClass.add(getJButtonClassCopyToLanguage());

			jPanelClass.add(getJButtonClassFromSymbols(), "wrap");

			// jPanelClass.add(getJPanelLabelDefinition(), "wrap");
			jPanelClass.add(getJPanelEditTextSymbolizer());
		}
		return jPanelClass;
	}

	/**
	 * This method initializes jComboBox
	 * 
	 * @return javax.swing.JComboBox
	 */
	private JComboBox getJComboBoxClass() {
		if (jComboBoxClass == null) {
			jComboBoxClass = new JComboBox();
			jComboBoxClass.setModel(new DefaultComboBoxModel(rulesList
					.getRuleNames().toArray()));

			jComboBoxClass.setSelectedIndex(0);

			SwingUtil.addMouseWheelForCombobox(jComboBoxClass, false);

			jComboBoxClass.setRenderer(new DefaultListCellRenderer() {
				@Override
				public Component getListCellRendererComponent(JList list,
						Object value, int index, boolean isSelected,
						boolean cellHasFocus) {

					JComponent superT = (JComponent) super
							.getListCellRendererComponent(list, value, index,
									isSelected, cellHasFocus);

					if (index >= 0 && index < rulesList.countClasses())
						superT.setToolTipText("<html>"
								+ rulesList.getClassFilter(index).toString()
								+ "</html>");
					else
						superT.setToolTipText(null);

					return superT;
				}
			});

			jComboBoxClass.addItemListener(new ItemListener() {

				public void itemStateChanged(ItemEvent e) {
					if (e.getStateChange() == ItemEvent.SELECTED) {
						rulesList.setSelIdx(getJComboBoxClass()
								.getSelectedIndex());

						// Update the tool-tip
						getJComboBoxClass().setToolTipText(
								"<html>"
										+ rulesList.getClassFilter(
												getJComboBoxClass()
														.getSelectedIndex())
												.toString() + "</html>");

						fireClassChanged();
					}
				}
			});
		}
		return jComboBoxClass;
	}

	/**
	 * This method initializes jCheckBox
	 * 
	 * @return javax.swing.JCheckBox
	 */
	private JCheckBox getJCheckBoxClassEnabled() {
		if (jCheckBoxClassEnabled == null) {
			jCheckBoxClassEnabled = new JCheckBox(
					AtlasStyler.R("TextRulesList.Labelclass.Checkbox"));

			jCheckBoxClassEnabled.setSelected(rulesList
					.isClassEnabled(rulesList.getSelIdx()));
			reactToClassEnabledChange();

			jCheckBoxClassEnabled.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					rulesList.setClassEnabled(rulesList.getSelIdx(),
							jCheckBoxClassEnabled.isSelected());
					reactToClassEnabledChange();
				}
			});

		}
		return jCheckBoxClassEnabled;
	}

	private void reactToClassEnabledChange() {
		getJPanelEditTextSymbolizer().setEnabled(
				getJCheckBoxClassEnabled().isSelected()
						&& getJCheckBoxEnabled().isSelected());
	}

	/**
	 * This method initializes jButton
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getJButtonClassAdd() {
		if (jButtonClassAdd == null) {
			jButtonClassAdd = new ThinButton(
					AtlasStyler.R("TextRulesList.Labelclass.Action.Add"));
			// jButtonClassAdd.setFont(jButtonClassAdd.getFont().deriveFont(
			// AtlasStylerTabbedPane.BUTTON_FONT_STYLE,
			// AtlasStylerTabbedPane.BUTTON_FONT_SIZE));
			jButtonClassAdd.setToolTipText(AtlasStyler
					.R("TextRulesList.Labelclass.Action.Add.TT"));
			jButtonClassAdd.setEnabled(false);
		}
		return jButtonClassAdd;
	}

	/**
	 * This method initializes jButton
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getJButtonClassDelete() {
		if (jButtonClassDelete == null) {
			jButtonClassDelete = new ThinButton(
					AtlasStyler.R("TextRulesList.Labelclass.Action.Delete"));

			jButtonClassDelete.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e) {
					int idx = rulesList.getSelIdx();

					rulesList.removeClass(idx);

					rulesList.setSelIdx(idx - 1);

					getJComboBoxClass().setModel(
							new DefaultComboBoxModel(rulesList.getRuleNames()
									.toArray()));

					fireClassChanged();
				}

			});

			jButtonClassDelete.setToolTipText(AtlasStyler
					.R("TextRulesList.Labelclass.Action.Delete.TT"));
		}
		return jButtonClassDelete;
	}

	private void fireClassChanged() {
		// rulesList.pushQuite();
		updateGUI();
		// rulesList.popQuite(null);
	}

	/**
	 * This button will copy the selected symbolizer and ask the use to create a
	 * language specific version of it. This button is only available in
	 * AtlasLanguage mode.
	 */
	private JButton getJButtonClassCopyToLanguage() {
		if (jButtonClassLangCopy == null) {
			jButtonClassLangCopy = new ThinButton("Default for langauge");
			jButtonClassLangCopy.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e) {

					// String lang = ASUtil.askForString(TextRuleListGUI.this,
					// "de", null);

					String lang = askForLang();

					if (lang != null) {

						lang = lang.toLowerCase();

						if (!AtlasStyler.getLanguages().contains(lang)) {
							AVSwingUtil
									.showMessageDialog(
											TextRuleListGUI.this,
											"Please enter one of the configured languages: "
													+ LangUtil
															.stringConcatWithSep(
																	",",
																	AtlasStyler
																			.getLanguages())); // i8n
							return;
						}

						int newIdx = rulesList.addDefaultClass(lang);

						if (newIdx < 0) {
							// alreadylexisted?
							AVSwingUtil
									.showMessageDialog(TextRuleListGUI.this,
											"Class could could not be created. Maybe the class alreay exits?"); // i8n
							return;
						} else
							rulesList.setSelIdx(newIdx);

						getJComboBoxClass().setModel(
								new DefaultComboBoxModel(rulesList
										.getRuleNames().toArray()));

						getJComboBoxClass().setSelectedIndex(
								rulesList.getSelIdx());

						// updateGUI();
					}
				}

				private String askForLang() {

					if (AtlasStyler.getLanguages().size() == rulesList
							.getDefaultLanguages().size()) {
						AVSwingUtil.showMessageDialog(TextRuleListGUI.this,
								"A rule for every language already exits.");
						return null;

					}

					final LanguagesComboBox lcb = new LanguagesComboBox(
							AtlasStyler.getLanguages(), rulesList
									.getDefaultLanguages());

					CancellableDialogAdapter d = new CancellableDialogAdapter(
							TextRuleListGUI.this) {

						@Override
						protected void dialogInit() {
							super.dialogInit();
							setLayout(new MigLayout());
							add(lcb);
							add(getOkButton());
							pack();
							SwingUtil.setRelativeFramePosition(this,
									TextRuleListGUI.this, 0.5, 0.5);

						}

						@Override
						public boolean close() {
							if (lcb.getSelectedIndex() == -1)
								return false;
							return super.close();
						}

						@Override
						public void cancel() {
						}

					};

					d.setModal(true);
					d.setVisible(true);

					return lcb.getSelectedLanguage();
				}
			});
		}
		return jButtonClassLangCopy;

	}

	/**
	 * This button allows to rename a CLASS
	 */
	private JButton getJButtonClassRename() {
		if (jButtonClassRename == null) {
			jButtonClassRename = new ThinButton(
					AtlasStyler.R("TextRulesList.Labelclass.Action.Rename"));
			jButtonClassRename.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e) {

					String ruleName = rulesList.getRuleName();
					String result = ASUtil.askForString(TextRuleListGUI.this,
							ruleName, null);
					if (result != null) {
						// We do not check if the name exists twice, because it
						// wouldn't really matter
						rulesList.getRuleNames().set(rulesList.getSelIdx(),
								result);
						getJComboBoxClass().setModel(
								new DefaultComboBoxModel(rulesList
										.getRuleNames().toArray()));
						getJComboBoxClass().setSelectedIndex(
								rulesList.getSelIdx());
					}
				}
			});
		}
		return jButtonClassRename;
	}

	/**
	 * This method initializes jButton
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getJButtonClassFromSymbols() {
		if (jButtonClassFromSymbols == null) {
			jButtonClassFromSymbols = new ThinButton(
					AtlasStyler
							.R("TextRulesList.Labelclass.Action.LoadClassesFromSymbols"));

			jButtonClassFromSymbols.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e) {
					AbstractRuleList symbRL = atlasStyler
							.getLastChangedRuleList();

					rulesList.importClassesFromStyle(symbRL,
							TextRuleListGUI.this);

					getJComboBoxClass().setModel(
							new DefaultComboBoxModel(rulesList.getRuleNames()
									.toArray()));
					getJComboBoxClass().setSelectedIndex(0);
				}
			});

			jButtonClassFromSymbols
					.setToolTipText(AtlasStyler
							.R("TextRulesList.Labelclass.Action.LoadClassesFromSymbols.TT"));
		}
		return jButtonClassFromSymbols;
	}

	/**
	 * This method initializes jPanel
	 * 
	 * @param features
	 * 
	 * @return javax.swing.JPanel
	 */
	private TextSymbolizerEditGUI getJPanelEditTextSymbolizer() {
		if (jPanelEditTextSymbolizer == null) {

			FeatureCollection<SimpleFeatureType, SimpleFeature> features = getPreviewFeatures();

			jPanelEditTextSymbolizer = new TextSymbolizerEditGUI(rulesList,
					atlasStyler, features);
			// jPanelEditTextSymbolizer.setBorder(BorderFactory
			// .createTitledBorder(AtlasStyler
			// .R("TextRuleListGUI.TextStyling.BorderTitle")));
		}
		return jPanelEditTextSymbolizer;
	}

	FeatureCollection<SimpleFeatureType, SimpleFeature> features;

	/**
	 * Caches the limited number of features for the preview in memory.
	 */
	private FeatureCollection<SimpleFeatureType, SimpleFeature> getPreviewFeatures() {
		if (features == null) {
			try {

				LOGGER.debug("Putting 100 random features into the preview");
				Filter filter = rulesList.getClassFilter(0) != null ? rulesList
						.getClassFilter(0) : Filter.INCLUDE;

				features = rulesList
						.getStyledFeatures()
						.getFeatureSource()
						.getFeatures(
								new DefaultQuery(rulesList.getStyledFeatures()
										.getSchema().getTypeName(), filter,
										100, null, "max 100 sample features"));

				if (features.size() == 0 && filter != Filter.INCLUDE) {
					LOGGER.info("Getting preview features for the filter "
							+ filter
							+ " failed, getting preview features without a filter!");

					features = rulesList
							.getStyledFeatures()
							.getFeatureSource()
							.getFeatures(
									new DefaultQuery(rulesList
											.getStyledFeatures().getSchema()
											.getTypeName(), Filter.INCLUDE,
											100, null,
											"max 100 sample features"));
				}

			} catch (IOException e) {
				LOGGER.error(
						"Sample features for TextSymbolizer Preview could not be requested:",
						e);
				throw new RuntimeException(
						"Sample features for TextSymbolizer Preview could not be requested",
						e);
			}
		}
		return features;
	}

	//
	// /**
	// * This method initializes jButton
	// *
	// * @return javax.swing.JButton
	// */
	// private JButton getJButton() {
	// if (jButtonPlacement == null) {
	// jButtonPlacement = new JButton();
	// jButtonPlacement.setFont(jButtonPlacement.getFont().deriveFont(
	// AtlasStylerTabbedPane.BUTTON_FONT_STYLE,
	// AtlasStylerTabbedPane.BUTTON_FONT_SIZE));
	//
	// LabelPlacement labelPlacement =
	// rulesList.getSymbolizers().get(0).getLabelPlacement();
	// PointPlacement pPlacement;
	// LinePlacement linePlacement;
	//
	// linePlacement.set
	//
	// AnchorPoint ap = StylingUtil.STYLE_BUILDER.createAnchorPoint(10., 10.);
	// pPlacement.setAnchorPoint(ap);
	//
	// jButtonPlacement.setEnabled(false);
	// }
	// return jButtonPlacement;
	// }

	// /**
	// * This method initializes jButton
	// *
	// * @return javax.swing.JButton
	// */
	// private JButton getJButtonScaleRange() {
	// if (jButtonScaleRange == null) {
	// jButtonScaleRange = new JButton(AtlasStyler
	// .R("TextRulesList.Button.ScaleRange"));
	// jButtonScaleRange.setFont(jButtonScaleRange.getFont().deriveFont(
	// AtlasStylerTabbedPane.BUTTON_FONT_STYLE,
	// AtlasStylerTabbedPane.BUTTON_FONT_SIZE));
	// jButtonScaleRange.setEnabled(false);
	// }
	// return jButtonScaleRange;
	// }

}
