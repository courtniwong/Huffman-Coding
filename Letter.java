
public class Letter implements Comparable<Letter> {
	char symbol;
	int frequency;
	String code;

	
	public Letter() {
		
	}
	
	public Letter(char symbol){
		this.symbol = symbol;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Letter(char symbol, int frequency) {
		this.symbol = symbol;
		this.frequency = frequency;
	}
	
	public char getSymbol() {
		return symbol;
	}
	public void setSymbol(char symbol) {
		this.symbol = symbol;
	}
	public int getFrequency() {
		return frequency;
	}
	public void setFrequency(int frequency) {
		this.frequency = frequency;
	}

	public String toString() {
		return "symbol= " + symbol + ", frequency= " + frequency + ", code = " + code;
	}

	public int compareTo(Letter o) {
		if(this.frequency > o.frequency){
			return 1;
		}
		else if(this.frequency < o.frequency){
			return -1;
		}
		else{
			return 0;
		}	
	}	
}
