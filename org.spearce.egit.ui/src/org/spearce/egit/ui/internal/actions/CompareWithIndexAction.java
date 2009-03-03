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

package org.spearce.egit.ui.internal.actions;

import java.io.File;
import java.io.IOException;

import org.eclipse.compare.CompareUI;
import org.eclipse.compare.IContentChangeListener;
import org.eclipse.compare.IContentChangeNotifier;
import org.eclipse.compare.ITypedElement;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.action.IAction;
import org.eclipse.team.core.history.IFileRevision;
import org.eclipse.team.core.subscribers.Subscriber;
import org.eclipse.team.internal.ui.Utils;
import org.eclipse.team.ui.TeamUI;
import org.eclipse.team.ui.synchronize.ISynchronizeParticipant;
import org.eclipse.team.ui.synchronize.SaveableCompareEditorInput;
import org.spearce.egit.core.internal.storage.GitFileRevision;
import org.spearce.egit.core.project.RepositoryMapping;
import org.spearce.egit.ui.internal.EditableRevision;
import org.spearce.egit.ui.internal.GitCompareFileRevisionEditorInput;
import org.spearce.egit.ui.synchronize.GitCompareIndexSubscriber;
import org.spearce.egit.ui.synchronize.GitSynchronizeParticipant;
import org.spearce.jgit.lib.GitIndex;
import org.spearce.jgit.lib.Repository;

/**
 * The "compare with index" action. This action opens a diff editor comparing
 * the file as found in the working directory and the version found in the index
 * of the repository.
 */
@SuppressWarnings("restriction")
public class CompareWithIndexAction extends RepositoryAction {

	@Override
	public void execute(IAction action) {
		final IResource[] selectedResources = getSelectedResources();
		final IResource resource = selectedResources[0];

		if (selectedResources.length == 1 && resource instanceof IFile) {
			compareOneFile((IFile)resource);
		} else {
			// if more than one file is selected, use the synchronize perspective
			Subscriber subscriber = new GitCompareIndexSubscriber(selectedResources);
			GitSynchronizeParticipant participant = new GitSynchronizeParticipant(subscriber);
			TeamUI.getSynchronizeManager().addSynchronizeParticipants(new ISynchronizeParticipant[] {participant});
			participant.refresh(selectedResources, null, null, null);
		}
	}

	private void compareOneFile(final IFile file) {
		final RepositoryMapping mapping = RepositoryMapping.getMapping(file.getProject());
		final Repository repository = mapping.getRepository();
		final String gitPath = mapping.getRepoRelativePath(file);

		final IFileRevision nextFile = GitFileRevision.inIndex(repository, gitPath);

		final ITypedElement base = SaveableCompareEditorInput.createFileElement(file);
		final EditableRevision next = new EditableRevision(nextFile);

		IContentChangeListener listener = new IContentChangeListener() {
			public void contentChanged(IContentChangeNotifier source) {
				final byte[] newContent = next.getModifiedContent();
				try {
					final GitIndex index = repository.getIndex();
					final File baseFile = new File(file.getLocation().toString());
					index.add(mapping.getWorkDir(), baseFile, newContent);
					index.write();
				} catch (IOException e) {
					Utils.handleError(getTargetPart().getSite().getShell(), e,
							"Error during adding to index",
							"Error during adding to index");
					return;
				}
			}
		};

		next.addContentChangeListener(listener);

		final GitCompareFileRevisionEditorInput in = new GitCompareFileRevisionEditorInput(
				base, next, null);
		CompareUI.openCompareEditor(in);
	}

	@Override
	public boolean isEnabled() {
		final IResource[] selectedResources = getSelectedResources();
		if (selectedResources.length == 0)
			return false;

		for (IResource resource : selectedResources) {
			final RepositoryMapping mapping = RepositoryMapping.getMapping(resource.getProject());
			if (mapping == null)
				return false;
		}

		return true;
	}

}
