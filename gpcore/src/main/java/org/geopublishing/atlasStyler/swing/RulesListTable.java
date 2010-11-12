package org.geopublishing.atlasStyler.swing;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import org.geopublishing.atlasStyler.AbstractRuleList;
import org.geopublishing.atlasStyler.AtlasStyler;

public class RulesListTable extends JTable {

	private static final int COLIDX_NAME = 0;
	public static final int COLIDX_ENABLED = 1;
	private ArrayList<AbstractRuleList> rulesList;
	private final AtlasStyler atlasStyler;

	private PropertyChangeListener updateOnRulesListsListChanges = new PropertyChangeListener() {

		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			((DefaultTableModel) getModel()).fireTableStructureChanged();
		}
	};

	public RulesListTable(AtlasStyler atlasStyler) {
		this.atlasStyler = atlasStyler;
		rulesList = atlasStyler.getRuleLists();
		setModel(new RulesListTableModel());

		atlasStyler.getRuleLists().addListener(updateOnRulesListsListChanges);
	}

	class RulesListTableModel extends DefaultTableModel {

		@Override
		public int getRowCount() {
			return rulesList.size();
		}

		@Override
		public int getColumnCount() {
			return 2;
		}

		@Override
		public String getColumnName(int column) {

			switch (column) {
			case COLIDX_NAME:
				return "name"; // i8n
			case COLIDX_ENABLED:
				return "enabled"; // i8n
			default:
				return super.getColumnName(column);
			}
		}

		@Override
		public Object getValueAt(int row, int column) {

			switch (column) {
			case COLIDX_NAME:
				return rulesList.get(row).getClass().getSimpleName();
			case COLIDX_ENABLED:
				return rulesList.get(row).isEnabled();
			default:
				return super.getValueAt(row, column);
			}
		}

		@Override
		public boolean isCellEditable(int row, int column) {
			switch (column) {
			case COLIDX_ENABLED:
				return true;
			default:
				return false;
			}
		}

		@Override
		public Class<?> getColumnClass(int column) {

			switch (column) {
			case COLIDX_NAME:
				return String.class;
			case COLIDX_ENABLED:
				return Boolean.class;
			default:
				return super.getColumnClass(column);
			}
		}

		@Override
		public void setValueAt(Object aValue, int row, int column) {
			switch (column) {
			case COLIDX_ENABLED:
				rulesList.get(row).setEnabled(!rulesList.get(row).isEnabled());
				return;
			default:
				return;
			}
		}

	}

}
