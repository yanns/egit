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

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.team.core.TeamException;
import org.eclipse.team.core.variants.IResourceVariant;
import org.eclipse.team.core.variants.ResourceVariantByteStore;
import org.eclipse.team.core.variants.ResourceVariantTree;
import org.eclipse.team.internal.core.mapping.LocalResourceVariant;
import org.spearce.egit.core.internal.storage.GitFileRevision;
import org.spearce.egit.core.project.RepositoryMapping;
import org.spearce.jgit.lib.Repository;

/**
 * The tree representing the resources from the staging area (GIT's index).
 */
public class GitIndexTree extends ResourceVariantTree {

	private GitCompareIndexSubscriber subscriber;

	/**
	 * The staging area variant of a resource. The class encapsulates
	 * {@link GitFileRevision}. TODO: GitFileRevision can maybe be extended to
	 * be used directly?
	 */
	public static class GitResourceVariant implements IResourceVariant {
		private final GitFileRevision gitFileRevision;

		/**
		 * @param repository
		 * @param gitPath
		 */
		public GitResourceVariant(final Repository repository,
				final String gitPath) {
			super();
			this.gitFileRevision = GitFileRevision.inIndex(repository, gitPath);
		}

		public byte[] asBytes() {
			return getContentIdentifier().getBytes();
		}

		public String getContentIdentifier() {
			return gitFileRevision.getContentIdentifier();
		}

		public String getName() {
			return gitFileRevision.getName();
		}

		public IStorage getStorage(IProgressMonitor monitor)
				throws TeamException {
			try {
				return gitFileRevision.getStorage(monitor);
			} catch (CoreException e) {
				throw TeamException.asTeamException(e);
			}
		}

		public boolean isContainer() {
			return false;
		}

		/**
		 * @return {@link #gitFileRevision}
		 */
		public GitFileRevision getGitFileRevision() {
			return gitFileRevision;
		}
	}

	/**
	 * @param store
	 *            the {@link ResourceVariantByteStore}
	 * @param subscriber
	 *            the subscriber that uses this tree
	 */
	public GitIndexTree(ResourceVariantByteStore store,
			GitCompareIndexSubscriber subscriber) {
		super(store);
		this.subscriber = subscriber;
	}

	@Override
	protected IResourceVariant[] fetchMembers(IResourceVariant variant,
			IProgressMonitor progress) throws TeamException {
		return null;
	}

	@Override
	protected IResourceVariant fetchVariant(IResource resource, int depth,
			IProgressMonitor monitor) throws TeamException {
		return null;
	}

	public IResourceVariant getResourceVariant(IResource resource)
			throws TeamException {
		if (resource instanceof IFile) {
			final RepositoryMapping mapping = RepositoryMapping
					.getMapping(resource.getProject());
			final Repository repository = mapping.getRepository();
			final String gitPath = mapping.getRepoRelativePath(resource);
			return new GitResourceVariant(repository, gitPath);
		} else {
			// TODO: handle folders to map them the GIT's trees?
			return new LocalResourceVariant(resource);
		}
	}

	public IResource[] roots() {
		return subscriber.roots();
	}

}
