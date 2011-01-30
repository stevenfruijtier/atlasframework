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
package org.geopublishing.geopublisher.gui.export;

import java.awt.Component;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JCheckBox;
import javax.swing.JLabel;

import net.miginfocom.swing.MigLayout;

import org.geopublishing.geopublisher.GPProps;
import org.geopublishing.geopublisher.GPProps.Keys;
import org.geopublishing.geopublisher.swing.GeopublisherGUI;
import org.netbeans.spi.wizard.WizardPage;

public class ExportWizardPage_DiskJwsSelection extends WizardPage {
    private final String validationJwsOrDiskFailedMsg = GeopublisherGUI
            .R("ExportWizard.JwsOrDisk.ValidationError");

    JLabel explanationJLabel = new JLabel(
            GeopublisherGUI.R("ExportWizard.JwsOrDisk.Explanation"));
    JLabel explanationJwsJLabel = new JLabel(
            GeopublisherGUI.R("ExportWizard.JwsOrDisk.Explanation.Jws"));
    JLabel explanationDiskJLabel = new JLabel(
            GeopublisherGUI.R("ExportWizard.JwsOrDisk.Explanation.Disk"));
    JLabel explanationFtpJLabel = new JLabel(
            GeopublisherGUI.R("ExportWizard.Ftp.Explanation.Ftp"));
    JCheckBox diskJCheckbox;
    JCheckBox jwsJCheckbox;
    JCheckBox ftpJCheckbox;

    private JCheckBox diskJCheckboxZip;

    public static String getDescription() {
        return GeopublisherGUI.R("ExportWizard.JwsOrDisk");
    }

    public ExportWizardPage_DiskJwsSelection() {
        initGui();
    }

    @Override
    protected String validateContents(final Component component,
            final Object event) {
        if (!getJwsJCheckbox().isSelected() && !getDiskJCheckbox().isSelected()
                && !getFtpJCheckbox().isSelected())
            return validationJwsOrDiskFailedMsg;
        return null;
    }

    private void initGui() {
        setSize(ExportWizard.DEFAULT_WPANEL_SIZE);
        setPreferredSize(ExportWizard.DEFAULT_WPANEL_SIZE);

        setLayout(new MigLayout("wrap 1"));
        add(explanationJLabel);
        add(getDiskJCheckbox(), "split 2, gapy unrelated");
        add(getDiskZipJCheckbox(), "");
        add(explanationDiskJLabel);
        add(getJwsJCheckbox(), "gapy unrelated");
        add(explanationJwsJLabel);
        add(getFtpJCheckbox(), "gapy unrelated");
        add(explanationFtpJLabel);

    }

    private JCheckBox getJwsJCheckbox() {
        if (jwsJCheckbox == null) {
            jwsJCheckbox = new JCheckBox(
                    GeopublisherGUI.R("ExportWizard.JwsOrDisk.JwsCheckbox"));
            jwsJCheckbox.setName(ExportWizard.JWS_CHECKBOX);
            jwsJCheckbox.setSelected(GPProps.getBoolean(Keys.LastExportJWS));

        }
        return jwsJCheckbox;
    }

    private JCheckBox getFtpJCheckbox() {
        if (ftpJCheckbox == null) {
            ftpJCheckbox = new JCheckBox(
                    GeopublisherGUI.R("ExportWizard.Ftp.FtpCheckbox"));
            ftpJCheckbox.setName(ExportWizard.FTP_CHECKBOX);
            ftpJCheckbox.setSelected(GPProps.getBoolean(Keys.LastExportFtp));
        }
        return ftpJCheckbox;
    }

    private JCheckBox getDiskJCheckbox() {
        if (diskJCheckbox == null) {
            diskJCheckbox = new JCheckBox(
                    GeopublisherGUI.R("ExportWizard.JwsOrDisk.DiskCheckbox"));
            diskJCheckbox.setName(ExportWizard.DISK_CHECKBOX);
            diskJCheckbox.setSelected(GPProps.getBoolean(Keys.LastExportDisk));

            getDiskJCheckbox().addItemListener(new ItemListener() {

                @Override
                public void itemStateChanged(ItemEvent e) {
                    getDiskZipJCheckbox().setEnabled(
                            getDiskJCheckbox().isSelected());
                }
            });
        }
        return diskJCheckbox;
    }

    private JCheckBox getDiskZipJCheckbox() {
        if (diskJCheckboxZip == null) {
            diskJCheckboxZip = new JCheckBox(
                    GeopublisherGUI.R("ExportWizard.JwsOrDisk.DiskZipCheckbox"));
            diskJCheckboxZip.setName(ExportWizard.DISKZIP_CHECKBOX);
            diskJCheckboxZip.setSelected(GPProps.getBoolean(
                    Keys.LastExportDiskZipped, true));
            diskJCheckboxZip.setEnabled(getDiskJCheckbox().isSelected());

        }
        return diskJCheckboxZip;
    }

}
