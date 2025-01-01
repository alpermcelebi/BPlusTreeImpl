import java.util.ArrayList;
import java.util.Objects;

public class ScholarTree {
	
	public ScholarNode primaryRoot;		//root of the primary B+ tree
	public ScholarNode secondaryRoot;	//root of the secondary B+ tree
	public ScholarTree(Integer order) {
		ScholarNode.order = order;
		primaryRoot = new ScholarNodePrimaryLeaf(null);
		primaryRoot.level = 0;
		secondaryRoot = new ScholarNodeSecondaryLeaf(null);
		secondaryRoot.level = 0;
	}

	public void addPaper(CengPaper paper) {
		// TODO: Implement this method
		// add methods to fill both primary and secondary tree
		insertIntoPrimaryTree(paper);
		insertIntoSecondaryTree(paper);
	}

	public CengPaper searchPaper(Integer paperId) {
		// TODO: Implement this method
		// find the paper with the searched paperId in primary B+ tree
		// return value will not be tested, just print according to the specifications
		ScholarNode currentNode = primaryRoot;
		System.out.println("search1|" + paperId);
		String indent = "";

		while (currentNode != null) {
			if (currentNode.getType() == ScholarNodeType.Internal) {
				ScholarNodePrimaryIndex indexNode = (ScholarNodePrimaryIndex) currentNode;
				System.out.println(indent + "<index>");
				for (int i = 0; i < indexNode.paperIdCount(); i++) {
					System.out.println(indent + indexNode.paperIdAtIndex(i));
				}
				System.out.println(indent + "</index>");

				int idx = 0;
				while (idx < indexNode.paperIdCount() && paperId >= indexNode.paperIdAtIndex(idx)) {
					idx++;
				}
				currentNode = indexNode.getChildrenAt(idx);
				indent += "\t";
			} else {
				ScholarNodePrimaryLeaf leafNode = (ScholarNodePrimaryLeaf) currentNode;
				System.out.println(indent + "<data>");
				CengPaper foundPaper = null;
				for (int i = 0; i < leafNode.paperCount(); i++) {
					if (leafNode.paperIdAtIndex(i).equals(paperId)) {
						foundPaper = leafNode.paperAtIndex(i);
						System.out.println(indent + "<record>" + foundPaper.fullName() + "</record>");
						break;
					}
				}
				System.out.println(indent + "</data>");

				if (foundPaper == null) {
					System.out.println("Could not find " + paperId);
				}
				return foundPaper;
			}
		}
		return null;
	}

	public void searchJournal(String journal) {
		// TODO: Implement this method
		// find the journal with the searched journal in secondary B+ tree
		ScholarNode currentNode = secondaryRoot;
		System.out.println("search2|" + journal);
		String indent = "";

		while (currentNode != null) {
			if (currentNode.getType() == ScholarNodeType.Internal) {
				ScholarNodeSecondaryIndex indexNode = (ScholarNodeSecondaryIndex) currentNode;
				System.out.println(indent + "<index>");
				for (int i = 0; i < indexNode.journalCount(); i++) {
					System.out.println(indent + indexNode.journalAtIndex(i));
				}
				System.out.println(indent + "</index>");

				int idx = 0;
				while (idx < indexNode.journalCount() &&
						journal.compareTo(indexNode.journalAtIndex(idx)) > 0) {
					idx++;
				}
				currentNode = indexNode.getChildrenAt(idx);
				indent += "\t";
			} else {
				ScholarNodeSecondaryLeaf leafNode = (ScholarNodeSecondaryLeaf) currentNode;
				System.out.println(indent + "<data>");
				boolean found = false;

				for (int i = 0; i < leafNode.journalCount(); i++) {
					if (leafNode.journalAtIndex(i).equals(journal)) {
						ArrayList<Integer> paperIds = leafNode.papersAtIndex(i);
						if (paperIds != null && !paperIds.isEmpty()) {
							// Print journal name
							System.out.println(indent + journal);
							// Print records
							for (Integer paperId : paperIds) {
								CengPaper paper = searchPFJ(paperId);
								if (paper != null) {
									System.out.println(indent + "\t<record>" + paper.fullName() + "</record>");
								}
							}
							found = true;
						}
						break;
					}
				}

				if (!found) {
					System.out.println(indent + "Could not find " + journal);
				}

				System.out.println(indent + "</data>");
				break;
			}
		}
	}

	public void printPrimaryScholar() {
		// TODO: Implement this method
		// print the primary B+ tree in Depth-first order
		ArrayList<ScholarNode> nodeQueue = new ArrayList<>();
		ArrayList<String> indentQueue = new ArrayList<>();
		nodeQueue.add(primaryRoot);
		indentQueue.add("");

		while (!nodeQueue.isEmpty()) {
			ScholarNode currentNode = nodeQueue.remove(0);
			String indent = indentQueue.remove(0);

			if (currentNode.getType() == ScholarNodeType.Internal) {
				ScholarNodePrimaryIndex indexNode = (ScholarNodePrimaryIndex) currentNode;
				ArrayList<ScholarNode> children = indexNode.getAllChildren();
				for (int i = children.size() - 1; i >= 0; i--) {
					nodeQueue.add(0, children.get(i));
					indentQueue.add(0, indent + "\t");
				}

				System.out.println(indent + "<index>");
				for (int i = 0; i < indexNode.paperIdCount(); i++) {
					System.out.println(indent + indexNode.paperIdAtIndex(i));
				}
				System.out.println(indent + "</index>");
			} else {
				ScholarNodePrimaryLeaf leafNode = (ScholarNodePrimaryLeaf) currentNode;
				System.out.println(indent + "<data>");
				for (int i = 0; i < leafNode.paperCount(); i++) {
					System.out.println(indent + "<record>" + leafNode.paperAtIndex(i).fullName() + "</record>");
				}
				System.out.println(indent + "</data>");
			}
		}
	}

	public void printSecondaryScholar() {
		// TODO: Implement this method
		// print the secondary B+ tree in Depth-first order
		ArrayList<ScholarNode> nodeQueue = new ArrayList<>();
		ArrayList<String> indentQueue = new ArrayList<>();
		nodeQueue.add(secondaryRoot);
		indentQueue.add("");

		while (!nodeQueue.isEmpty()) {
			ScholarNode currentNode = nodeQueue.remove(0);
			String indent = indentQueue.remove(0);

			if (currentNode.getType() == ScholarNodeType.Internal) {
				ScholarNodeSecondaryIndex indexNode = (ScholarNodeSecondaryIndex) currentNode;
				ArrayList<ScholarNode> children = indexNode.getAllChildren();
				for (int i = children.size() - 1; i >= 0; i--) {
					nodeQueue.add(0, children.get(i));
					indentQueue.add(0, indent + "\t");
				}

				System.out.println(indent + "<index>");
				for (int i = 0; i < indexNode.journalCount(); i++) {
					System.out.println(indent + indexNode.journalAtIndex(i));
				}
				System.out.println(indent + "</index>");
			} else {
				ScholarNodeSecondaryLeaf leafNode = (ScholarNodeSecondaryLeaf) currentNode;
				System.out.println(indent + "<data>");
				for (int i = 0; i < leafNode.journalCount(); i++) {
					String journal = leafNode.journalAtIndex(i);
					ArrayList<Integer> paperIds = leafNode.papersAtIndex(i);
					System.out.println(indent + journal);
					for (Integer paperId : paperIds) {
						System.out.println(indent + "\t<record>" + paperId + "</record>");
					}
				}
				System.out.println(indent + "</data>");
			}
		}
	}
	
	// Extra functions if needed
	private void insertIntoPrimaryTree(CengPaper paper) {
		ScholarNodePrimaryLeaf leaf = findPrimaryLeaf(paper.paperId());
		int position = findPrimaryInsertPosition(leaf, paper.paperId());
		leaf.addPaper(position, paper);

		if (leaf.paperCount() > 2 * ScholarNode.order) {
			handlePrimarySplit(leaf);
		}
	}

	private ScholarNodePrimaryLeaf findPrimaryLeaf(Integer paperId) {
		ScholarNode currentNode = primaryRoot;
		while (currentNode.getType() == ScholarNodeType.Internal) {
			ScholarNodePrimaryIndex indexNode = (ScholarNodePrimaryIndex) currentNode;
			int idx = 0;
			while (idx < indexNode.paperIdCount() && paperId >= indexNode.paperIdAtIndex(idx)) {
				idx++;
			}
			currentNode = indexNode.getChildrenAt(idx);
		}
		return (ScholarNodePrimaryLeaf) currentNode;
	}

	private int findPrimaryInsertPosition(ScholarNodePrimaryLeaf leaf, Integer paperId) {
		int position = 0;
		while (position < leaf.paperCount() && leaf.paperIdAtIndex(position) < paperId) {
			position++;
		}
		return position;
	}

	private void handlePrimarySplit(ScholarNodePrimaryLeaf leaf) {
		if (leaf.getParent() == null) {
			ScholarNodePrimaryIndex newRoot = new ScholarNodePrimaryIndex(null);
			ScholarNodePrimaryLeaf newLeaf = new ScholarNodePrimaryLeaf(newRoot);

			int midpoint = ScholarNode.order;
			for (int i = midpoint; i < leaf.paperCount(); i++) {
				newLeaf.addPaper(newLeaf.paperCount(), leaf.paperAtIndex(i));
			}
			leaf.getPapers().subList(midpoint, leaf.getPapers().size()).clear();

			leaf.setParent(newRoot);
			newRoot.addKey(0, newLeaf.paperIdAtIndex(0));
			newRoot.addChild(0, leaf);
			newRoot.addChild(1, newLeaf);
			primaryRoot = newRoot;
		} else {
			handlePrimaryNonRootSplit(leaf);
		}
	}

	private void handlePrimaryNonRootSplit(ScholarNodePrimaryLeaf leaf) {
		ScholarNodePrimaryIndex parent = (ScholarNodePrimaryIndex) leaf.getParent();
		ScholarNodePrimaryLeaf newLeaf = new ScholarNodePrimaryLeaf(parent);

		int midpoint = ScholarNode.order;
		for (int i = midpoint; i < leaf.paperCount(); i++) {
			newLeaf.addPaper(newLeaf.paperCount(), leaf.paperAtIndex(i));
		}
		leaf.getPapers().subList(midpoint, leaf.getPapers().size()).clear();

		insertIntoPrimaryParent(parent, newLeaf.paperIdAtIndex(0), newLeaf);
	}

	private void insertIntoPrimaryParent(ScholarNodePrimaryIndex parent, Integer key, ScholarNode child) {
		int position = 0;
		while (position < parent.paperIdCount() && parent.paperIdAtIndex(position) < key) {
			position++;
		}

		parent.addKey(position, key);
		parent.addChild(position + 1, child);

		if (parent.paperIdCount() > 2 * ScholarNode.order) {
			splitPrimaryIndex(parent);
		}
	}

	private void splitPrimaryIndex(ScholarNodePrimaryIndex node) {
		int midpoint = ScholarNode.order;
		Integer midKey = node.paperIdAtIndex(midpoint);

		ScholarNodePrimaryIndex newNode = new ScholarNodePrimaryIndex(node.getParent());

		for (int i = midpoint + 1; i < node.paperIdCount(); i++) {
			newNode.addKey(newNode.paperIdCount(), node.paperIdAtIndex(i));
		}

		for (int i = midpoint + 1; i <= node.paperIdCount(); i++) {
			ScholarNode child = node.getChildrenAt(i);
			newNode.addChild(newNode.getAllChildren().size(), child);
			child.setParent(newNode);
		}

		node.getAllKeys().subList(midpoint, node.getAllKeys().size()).clear();
		node.getAllChildren().subList(midpoint + 1, node.getAllChildren().size()).clear();

		if (node.getParent() == null) {
			ScholarNodePrimaryIndex newRoot = new ScholarNodePrimaryIndex(null);
			newRoot.addKey(0, midKey);
			newRoot.addChild(0, node);
			newRoot.addChild(1, newNode);
			node.setParent(newRoot);
			newNode.setParent(newRoot);
			primaryRoot = newRoot;
		} else {
			ScholarNodePrimaryIndex parent = (ScholarNodePrimaryIndex) node.getParent();
			int insertPosition = 0;
			while (insertPosition < parent.paperIdCount() &&
					parent.paperIdAtIndex(insertPosition) < midKey) {
				insertPosition++;
			}

			parent.addKey(insertPosition, midKey);
			parent.addChild(insertPosition + 1, newNode);
			newNode.setParent(parent);

			if (parent.paperIdCount() > 2 * ScholarNode.order) {
				splitPrimaryIndex(parent);
			}
		}
	}

	private void insertIntoSecondaryTree(CengPaper paper) {
		ScholarNodeSecondaryLeaf leaf = findSecondaryLeaf(paper.journal());

		int existingIndex = -1;
		ScholarNodeSecondaryLeaf currentLeaf = findLeftmostLeaf();

		while (currentLeaf != null) {
			for (int i = 0; i < currentLeaf.journalCount(); i++) {
				if (currentLeaf.journalAtIndex(i).equals(paper.journal())) {
					existingIndex = i;
					leaf = currentLeaf;
					break;
				}
			}
			if (existingIndex != -1) break;
			currentLeaf = getNextLeaf(currentLeaf);
		}

		if (existingIndex != -1) {
			ArrayList<Integer> paperIds = leaf.papersAtIndex(existingIndex);
			int insertPos = 0;
			while (insertPos < paperIds.size() && paperIds.get(insertPos) < paper.paperId()) {
				insertPos++;
			}
			paperIds.add(insertPos, paper.paperId());
		} else {
			int position = findSecondaryInsertPosition(leaf, paper.journal());
			ArrayList<Integer> newPaperIds = new ArrayList<>();
			newPaperIds.add(paper.paperId());

			leaf.getJournals().add(position, paper.journal());
			leaf.getPaperIdBucket().add(position, newPaperIds);

			if (leaf.journalCount() > 2 * ScholarNode.order) {
				handleSecondarySplit(leaf);
			}
		}
	}

	private ScholarNodeSecondaryLeaf findSecondaryLeaf(String journal) {
		ScholarNode currentNode = secondaryRoot;
		while (currentNode.getType() == ScholarNodeType.Internal) {
			ScholarNodeSecondaryIndex indexNode = (ScholarNodeSecondaryIndex) currentNode;
			int idx = 0;
			while (idx < indexNode.journalCount() &&
					journal.compareTo(indexNode.journalAtIndex(idx)) > 0) {
				idx++;
			}
			currentNode = indexNode.getChildrenAt(idx);
		}
		return (ScholarNodeSecondaryLeaf) currentNode;
	}

	private ScholarNodeSecondaryLeaf findLeftmostLeaf() {
		ScholarNode current = secondaryRoot;
		while (current.getType() == ScholarNodeType.Internal) {
			ScholarNodeSecondaryIndex index = (ScholarNodeSecondaryIndex) current;
			current = index.getChildrenAt(0);
		}
		return (ScholarNodeSecondaryLeaf) current;
	}

	private ScholarNodeSecondaryLeaf getNextLeaf(ScholarNodeSecondaryLeaf leaf) {
		if (leaf.getParent() == null) return null;

		ScholarNodeSecondaryIndex parent = (ScholarNodeSecondaryIndex) leaf.getParent();
		ArrayList<ScholarNode> siblings = parent.getAllChildren();
		int currentIndex = siblings.indexOf(leaf);

		if (currentIndex < siblings.size() - 1) {
			return (ScholarNodeSecondaryLeaf) siblings.get(currentIndex + 1);
		} else {
			while (parent != null) {
				if (parent.getParent() == null) return null;

				ScholarNodeSecondaryIndex grandParent = (ScholarNodeSecondaryIndex) parent.getParent();
				ArrayList<ScholarNode> parentSiblings = grandParent.getAllChildren();
				int parentIndex = parentSiblings.indexOf(parent);

				if (parentIndex < parentSiblings.size() - 1) {
					ScholarNode nextParent = parentSiblings.get(parentIndex + 1);
					while (nextParent.getType() == ScholarNodeType.Internal) {
						nextParent = ((ScholarNodeSecondaryIndex) nextParent).getChildrenAt(0);
					}
					return (ScholarNodeSecondaryLeaf) nextParent;
				}
				parent = grandParent;
			}
			return null;
		}
	}

	private int findSecondaryInsertPosition(ScholarNodeSecondaryLeaf leaf, String journal) {
		int position = 0;
		while (position < leaf.journalCount() &&
				leaf.journalAtIndex(position).compareTo(journal) < 0) {
			position++;
		}
		return position;
	}

	private void handleSecondarySplit(ScholarNodeSecondaryLeaf leaf) {
		ScholarNodeSecondaryLeaf newLeaf = new ScholarNodeSecondaryLeaf(null);
		int midpoint = ScholarNode.order;

		ArrayList<String> journals = leaf.getJournals();
		ArrayList<ArrayList<Integer>> paperIdBucket = leaf.getPaperIdBucket();

		for (int i = midpoint; i < journals.size(); i++) {
			newLeaf.getJournals().add(journals.get(i));
			newLeaf.getPaperIdBucket().add(new ArrayList<>(paperIdBucket.get(i)));
		}

		journals.subList(midpoint, journals.size()).clear();
		paperIdBucket.subList(midpoint, paperIdBucket.size()).clear();

		if (leaf.getParent() == null) {
			ScholarNodeSecondaryIndex newRoot = new ScholarNodeSecondaryIndex(null);
			newRoot.addJournal(0, newLeaf.journalAtIndex(0));
			newRoot.addChild(0, leaf);
			newRoot.addChild(1, newLeaf);
			leaf.setParent(newRoot);
			newLeaf.setParent(newRoot);
			secondaryRoot = newRoot;
		} else {
			ScholarNodeSecondaryIndex parent = (ScholarNodeSecondaryIndex) leaf.getParent();
			newLeaf.setParent(parent);

			int position = 0;
			while (position < parent.journalCount() &&
					parent.journalAtIndex(position).compareTo(newLeaf.journalAtIndex(0)) < 0) {
				position++;
			}

			parent.addJournal(position, newLeaf.journalAtIndex(0));
			parent.addChild(position + 1, newLeaf);

			if (parent.journalCount() > 2 * ScholarNode.order) {
				splitSecondaryIndex(parent);
			}
		}
	}

	private void splitSecondaryIndex(ScholarNodeSecondaryIndex node) {
		int midpoint = ScholarNode.order;
		String midJournal = node.journalAtIndex(midpoint);

		ScholarNodeSecondaryIndex newNode = new ScholarNodeSecondaryIndex(node.getParent());

		for (int i = midpoint + 1; i < node.journalCount(); i++) {
			newNode.addJournal(newNode.journalCount(), node.journalAtIndex(i));
		}

		for (int i = midpoint + 1; i <= node.journalCount(); i++) {
			ScholarNode child = node.getChildrenAt(i);
			newNode.addChild(newNode.getAllChildren().size(), child);
			child.setParent(newNode);
		}

		node.getAllJournals().subList(midpoint, node.getAllJournals().size()).clear();
		node.getAllChildren().subList(midpoint + 1, node.getAllChildren().size()).clear();

		if (node.getParent() == null) {
			ScholarNodeSecondaryIndex newRoot = new ScholarNodeSecondaryIndex(null);
			newRoot.addJournal(0, midJournal);
			newRoot.addChild(0, node);
			newRoot.addChild(1, newNode);
			node.setParent(newRoot);
			newNode.setParent(newRoot);
			secondaryRoot = newRoot;
		} else {
			ScholarNodeSecondaryIndex parent = (ScholarNodeSecondaryIndex) node.getParent();
			int insertPosition = 0;
			while (insertPosition < parent.journalCount() &&
					parent.journalAtIndex(insertPosition).compareTo(midJournal) < 0) {
				insertPosition++;
			}

			parent.addJournal(insertPosition, midJournal);
			parent.addChild(insertPosition + 1, newNode);
			newNode.setParent(parent);

			if (parent.journalCount() > 2 * ScholarNode.order) {
				splitSecondaryIndex(parent);
			}
		}
	}

	private CengPaper searchPFJ(Integer paperId) {
		ScholarNode currentNode = primaryRoot;
		while (currentNode != null) {
			if (currentNode.getType() == ScholarNodeType.Internal) {
				ScholarNodePrimaryIndex indexNode = (ScholarNodePrimaryIndex) currentNode;
				int idx = 0;
				while (idx < indexNode.paperIdCount() && paperId >= indexNode.paperIdAtIndex(idx)) {
					idx++;
				}
				currentNode = indexNode.getChildrenAt(idx);
			} else {
				ScholarNodePrimaryLeaf leafNode = (ScholarNodePrimaryLeaf) currentNode;
				for (int i = 0; i < leafNode.paperCount(); i++) {
					if (leafNode.paperIdAtIndex(i).equals(paperId)) {
						return leafNode.paperAtIndex(i);
					}
				}
				return null;
			}
		}
		return null;
	}



}


