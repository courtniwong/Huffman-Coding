public class HuffmanTree {
	FrequencyTable frequencyTable = new FrequencyTable();
	String fileName;
	Node huffmanTree;
	int numNodes;


	HuffmanTree(String fileName) {
		this.fileName = fileName; 
	}


	//	reads in file, one character at a time and increments the 
	//	frequency of the character via its ASCII representation
	public void populate() {
		TextFile textFile = new TextFile(fileName, 'r');
		while (!textFile.EndOfFile()) {
			char c = textFile.readChar();
			frequencyTable.add(c);
		}
		textFile.close();
	}

	/*	creates nodes from elements in array and inserts them into 
		a sorted linked list
	 */
	public Node createHuffManTree() {
		SortedList<Node> newList = new SortedList<Node>();
		for (int i = 0; i < frequencyTable.letters.length; i++) {
			if (frequencyTable.letters[i] != null) {
				Node newNode = new Node(frequencyTable.letters[i]);
				newList.insert(newNode);
			}
		}

		/*	takes two smallest nodes from the sorted linked list and creates 
			a new node with the two smallest combined frequencies and sets 
			the two smallest nodes as it's left and right, then inserts the
			new node back into the sorted linked list. this continues until
			there is just one node, so the huffman tree is built
		 */
		huffmanTree = null;
		while (!newList.isEmpty()) {
			Node first = newList.removeFirst();
			Node second = null;
			if (!newList.isEmpty()) {
				second = newList.removeFirst();
				Letter letter = new Letter('-', first.letter.frequency
						+ second.letter.frequency);
				Node newNode = new Node(letter);
				newNode.setLeft(first);
				newNode.setRight(second);
				newList.insert(newNode);
			} else {
				huffmanTree = first;
				break;
			}
		}
		return huffmanTree;
	}

	/*	create codes/lookup table for the characters using the huffman tree
		adds 0 if node goes left and adds 1 if node goes right 
		the code is set when it reaches a leaf
	 */
	public void generateCode(Node tree, String code) {
		if (tree == null) {
			return;
		}
		generateCode(tree.left, code + "0");
		if (tree.isLeaf()) {
			tree.letter.setCode(code);
		}
		generateCode(tree.right, code + "1");
	}

	//	calls three previous methods and returns the array with the lookup table
	public Letter[] getCodes() {
		populate();
		huffmanTree = createHuffManTree();
		generateCode(huffmanTree, "");
		return frequencyTable.letters;
	}

	/*	reads in the input file character by character and appends the characters code 
		via lookup table to a string builder then returns the string of code
	 */
	public String encode(){
		StringBuilder code = new StringBuilder();
		TextFile textFile = new TextFile(fileName, 'r');
		while (!textFile.EndOfFile()) {
			char c = textFile.readChar();
			code.append(frequencyTable.letters[c].getCode());
		}
		textFile.close();
		return code.toString();
	}

	//	encodes file by writing bits into output file for decoding later
	public void encode(BinaryFile binaryOutput){
		TextFile textFile = new TextFile(fileName, 'r');
		while (!textFile.EndOfFile()) {
			char c = textFile.readChar();
			String code = frequencyTable.letters[c].getCode();
			for(int i = 0; i < code.length(); i++){
				if(code.charAt(i) == '0'){
					binaryOutput.writeBit(false);
				}
				else{
					binaryOutput.writeBit(true);
				}
			}
		}
		textFile.close();
	}
	
//	creates binary output file
	public void encode(String outputFileName){
		//	create output file
		BinaryFile onputFile = new BinaryFile(outputFileName, 'w');
		//	writes magic numbers
		onputFile.writeChar('H');
		onputFile.writeChar('F');
		int numNodes = countNodes();
		//	writes number of nodes of huffman tree
		onputFile.writeChar((char)numNodes); 
		//		System.out.println("numNodes: " + numNodes);
		serializeHuffmanTree(huffmanTree, onputFile);
		encode(onputFile);
		onputFile.close();
	}


	//	returns size of huffman tree
	public int countNodes(Node tree){
		if(tree == null){
			return 0;
		}
		int size = 1;
		size += countNodes(tree.left);
		size += countNodes(tree.right);
		return size;
	}

	public int countNodes(){
		return countNodes(huffmanTree);
	}


	//	encodes the huffman tree for decoding later
	public void serializeHuffmanTree(Node tree, BinaryFile file){
		if(tree == null){
			return;
		}
		//	writes 0 if leaf and the letter
		if(tree.isLeaf()){
			file.writeBit(false);
			file.writeChar(tree.letter.symbol);
		}
		//	writes 1 if interior node
		else{
			file.writeBit(true);
		}
		serializeHuffmanTree(tree.left, file);
		serializeHuffmanTree(tree.right, file);

	}

	public void serializeHuffmanTree(BinaryFile file){
		serializeHuffmanTree(huffmanTree, file);
	}

	public void PrintSerializeHuffmanTree(Node tree){
		if(tree == null){
			return;
		}
		//	writes 0 if leaf and the letter
		if(tree.isLeaf()){
			System.out.print("0");
			System.out.print(tree.letter.symbol);
		}
		//	writes 1 if interior node
		else{
			System.out.print("1");
		}
		PrintSerializeHuffmanTree(tree.left);
		PrintSerializeHuffmanTree(tree.right);


	}

	public void PrintSerializeHuffmanTree(){
		PrintSerializeHuffmanTree(huffmanTree);
	}
	
	/*	Creates the huffman tree by pre-order.
	 * Stops walking through the huffman tree when the number
	 * of nodes reaches zero because we will have reached the
	 * end of the tree.
	 * Reads in bit, if bit is 1, it is an interior node, else 
	 * the bit is a 0 and is a leaf and the letter is set to the 
	 * next char that is read in
	 */
	Node readPreorder(BinaryFile output){
		if(numNodes == 0){
			return null;
		}
		numNodes--;
		Node tmp;
		if(output.readBit()){
			tmp = new Node();
			tmp.left = readPreorder(output);
			tmp.right = readPreorder(output);
			return tmp;
		}
		else{
			//	leaf
			tmp = new Node();
			tmp.setLetter(new Letter(output.readChar()));
			return tmp;
		}

	}


	/*	Takes in the string of code from the file and uses the huffman 
	 * 	tree. Node goes left if '0' is read and right if '1'. When the node 
	 * 	hits a leaf, the letter stored is appended to the string builder 
	 */
	public String decode(String code){
		Node current = huffmanTree;
		StringBuilder decode = new StringBuilder();
		for(int i = 0; i < code.length(); i++){
			char c = code.charAt(i);
			if(c == '0'){
				current = current.getLeft();
			} else  {
				current = current.getRight();
			}

			if(current.isLeaf()){
				decode.append(current.getLetter().getSymbol());
				current = huffmanTree;
			}
		}
		return decode.toString();
	}


	/* uses the huffman tree to decode the data within the binary file
	 * when a leaf is reached, the letter contained in the node is written
	 * to the text file
	 */
	public void decodeFile(BinaryFile inputFile, TextFile outputFile){
		Node current = huffmanTree;
		while(!inputFile.EndOfFile()){
			boolean one = inputFile.readBit();
			if(one){
				current = current.getRight();
			} else  {
				current = current.getLeft();
			}

			if(current.isLeaf()){
				outputFile.writeChar(current.getLetter().getSymbol());
				current = huffmanTree;
			}
		}
	}


	/*	reads in the binary file and creates a text file 
	 * reads in magic numbers, if both magic numbers matches
	 * the H and F the number of nodes is read next
	 * then the huffman tree is created by called readPreorder()
	 * using the huffman tree, the data from the binary file is
	 * decoded and written into the text file by calling decodeFile().
	 * if the magic numbers don't pass and error message is printed 
	 * and the program exists
	 */
	public void decodeFile(String encodeFile, String decodeFile){
		BinaryFile inputFile = new BinaryFile(encodeFile, 'r');
		TextFile outputFile = new TextFile(decodeFile, 'w');
		char first = inputFile.readChar();
		char second = inputFile.readChar();
		if(first == 'H' && second == 'F'){
			System.out.println("Magic number passed");
			numNodes = inputFile.readChar();
			huffmanTree = readPreorder(inputFile);
			decodeFile(inputFile, outputFile);
		}
		else{
			System.err.println("Magic Number did not pass. File will not be decompressed.");
			System.exit(0);
		}
		inputFile.close();
		outputFile.close();
	}



	/*	counts bits of the huffman tree
	 * if the current node is not a leaf, it is an interior node
	 * so 1 is added to the bits total, if it is a leaf 9 is
	 * added the the bits total
	 */
	public int countBits(Node tree,int bits){

		if(tree == null){
			return bits;
		}
		if(tree.isLeaf()){
			bits += 9;
		}
		else{
			bits += 1;
		}

		bits = countBits(tree.left, bits);
		bits = countBits(tree.right, bits);
		return bits;
	}

	public int countBits(){
		return countBits(huffmanTree, 0);
	}


	//	returns the size of the uncompressed file
	public int getUncompressedSize(){
		int uncompressed = 0;
		for (int i = 0; i < frequencyTable.letters.length; i++) {
			if(frequencyTable.letters[i] != null){
				uncompressed += frequencyTable.letters[i].frequency;
			}
		}
		uncompressed = uncompressed * 8;
		return uncompressed;
	}


	//	returns the size of the compressed file
	public int getCompressedSize(){
		int compressed = 0;
		for (int i = 0; i < frequencyTable.letters.length; i++) {
			if(frequencyTable.letters[i] != null){
				compressed += (frequencyTable.letters[i].frequency * frequencyTable.letters[i].code.length());
			}
		}
		compressed += countBits();
		compressed += (16 + 32);

		int newCompressed = compressed;
		newCompressed = compressed % 8;
		if(newCompressed != 0){
			compressed = compressed / 8;
			compressed = compressed * 8;
			compressed += 8;
		}

		return compressed;
	}

	/* checks the size of the compressed and uncompressed files
	 * if the compressed is smaller, the file will be compressed
	 * else an error message is printed and the program exits
	 */
	public void checkSize(String inputFile){
		int uncompressed = getUncompressedSize();
		int compressed = getCompressedSize();

		if(compressed < uncompressed){
			encode(inputFile);
		}
		else{
			System.err.println("File cannot be compressed. The compressed file will be larger than original file.");
			System.exit(0);
			
		}
	}

	/* Reads in command line arguments
	 * The program exists if the length is less than or greater than 5,
	 * if not, if the argument equals to -c, -u, -v, or -f, the booleans
	 * are set to true. Else, the arguments are the input file and the 
	 * argument after is the output file. A new object of HuffmanTree is
	 * created and the getCodes() is called that calls on other methods 
	 * to populate my array, createHuffManTree() that creates the huffman
	 * tree and generateCodes() that creates the lookup table. 
	 * Depending on whether -c or -u is entered, we will compress or 
	 * decompress. 
	 * 
	 * If -c is entered with -f, we will compress regardless, 
	 * if not, we will only compress if the compressed file is smaller than
	 * the uncompressed. If -v is also entered, we will print out the 
	 * frequencies, huffman tree, and codes. 
	 * 
	 * If -u is entered, we will decompress regardless if -f is entered or
	 * not. If -v is entered, the hufffman tree will be printed
	 */
	public static void commandLineArgs(String args[]){
		Boolean compress = false;
		Boolean verbose = false;
		Boolean force = false;
		String inputFile = null;
		String outputFile = null;

		int j = 0;

		if(args.length < 3 || args.length > 5){
			System.err.println("There needs to be 3 command line arguments");
			System.exit(0);
		}
		for (int i = 0; i < args.length; i++){
			if (args[i].equals("-c")){
				compress = true;
			}
			else if (args[i].equals("-u")){
				compress = false;
			}
			else if (args[i].equals("-v")){
				verbose = true;
			}
			else if (args[i].equals("-f")){
				force = true;
			}

			else {
				inputFile = args[i];
				j = i + 1;
				break;
			}
		}

		outputFile = args[j];

		HuffmanTree h = new HuffmanTree(inputFile);
		h.getCodes();

		
		// conditions and method calls
		if(compress){
			if(!force){
				h.checkSize(outputFile);
			}
			else{
				h.encode(outputFile);
			}
			if(verbose){
				// print frequency of characters
				System.out.println("Frequencies: ");
				for (int i = 0; i < h.frequencyTable.letters.length; i++) {
					if(h.frequencyTable.letters[i] != null){
						System.out.println((int)h.frequencyTable.letters[i].symbol + "\t" + h.frequencyTable.letters[i].frequency);
					}
				}

				// print huffman tree
				System.out.println();
				System.out.println("Huffman Tree: ");
				h.PrintSerializeHuffmanTree();

				// print codes
				System.out.println();
				System.out.println();
				System.out.println("Codes: ");

				for (int i = 0; i < h.frequencyTable.letters.length; i++) {
					if(h.frequencyTable.letters[i] != null){
						System.out.println((int)h.frequencyTable.letters[i].symbol + "\t" + h.frequencyTable.letters[i].code);
					}
				}

				// print size of uncompressed and compressed files
				System.out.println();
				System.out.println("Uncompressed file: " + h.getUncompressedSize());
				System.out.println("Compressed file: " + h.getCompressedSize());
			}
		}
		else if(!compress){
			h.decodeFile(inputFile,outputFile);
			if(verbose){
				// only prints huffman tree
				System.out.println("Huffman Tree: ");
				h.PrintSerializeHuffmanTree();
			}
		}
	}

	public static void main(String[] args) {
		commandLineArgs(args);
	}

}
