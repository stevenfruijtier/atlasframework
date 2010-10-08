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

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JMenuBar;
import javax.swing.UIDefaults;
import javax.swing.UIManager;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.geopublishing.atlasViewer.dp.DataPool;
import org.geopublishing.atlasViewer.dp.DpEntry;
import org.geopublishing.atlasViewer.dp.Group;
import org.geopublishing.atlasViewer.map.Map;
import org.geopublishing.atlasViewer.map.MapPool;
import org.geopublishing.geopublisher.GpUtil;

import rachel.ResourceLoader;
import rachel.loader.ResourceLoaderManager;
import schmitzm.jfree.chart.style.ChartStyle;
import skrueger.i8n.I8NUtil;
import skrueger.i8n.Translation;
import skrueger.swing.Disposable;

/**
 * The whole configuration for one Atlas project
 * 
 * @author Stefan Alfons Tzeggai
 */
public class AtlasConfig implements Disposable {
	private static final Logger LOGGER = Logger.getLogger(AtlasConfig.class);

	{
		// http://forum.java.sun.com/thread.jspa?threadID=217692&messageID=1946056
		UIManager.getDefaults().put(
				"Button.focusInputMap",
				new UIDefaults.LazyInputMap(new Object[] { "ENTER", "pressed",
						"released ENTER", "released", "SPACE", "pressed",
						"released SPACE", "released" }));
	}

	/**
	 * A list of fonts added manually to the atlas by the user
	 */
	private final List<Font> fonts = new ArrayList<Font>();

	/**
	 * Resource name of the icon that will be used for JavaWebStart if the
	 * user-defined one can't be found.
	 */
	public static final String JWSICON_RESOURCE_NAME_FALLBACK = "/export/jws/icon.gif";

	// /**
	// * If true, then the AtlasViewer classes will try to not cache files that
	// * could be changed during runtime, like SLDs
	// */
	// boolean noCachingMode = false;

	/**
	 * Resource name of the map emblem that will be shown hovering above the map
	 * in the lower right corner *
	 */
	public static final String MAPICON_RESOURCE_NAME = "ad/map_logo.png";

	/**
	 * Resource name of the icon that will be used for the About-Dialog and
	 * JavaWebStart (Attention: autostart.inf uses .ico only)
	 */
	public static final String JWSICON_RESOURCE_NAME = "ad/icon.gif";

	/**
	 * Resource name of the splashscreen image that will be used for
	 * JavaWebStart and start.bat. It must be stores. It's the atlases
	 * spashscreen. If it doesn't exist, the fallback is used.
	 */
	public static final String SPLASHSCREEN_RESOURCE_NAME = "ad/splashscreen.png";

	/** The name of the directory containing the "About"- HTML pages **/
	public static final String ABOUT_DIRNAME = "about";

	/** The name of the directory containing the images used in HTML pages **/
	public static final String IMAGES_DIRNAME = "images";

	/**
	 * Name of the directory relative to {@value #ATLASDATA_DIRNAME} that
	 * contains HTML-folders for every map
	 **/
	public static final String HTML_DIRNAME = "html";

	/**
	 * Name of the directory relative to {@value #ATLASDATA_DIRNAME} that
	 * contains data-folders for every {@link DpEntry}
	 **/
	public static final String DATA_DIRNAME = "data";

	/**
	 * The name of the directory relative to the .gpa file that conatins atlas
	 * data. Usually 'ad'
	 **/
	public static final String ATLASDATA_DIRNAME = "ad";

	/**
	 * The name of the directory relative to the #ATLASDATA_DIRNAME which
	 * contains extra fonts
	 **/
	public static final String FONTS_DIRNAME = "fonts";

	/**
	 * atlas.xml
	 **/
	public static final String ATLAS_XML_FILENAME = "atlas.xml";

	/** This is THE instance of the {@link ResourceLoaderManager} */
	protected ResourceLoaderManager resLoMan = new ResourceLoaderManager();

	/** Name of a file in the ad-folder that stores the atlas default CRS **/
	public static String DEFAULTCRS_FILENAME = "defaultcrs.prj";

	private Translation title = new Translation();

	private Translation desc = new Translation();

	private Translation creator = new Translation();

	private Translation copyright = new Translation();

	/**
	 * TODO Think about the version field. At the moment it is used nowhere, but
	 * increased by 0.1 very time we save.
	 */
	private Float atlasversion = 0f;

	/**
	 * A HashMap with all the defined {@link Map}s, identified by their IDs.
	 */
	private MapPool mapPool = new MapPool();

	/**
	 * A HashMap with all the defined {@link DpEntry}, identified by their IDs.
	 */
	private DataPool datapool = new DataPool();

	/**
	 * List of LanguageCodes that this Atlas is supposed to "speak". Consistency
	 * with all description-fields will be checked during export.
	 */
	private List<String> languages = new LinkedList<String>();

	/**
	 * Root-group of the "Topics-tree", defined by < aml:group > tags The
	 * subgroup called "menubar" will be parsed as {@link JMenuBar} in the
	 * AtlasViewer.
	 */
	private Group firstGroup = new Group(this, true);

	private String resourceBasename = ATLASDATA_DIRNAME + "/" + DATA_DIRNAME
			+ "/";

	private AVProps avprops;

	/**
	 * An readily instantiated DatapoolEntry is added...
	 * 
	 * @param entry
	 *            {@link DpEntry}-subclass. Must not be <code>null</code>
	 * @author <a href="mailto:skpublic@wikisquare.de">Stefan Alfons Tzeggai</a>
	 */
	public void add(DpEntry<? extends ChartStyle> entry) {
		datapool.add(entry);
	}

	/**
	 * Returns the translated name of the Atlas
	 */
	public final Translation getTitle() {
		return title;
	}

	/**
	 * Set the {@link Translation} of the name of the atlas
	 */
	public void setTitle(Translation title) {
		this.title = title;
	}

	public Float getAtlasversion() {
		return atlasversion;
	}

	public void setAtlasversion(Float atlasversion) {
		this.atlasversion = atlasversion;
	}

	public void setMapPool(MapPool mapPool) {
		this.mapPool = mapPool;
	}

	public void setDatapool(DataPool datapool) {
		this.datapool = datapool;
	}

	/**
	 * Returns a {@link DataPool} {@link HashMap} (ID-> {@link DpEntry})
	 */
	public DataPool getDataPool() {
		return datapool;
	}

	/**
	 * Reset the {@link List} of supported Languages to the passed
	 * {@link String}
	 * 
	 * @param langs
	 *            new {@link List} of lang codes
	 */
	public void setLanguages(String... langs) {
		if (langs.length > 0)
			languages.clear();

		for (String code : langs) {
			code = code.trim();
			if (I8NUtil.isValidISOLangCode(code)) {
				LOGGER.debug("Atlas " + getTitle() + " set up to support "
						+ code);
				languages.add(code);
			} else
				throw new IllegalArgumentException("The ISO Language code '"
						+ code + "' is not known/valid." + "\nIgnoring it.");
		}

	}

	/**
	 * @return List of enabled languages. Strings are ISO language code (2
	 *         letter)
	 */
	public final List<String> getLanguages() {
		return languages;
	}

	/**
	 * Returns a {@link MapPool} {@link HashMap} (ID-> {@link Map})
	 */

	public final MapPool getMapPool() {
		return mapPool;
	}

	/**
	 * Returns the root/first group of the "groups-tree" which contains
	 * references to {@link DpEntry}s or sub groups *
	 */
	public Group getFirstGroup() {
		return firstGroup;
	}

	/**
	 * Sets the root/first group of the "groups-tree" which can contain
	 * references to {@link DpEntry}s or sub groups *
	 */
	public void setFirstGroup(Group firstGroup) {
		this.firstGroup = firstGroup;
		this.firstGroup.setAtlasRoot(true);
	}

	/**
	 * @return the {@link ResourceLoaderManager} that unites the
	 *         {@link ResourceLoader}s for this application
	 */
	public ResourceLoaderManager getResLoMan() {
		return resLoMan;
	}

	public Translation getDesc() {
		return desc;
	}

	public void setDesc(Translation desc) {
		this.desc = desc;
	}

	public Translation getCreator() {
		return creator;
	}

	public void setCreator(Translation creator) {
		this.creator = creator;
	}

	public Translation getCopyright() {
		return copyright;
	}

	public void setCopyright(Translation copyright) {
		this.copyright = copyright;
	}

	/**
	 * Copies bytes from the URL <code>source</code> to a file
	 * <code>destination</code>. The directories up to <code>destination</code>
	 * will be created if they don't already exist. <code>destination</code>
	 * will be overwritten if it already exists.
	 * 
	 * If an IO error occurs during copying it is not thrown!
	 */
	public static void exportURLtoFileNoEx(URL source, File dest) {
		try {
			FileUtils.copyURLToFile(source, dest);
		} catch (IOException e) {
		}
	}

	//
	// /**
	// * If true, then the AtlasViewer classes will try not to cache files that
	// * could be changed during runtime, like SLDs, .prj, ...
	// * @Deprecated Use ACE.uncache()
	// *
	// */
	// public boolean isNoCachingMode() {
	// return noCachingMode;
	// }
	//
	// /**
	// * If true, then the AtlasViewer classes will try not to cache files that
	// * could be changed during runtime, like SLDs
	// * @Deprecated Use ACE.uncache()
	// */
	// public void setNoCachingMode(boolean noCachingMode) {
	// this.noCachingMode = noCachingMode;
	// }

	/**
	 * @return Default "ad/data/"
	 */
	public String getResouceBasename() {
		return resourceBasename;
	};

	/**
	 * Change the basename of any datapool resource in the Atlas. Main reason
	 * for this method is the JUnit test scenario. Is this method is not called,
	 * <code>ad/data/</code> will be the default.
	 * 
	 * @param baseName
	 */
	public void setResouceBasename(String baseName) {
		resourceBasename = baseName;
	};

	/**
	 * @return {@link URL} to the HTML About Document for the active language
	 */
	public URL getAboutHTMLURL() {

		// TODO dangerouse redundant paths here.. see ACE.getAboutDir()

		String location = ATLASDATA_DIRNAME + "/" + HTML_DIRNAME + "/"
				+ ABOUT_DIRNAME + "/about_" + Translation.getActiveLang()
				+ ".html";
		URL url = getResLoMan().getResourceAsUrl(location);
		LOGGER.debug("AboutHTML URL = " + url + " for location = " + location);
		return url;
	}

	/**
	 * @return {@link URL} to the HTML Popup Document for the active language
	 */
	public URL getPopupHTMLURL() {

		// TODO dangerous redundant paths here.. see ACE.getAboutDir()

		String location = "ad/html/about/popup_" + Translation.getActiveLang()
				+ ".html";
		URL url = getResLoMan().getResourceAsUrl(location);
		LOGGER.debug("PopupHTML URL = " + url + " for location = " + location);
		return url;
	}

	/**
	 * @return <code>null</code> if no icon found. Otherwise an {@link URL} to a
	 *         user-defined- or default icon.
	 */
	public URL getIconURL() {
		URL iconURL = getResLoMan().getResourceAsUrl(JWSICON_RESOURCE_NAME);
		if (iconURL == null)
			iconURL = GpUtil.class.getResource(JWSICON_RESOURCE_NAME_FALLBACK);
		return iconURL;
	}

	public AVProps getProperties() {
		if (avprops == null) {
			avprops = new AVProps(this);
		}
		return avprops;
	}

	public URL getResource(String resourceLocation) {
		return getResLoMan().getResourceAsUrl(resourceLocation);
	}

	public InputStream getResourceAsStream(String resourceLocation) {
		return getResLoMan().getResourceAsStream(resourceLocation);
	}

	/**
	 * Determines if this is an atlas dir by looking if a ./ad/atlas.xml file
	 * exists
	 * 
	 * @param atlasDir
	 *            A {@link File} folder to check
	 * @return true if this looks like an atlasDir
	 */
	public static boolean isAtlasDir(File atlasDir) {
		if (!new File(atlasDir, AtlasConfig.ATLASDATA_DIRNAME + "/"
				+ AtlasConfig.ATLAS_XML_FILENAME).exists())
			return false;
		return true;
	}

	@Override
	public String toString() {
		String str = "";
		if (getTitle() != null)
			str += getTitle().toString();
		if (getDesc() != null)
			str += ", desc = " + getDesc().toString();
		return str;
	}

	public void uncache() {
		/**
		 * First uncache all Styles
		 */
		for (DpEntry<? extends ChartStyle> dpe : getDataPool().values()) {
			dpe.uncache();
		}

		for (Map map : getMapPool().values()) {
			map.uncache(null);
		}
	}

	public void dispose() {
		uncache();
	}

	/**
	 * A {@link List} of fonts added manually to the atlas by the user. When you
	 * change it call {@link #registerFonts()}
	 */
	public List<Font> getFonts() {
		return fonts;
	}

	/**
	 * Registers the Fonts in {@link #getFonts()} to the system, if they are not
	 * registered yet.
	 */
	public void registerFonts() {
		for (Font f : getFonts()) {
			// if (Font.decode(f.getName()) == null) {
			LOGGER.debug("Registering user font " + f);
			GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(f);
			org.geotools.renderer.style.FontCache fc = org.geotools.renderer.style.FontCache
					.getDefaultInstance();
			fc.registerFont(f);
			// }
		}
	}

}
