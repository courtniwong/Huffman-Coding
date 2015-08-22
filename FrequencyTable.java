
public class FrequencyTable {
	
	Letter[] letters = new Letter[256];
	
	//	adds character to array if it doesn't exist, if the character does exist, the frequency is incremented 
	public void add(char c){
		if (letters[c] != null){
			letters[c].frequency++;
		}
		else{
			letters[c] = new Letter(c,0);
			letters[c].frequency++;
		}
	}
	
	//	prints array
	public void print(){
		for(int i = 0; i < letters.length; i++){
			if(letters[i] != null){
				System.out.println(letters[i]);
			}
		}
	}
}
