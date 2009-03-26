/*
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

package org.spearce.egit.ui.synchronize;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.LocalResourceManager;
import org.eclipse.jface.resource.ResourceManager;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.spearce.egit.ui.internal.GitDecoration;
import org.spearce.egit.ui.internal.decorators.GitLightweightDecorator;

/**
 * @author simon
 *
 */
public class GitParticipantLabelDecorator extends LabelProvider implements
		ILabelDecorator {

	private final ResourceManager fImageCache;

	/**
	 * constructor
	 */
	public GitParticipantLabelDecorator() {
		super();
		fImageCache = new LocalResourceManager(JFaceResources.getResources());
	}

	public Image decorateImage(final Image image, final Object element) {
		final GitDecoration decoration = GitLightweightDecorator
				.getDecoration(element);
		return decoration.decorateImage(image, fImageCache);
	}

	public String decorateText(final String input, final Object element) {
		final GitDecoration decoration = GitLightweightDecorator
				.getDecoration(element);
		return decoration.decorateText(input);
	}
}
