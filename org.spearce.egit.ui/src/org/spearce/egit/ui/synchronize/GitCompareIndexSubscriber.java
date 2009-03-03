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

import org.eclipse.core.resources.IResource;
import org.eclipse.team.core.TeamException;
import org.eclipse.team.core.variants.IResourceVariantComparator;
import org.eclipse.team.core.variants.IResourceVariantTree;
import org.eclipse.team.core.variants.ResourceVariantTreeSubscriber;
import org.eclipse.team.core.variants.SessionResourceVariantByteStore;

/**
 * This subscriber is used when comparing the local workspace with its GIT's
 * index.
 */
public class GitCompareIndexSubscriber extends ResourceVariantTreeSubscriber {

	private IResource[] roots;

	private GitBaseTree base;

	private GitIndexTree index;

	private GitResourceVariantComparator comparator;

	/**
	 * @param roots
	 *            the list of compared resources
	 */
	public GitCompareIndexSubscriber(IResource[] roots) {
		super();
		this.roots = roots;
		this.base = new GitBaseTree(new SessionResourceVariantByteStore(), this);
		this.index = new GitIndexTree(new SessionResourceVariantByteStore(), this);
		this.comparator = new GitResourceVariantComparator();
	}

	@Override
	protected IResourceVariantTree getBaseTree() {
		return base;
	}

	@Override
	protected IResourceVariantTree getRemoteTree() {
		return index;
	}

	@Override
	public String getName() {
		return "git";
	}

	@Override
	public IResourceVariantComparator getResourceComparator() {
		return comparator;
	}

	@Override
	public boolean isSupervised(IResource resource) throws TeamException {
		return true;
	}

	@Override
	public IResource[] roots() {
		return roots;
	}

}
