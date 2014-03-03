/*******************************************************************************
 * Copyright (c) 2005 Bastian Bergerhoff and others
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution.
 * 
 * Contributors:
 *     Bastian Bergerhoff - initial API and implementation
 *     Georg Sendt - added JRegexp-related implementations
 *******************************************************************************/
package de.babe.eclipse.plugins.quickREx;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import org.eclipse.core.runtime.Path;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

/**
 * @author bastian.bergerhoff, georg.sendt
 */
public class PluginImageRegistry extends ImageRegistry {

  private static URL iconBaseURL = null;

  public final static String IMG_ORGANIZE_RES = "IMG_ORGANIZE_RES"; //$NON-NLS-1$

  public final static String IMG_ORGANIZE_TEXTS = "IMG_ORGANIZE_TEXTS"; //$NON-NLS-1$

  public static final String IMG_JAVA_LOGO = "IMG_JAVA_LOGO"; //$NON-NLS-1$

  public static final String IMG_ORO_PERL_LOGO = "IMG_ORO_PERL_LOGO"; //$NON-NLS-1$

  public static final String IMG_ORO_AWK_LOGO = "IMG_ORO_AWK_LOGO"; //$NON-NLS-1$
  
  public static final String IMG_JREGEX_LOGO = "IMG_JREGEX_LOGO"; //$NON-NLS-1$

  public static final String IMG_JAKARTA_REGEXP_LOGO = "IMG_JAKARTA_REGEXP_LOGO"; //$NON-NLS-1$

  public static final String IMG_KEEP_RE = "IMG_KEEP_RE"; //$NON-NLS-1$

  public static final String IMG_SAVE_TT = "IMG_SAVE_TT"; //$NON-NLS-1$

  public static final String IMG_LOAD_TT = "IMG_LOAD_TT"; //$NON-NLS-1$

  public static final String IMG_JCOPY = "IMG_JCOPY"; //$NON-NLS-1$

  public static final String IMG_GREP = "IMG_GREP"; //$NON-NLS-1$

  public static final String IMG_BOOK = "IMG_BOOK"; //$NON-NLS-1$

  public static final String IMG_CATEGORY = "IMG_CATEGORY"; //$NON-NLS-1$

  public static final String IMG_REG_EXP = "IMG_REG_EXP"; //$NON-NLS-1$

  public static final String IMG_LINK_WITH_EDITOR = "IMG_LINK_WITH_EDITOR"; //$NON-NLS-1$

  public static final String IMG_SEARCH_RE = "IMG_SEARCH_RE"; //$NON-NLS-1$

  public static final String IMG_EDIT_RE = "IMG_EDIT_RE"; //$NON-NLS-1$

  private static HashMap imagesMap = new HashMap();

  /**
   * The constructor
   */
  public PluginImageRegistry() {
    super();
  }

  /**
   * The constructor
   * 
   * @param p_display
   */
  public PluginImageRegistry(Display p_display) {
    super(p_display);
  }

  /**
   * The constructor
   * 
   * @param p_plugin
   */
  public PluginImageRegistry(QuickRExPlugin p_plugin) {
    this();
    try {
      if (iconBaseURL == null) {
        iconBaseURL = p_plugin.find(new Path("/icons/")); //$NON-NLS-1$
      }
      put(IMG_ORGANIZE_RES, ImageDescriptor.createFromURL(new URL(iconBaseURL, "orgREs.gif"))); //$NON-NLS-1$
      put(IMG_ORGANIZE_TEXTS, ImageDescriptor.createFromURL(new URL(iconBaseURL, "orgTestTexts.gif"))); //$NON-NLS-1$
      put(IMG_JAVA_LOGO, ImageDescriptor.createFromURL(new URL(iconBaseURL, "JavalogoSmall.gif"))); //$NON-NLS-1$
      put(IMG_ORO_PERL_LOGO, ImageDescriptor.createFromURL(new URL(iconBaseURL, "OROPerllogoSmall.gif"))); //$NON-NLS-1$
      put(IMG_ORO_AWK_LOGO, ImageDescriptor.createFromURL(new URL(iconBaseURL, "OROAwklogoSmall.gif"))); //$NON-NLS-1$
      put(IMG_JREGEX_LOGO, ImageDescriptor.createFromURL(new URL(iconBaseURL, "JRegexSmall.gif"))); //$NON-NLS-1$      
      put(IMG_JAKARTA_REGEXP_LOGO, ImageDescriptor.createFromURL(new URL(iconBaseURL, "JakartaRegexpSmall.gif"))); //$NON-NLS-1$      
      put(IMG_KEEP_RE, ImageDescriptor.createFromURL(new URL(iconBaseURL, "saveRE.gif"))); //$NON-NLS-1$
      put(IMG_SAVE_TT, ImageDescriptor.createFromURL(new URL(iconBaseURL, "saveText.gif"))); //$NON-NLS-1$
      put(IMG_LOAD_TT, ImageDescriptor.createFromURL(new URL(iconBaseURL, "loadText.gif"))); //$NON-NLS-1$
      put(IMG_JCOPY, ImageDescriptor.createFromURL(new URL(iconBaseURL, "jcopy.gif"))); //$NON-NLS-1$
      put(IMG_GREP, ImageDescriptor.createFromURL(new URL(iconBaseURL, "grep.gif"))); //$NON-NLS-1$
      put(IMG_BOOK, ImageDescriptor.createFromURL(new URL(iconBaseURL, "book.gif"))); //$NON-NLS-1$
      put(IMG_CATEGORY, ImageDescriptor.createFromURL(new URL(iconBaseURL, "category.gif"))); //$NON-NLS-1$
      put(IMG_REG_EXP, ImageDescriptor.createFromURL(new URL(iconBaseURL, "regExp.gif"))); //$NON-NLS-1$
      put(IMG_LINK_WITH_EDITOR, ImageDescriptor.createFromURL(new URL(iconBaseURL, "linkWithEditor.gif"))); //$NON-NLS-1$
      put(IMG_SEARCH_RE, ImageDescriptor.createFromURL(new URL(iconBaseURL, "searchInRELib.gif"))); //$NON-NLS-1$
      put(IMG_EDIT_RE, ImageDescriptor.createFromURL(new URL(iconBaseURL, "editRE.gif"))); //$NON-NLS-1$
    } catch (MalformedURLException me) {
      // nop
    }
  }

  /**
   * Returns the image for the passed key or <code>null</code>.
   * 
   * @param p_key the key for the image
   * @return the image or <code>null</code>
   */
  public Image getImage(String p_key) {
    if (imagesMap.containsKey(p_key)) {
      return (Image)imagesMap.get(p_key);
    }
    ImageDescriptor iDesc = getDescriptor(p_key);
    if (iDesc == null) {
      return null;
    } else {
      Image img = iDesc.createImage();
      imagesMap.put(p_key, img);
      return img;
    }
  }

  /**
   * Returns the ImageDescriptor for the passed key.
   * 
   * @param p_key the key for the descriptor
   * @return the descriptor
   */
  public ImageDescriptor getImageDescriptor(String p_key) {
    return getDescriptor(p_key);
  }
}