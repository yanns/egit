/*
 * Copyright (C) 2003, 2006 Subclipse project and others.
 * Copyright (C) 2008, Tor Arne Vestbø <torarnv@gmail.com>
 * Copyright (C) 2009, Yann Simon <yann.simon.fr@gmail.com>
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or
 * without modification, are permitted provided that the following
 * conditions are met:
 *
 * - Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * - Redistributions in binary form must reproduce the above
 *   copyright notice, this list of conditions and the following
 *   disclaimer in the documentation and/or other materials provided
 *   with the distribution.
 *
 * - Neither the name of the Git Development Community nor the
 *   names of its contributors may be used to endorse or promote
 *   products derived from this software without specific prior
 *   written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND
 * CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.spearce.egit.ui.internal;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ResourceManager;
import org.eclipse.jface.viewers.DecorationContext;
import org.eclipse.jface.viewers.DecorationOverlayIcon;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.IDecorationContext;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.spearce.egit.ui.Activator;

/**
 * A decoration for a GIT resource.
 */
public class GitDecoration implements IDecoration {

	private List<String> prefixes = new ArrayList<String>();

	private List<String> suffixes = new ArrayList<String>();

	private ImageDescriptor overlay = null;

	private Font font;

	private Color backgroundColor;

	private Color foregroundColor;

	/**
	 * Adds an icon overlay to the decoration
	 * <p>
	 * Copies the behavior of <code>DecorationBuilder</code> of only allowing
	 * the overlay to be set once.
	 */
	public void addOverlay(ImageDescriptor overlayImage) {
		if (overlay == null)
			overlay = overlayImage;
	}

	public void addOverlay(ImageDescriptor overlayImage, int quadrant) {
		addOverlay(overlayImage);
	}

	public void addPrefix(String prefix) {
		prefixes.add(prefix);
	}

	public void addSuffix(String suffix) {
		suffixes.add(suffix);
	}

	public IDecorationContext getDecorationContext() {
		return new DecorationContext();
	}

	public void setBackgroundColor(Color color) {
		backgroundColor = color;
	}

	public void setForegroundColor(Color color) {
		foregroundColor = color;
	}

	public void setFont(Font font) {
		this.font = font;
	}

	/**
	 * @return overlay
	 */
	public ImageDescriptor getOverlay() {
		return overlay;
	}

	/**
	 * @return prefix
	 */
	public String getPrefix() {
		StringBuffer sb = new StringBuffer();
		for (Iterator<String> iter = prefixes.iterator(); iter.hasNext();) {
			sb.append(iter.next());
		}
		return sb.toString();
	}

	/**
	 * @return suffix
	 */
	public String getSuffix() {
		StringBuffer sb = new StringBuffer();
		for (Iterator<String> iter = suffixes.iterator(); iter.hasNext();) {
			sb.append(iter.next());
		}
		return sb.toString();
	}

	/**
	 * @return font
	 */
	public Font getFont() {
		return font;
	}

	/**
	 * @return background color
	 */
	public Color getBackgroundColor() {
		return backgroundColor;
	}

	/**
	 * @return foreground color
	 */
	public Color getForegroundColor() {
		return foregroundColor;
	}

	/**
	 * Decorates a String.
	 *
	 * @param input
	 *            the String to decorate
	 * @return the decorated String
	 */
	public String decorateText(String input) {
		final StringBuffer buffer = new StringBuffer();
		final String prefix = getPrefix();
		if (prefix != null)
			buffer.append(prefix);
		buffer.append(input);
		final String suffix = getSuffix();
		if (suffix != null)
			buffer.append(suffix);
		return buffer.toString();
	}

	/**
	 * Decorates an image.
	 *
	 * @param image
	 *            the Image to decorate.
	 * @param fImageCache
	 *            the {@link ResourceManager} used to manipulate the images
	 * @return the decorated Image.
	 */
	public Image decorateImage(Image image, final ResourceManager fImageCache) {
		if (overlay != null) {
			try {
				return fImageCache.createImage(new DecorationOverlayIcon(image,
						overlay, IDecoration.BOTTOM_RIGHT));
			} catch (Exception e) {
				Activator.logError(e.getMessage(), e);
			}
		}
		return image;
	}
}
