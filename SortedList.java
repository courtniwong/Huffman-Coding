
public class SortedList<T extends Comparable<T>>{
	private Node first;
	
	class Node{
		T data;
		Node next;
	}
		
	SortedList(){
	}

	//	inserts node into sorted linked list
	public void insert(T info){
		Node previous = null;
		Node current = first;
		Node newNode = new Node();
		newNode.data = info;

		while(current != null && current.data.compareTo(newNode.data) < 0){
			if(current.data.compareTo(newNode.data) < 0 ){
				previous = current;
				current = current.next;
			}
		}
		if(previous == null){
			first = newNode;
		}
		else{
			previous.next = newNode;
		}
		newNode.next = current;
	}
	
	//	removes the first node, used to make huffman tree
	T removeFirst(){
		Node tmp = new Node();
		tmp = first;
		first = tmp.next;
		return tmp.data;
	}
	
	public void print(){
		Node tmp = new Node();
		tmp = first;
		while (tmp != null){
			System.out.println(tmp.data);
			tmp = tmp.next;
		}
	}
	public boolean isEmpty(){
		if(first == null){
			return true;
		}
		else{
			return false;
		}
	}
}


