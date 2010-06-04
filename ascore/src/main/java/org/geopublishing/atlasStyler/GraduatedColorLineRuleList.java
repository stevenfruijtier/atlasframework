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
package org.geopublishing.atlasStyler;

import org.apache.log4j.Logger;
import org.geotools.styling.FeatureTypeStyle;

import schmitzm.geotools.feature.FeatureUtil.GeometryForm;
import skrueger.geotools.StyledFeaturesInterface;

public class GraduatedColorLineRuleList extends GraduatedColorRuleList {
	protected Logger LOGGER = ASUtil.createLogger(this);

	public GraduatedColorLineRuleList(StyledFeaturesInterface<?> styledFeatures) {
		super(styledFeatures);
	}
//
//	@Override
//	public SingleRuleList<LineSymbolizer> getDefaultTemplate() {
//		return ASUtil.getDefaultLineTemplate();
//	}
	
	@Override
	public void importTemplate(FeatureTypeStyle importFTS) {
		setTemplate(ASUtil.importLineTemplateFromFirstRule(importFTS));

	}

	@Override
	public RulesListType getTypeID() {
		return RulesListType.QUANTITIES_COLORIZED_LINE;
	}

	@Override
	public GeometryForm getGeometryForm() {
		return GeometryForm.LINE;
	}
}
