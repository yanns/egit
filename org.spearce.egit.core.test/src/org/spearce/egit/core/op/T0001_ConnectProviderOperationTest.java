/*******************************************************************************
 * Copyright (C) 2007, Robin Rosenberg <robin.rosenberg@dewire.com>
 * Copyright (C) 2008, Shawn O. Pearce <spearce@spearce.org>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * See LICENSE for the full license text, also available.
 *******************************************************************************/
package org.spearce.egit.core.op;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.TimeZone;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.team.core.RepositoryProvider;
import org.spearce.egit.core.test.GitTestCase;
import org.spearce.jgit.lib.Commit;
import org.spearce.jgit.lib.FileTreeEntry;
import org.spearce.jgit.lib.ObjectId;
import org.spearce.jgit.lib.ObjectWriter;
import org.spearce.jgit.lib.PersonIdent;
import org.spearce.jgit.lib.RefUpdate;
import org.spearce.jgit.lib.Repository;
import org.spearce.jgit.lib.Tree;

public class T0001_ConnectProviderOperationTest extends GitTestCase {

	public void testNoRepository() throws CoreException {

		ConnectProviderOperation operation = new ConnectProviderOperation(
				project.getProject(), null);
		operation.run(null);

		// We are shared because we declared as shared
		assertTrue(RepositoryProvider.isShared(project.getProject()));
		assertTrue(!gitDir.exists());
	}

	public void testNewRepository() throws CoreException {

		File gitDir = new File(project.getProject().getWorkspace().getRoot()
				.getRawLocation().toFile(), ".git");
		ConnectProviderOperation operation = new ConnectProviderOperation(
				project.getProject(), gitDir);
		operation.run(null);

		assertTrue(RepositoryProvider.isShared(project.getProject()));

		assertTrue(gitDir.exists());
	}

	public void testNewUnsharedFile() throws CoreException, IOException,
			InterruptedException {

		project.createSourceFolder();
		IFile fileA = project.getProject().getFolder("src").getFile("A.java");
		String srcA = "class A {\n" + "}\n";
		fileA.create(new ByteArrayInputStream(srcA.getBytes()), false, null);

		File gitDir = new File(project.getProject().getWorkspace().getRoot()
				.getRawLocation().toFile(), ".git");
		Repository thisGit = new Repository(gitDir);
		thisGit.create();
		Tree rootTree = new Tree(thisGit);
		Tree prjTree = rootTree.addTree(project.getProject().getName());
		Tree srcTree = prjTree.addTree("src");
		FileTreeEntry entryA = srcTree.addFile("A.java");
		ObjectWriter writer = new ObjectWriter(thisGit);
		entryA.setId(writer.writeBlob(fileA.getRawLocation().toFile()));
		srcTree.setId(writer.writeTree(srcTree));
		prjTree.setId(writer.writeTree(prjTree));
		rootTree.setId(writer.writeTree(rootTree));
		Commit commit = new Commit(thisGit);
		commit.setTree(rootTree);
		commit.setAuthor(new PersonIdent("J. Git", "j.git@egit.org", new Date(
				60876075600000L), TimeZone.getTimeZone("GMT+1")));
		commit.setCommitter(commit.getAuthor());
		commit.setMessage("testNewUnsharedFile\n\nJunit tests\n");
		ObjectId id = writer.writeCommit(commit);
		RefUpdate lck = thisGit.updateRef("refs/heads/master");
		assertNotNull("obtained lock", lck);
		lck.setNewObjectId(id);
		assertEquals(RefUpdate.Result.NEW, lck.forceUpdate());

		// helper asserts, this is not what we are really testing
		assertTrue("blob missing", new File(gitDir,
				"objects/2e/2439c32d01f0ef39644d561945e8f4b2239489").exists());

		assertTrue("tree missing", new File(gitDir,
				"objects/87/a105cc4bc0a79885d07ec560c3eee49438acf0").exists());
		assertTrue("tree missing", new File(gitDir,
				"objects/08/ccc3d91a14d337a45f355d3d63bd97fd5e4db9").exists());
		assertTrue("tree missing", new File(gitDir,
				"objects/9d/aeec817090098f05eeca858e3a552d78b0a346").exists());
		assertTrue("commit missing", new File(gitDir,
				"objects/09/6f1a84091b90b6d9fb12f95848da69496305c1").exists());

		ConnectProviderOperation operation = new ConnectProviderOperation(
				project.getProject(), null);
		operation.run(null);

		final boolean f[] = new boolean[1];
		new Job("wait") {
			protected IStatus run(IProgressMonitor monitor) {

				System.out.println("MyJob");
				f[0] = true;
				return null;
			}

			{
				setRule(project.getProject());
				schedule();
			}
		};
		while (!f[0]) {
			System.out.println("Waiting");
			Thread.sleep(1000);
		}
		System.out.println("DONE");
	}
}
