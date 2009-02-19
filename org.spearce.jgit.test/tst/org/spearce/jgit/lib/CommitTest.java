package org.spearce.jgit.lib;

import java.io.File;
import java.io.IOException;

public class CommitTest extends RepositoryTestCase {

	private static final String FILE_NAME = "file1";

	public void testASimpleCommit() throws IOException {

		// create new repository
		recursiveDelete(trash_git, false, null, true);
		db = new Repository(trash_git);
		db.create();

		GitIndex index = new GitIndex(db);

		// commit nothing
		commitIndex(index, "commit 1");

		// add a file and commit it
		String initialContent = "file1\n";
		File file1 = writeTrashFile(FILE_NAME, initialContent);
		index.add(trash, file1);

		commitIndex(index, "commit 2");

		String newContent = "new content of file1\n";
		File file2 = writeTrashFile(FILE_NAME, newContent);

		// git diff
		ObjectLoader loader;
		loader = db.openBlob(index.getEntry(FILE_NAME).getObjectId());
		byte[] indexBytes = loader.getBytes();

		compare(initialContent.getBytes(), indexBytes);

		// git diff --cached
		index.add(trash, file2);

		loader = db.openBlob(index.getEntry(FILE_NAME).getObjectId());
		indexBytes = loader.getBytes();
		compare(newContent.getBytes(), indexBytes);

		// git diff HEAD^
		commitIndex(index, "commit 3");
		loader = db.openBlob(index.getEntry(FILE_NAME).getObjectId());
		indexBytes = loader.getBytes();
		compare(newContent.getBytes(), indexBytes);

		Tree baselineTree = db.mapTree("HEAD");
		TreeEntry blobEntry = baselineTree.findBlobMember(FILE_NAME);
		loader = db.openBlob(blobEntry.getId());
		byte[] commitBytes = loader.getBytes();
		compare(newContent.getBytes(), commitBytes);
	}

	private void commitIndex(GitIndex index, String commitMessage) throws IOException {

		// where is the HEAD before commiting
		Ref oldHEAD = db.getAllRefs().get(Constants.HEAD);

		// ask the index to construct the main tree.
		ObjectId objectId = index.writeTree();
		final Commit commit = new Commit(db);
		commit.setAuthor(new PersonIdent(db));
		commit.setCommitter(new PersonIdent(db));
		commit.setMessage(commitMessage);
		commit.setTreeId(objectId);
		if (oldHEAD != null) {
			commit.setParentIds(new ObjectId[]{ oldHEAD.getObjectId() });
		}
		commit.commit();

		final RefUpdate ru = db.updateRef(Constants.HEAD);
		ru.setNewObjectId(commit.getCommitId());
		ru.setRefLogMessage(commitMessage, false);

		if (oldHEAD != null) {
			// commit has parents
			ru.setExpectedOldObjectId(oldHEAD.getObjectId());
			assertSame(RefUpdate.Result.FAST_FORWARD, ru.update());
		} else {
			// commit has no parents
			assertSame(RefUpdate.Result.NEW, ru.update());
		}
	}

	private void compare(byte[] expected, byte[] actual) {
		int i = 0;
		for (byte actualByte : actual) {
			byte expectedByte = expected[i];
			assertEquals(expectedByte, actualByte);
			i += 1;
		}

	}

}
