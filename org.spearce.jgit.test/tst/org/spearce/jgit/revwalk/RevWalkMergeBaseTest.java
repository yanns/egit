/*
 * Copyright (C) 2008, Google Inc.
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

package org.spearce.jgit.revwalk;

import org.spearce.jgit.lib.ObjectId;
import org.spearce.jgit.revwalk.filter.RevFilter;

public class RevWalkMergeBaseTest extends RevWalkTestCase {
	public void testNone() throws Exception {
		final ObjectId c1 = commit(commit(commit()));
		final ObjectId c2 = commit(commit(commit()));

		rw.setRevFilter(RevFilter.MERGE_BASE);
		markStart(c1);
		markStart(c2);
		assertNull(rw.next());
	}

	public void testSimple() throws Exception {
		final ObjectId a = commit();
		final ObjectId b = commit(a);
		final ObjectId c1 = commit(commit(commit(commit(commit(b)))));
		final ObjectId c2 = commit(commit(commit(commit(commit(b)))));

		rw.setRevFilter(RevFilter.MERGE_BASE);
		markStart(c1);
		markStart(c2);
		assertCommit(b, rw.next());
		assertNull(rw.next());
	}

	public void testMultipleHeads_SameBase1() throws Exception {
		final ObjectId a = commit();
		final ObjectId b = commit(a);
		final ObjectId c1 = commit(commit(commit(commit(commit(b)))));
		final ObjectId c2 = commit(commit(commit(commit(commit(b)))));
		final ObjectId c3 = commit(commit(commit(b)));

		rw.setRevFilter(RevFilter.MERGE_BASE);
		markStart(c1);
		markStart(c2);
		markStart(c3);
		assertCommit(b, rw.next());
		assertNull(rw.next());
	}

	public void testMultipleHeads_SameBase2() throws Exception {
		final ObjectId a = commit();
		final ObjectId b = commit(a);
		final ObjectId c = commit(b);
		final ObjectId d1 = commit(commit(commit(commit(commit(b)))));
		final ObjectId d2 = commit(commit(commit(commit(commit(c)))));
		final ObjectId d3 = commit(commit(commit(c)));

		rw.setRevFilter(RevFilter.MERGE_BASE);
		markStart(d1);
		markStart(d2);
		markStart(d3);
		assertCommit(b, rw.next());
		assertNull(rw.next());
	}

	public void testCrissCross() throws Exception {
		// See http://marc.info/?l=git&m=111463358500362&w=2 for a nice
		// description of what this test is creating. We don't have a
		// clean merge base for d,e as they each merged the parents b,c
		// in different orders.
		//
		final ObjectId a = commit();
		final ObjectId b = commit(a);
		final ObjectId c = commit(a);
		final ObjectId d = commit(b, c);
		final ObjectId e = commit(c, b);

		rw.setRevFilter(RevFilter.MERGE_BASE);
		markStart(d);
		markStart(e);
		assertCommit(c, rw.next());
		assertCommit(b, rw.next());
		assertNull(rw.next());
	}
}
