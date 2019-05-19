package com.github.jikoo.util;

public class Pair<L, R> {

	private L left;
	private R right;

	public Pair(L left, R right) {
		this.left = left;
		this.right = right;
	}

	public L getLeft() {
		return left;
	}

	public R getRight() {
		return right;
	}

	public void setLeft(L left) {
		this.left = left;
	}

	public void setRight(R right) {
		this.right = right;
	}

	@Override
	public int hashCode() {
		int hash = 1;
		hash = hash * 17 * left.hashCode();
		hash = hash * 17 * right.hashCode();
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!getClass().isInstance(obj)) {
			return false;
		}
		Pair other = (Pair) obj;
		return left.equals(other.left) && right.equals(other.right);
	}

}
