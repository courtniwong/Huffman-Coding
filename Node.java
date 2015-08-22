
public class Node implements Comparable<Node>{
	public Node left, right;
	public Letter letter;
	
	public Node(){
		
	}
	
	public Node(Letter letter) {
		this.letter = letter;
	}

	public Node getLeft() {
		return left;
	}

	public void setLeft(Node left) {
		this.left = left;
	}

	public Node getRight() {
		return right;
	}

	public void setRight(Node right) {
		this.right = right;
	}

	public Letter getLetter() {
		return letter;
	}

	public void setLetter(Letter letter) {
		this.letter = letter;
	}

	public int compareTo(Node node) {
		return letter.compareTo(node.letter);
	}
	
	public boolean isLeaf() {
		if(this.left == null && this.right == null){
			return true;
		}
		else{
			return false;	
		}
	}

	public String toString() {
		return letter != null ? letter.toString() : "null";
	}
	
}
