package bokwon;

public class Bokwon {

	public static void main(String[] args) {
		Thread t1 = new Thread(new BokwonThread("201701"));
		Thread t2 = new Thread(new BokwonThread("201702"));
		
		t1.start();
		t2.start();
		
	}

	

}