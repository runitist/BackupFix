package bokwon;

public class Bokwon {
	//복원 프로그램 메인

	public static void main(String[] args) {
		//따로 폴더 여부 확인하고 만드는 여부는 만들지 않아서 폴더 경로를 맞춰줘야함.
		//코드가 더러운 점은 이해를...
		Thread t1 = new Thread(new BokwonThread("201703"));//쓰레드. 1,2번과 3,4번은 이미지 컬럼이 다르니 2개씩 실행
		Thread t2 = new Thread(new BokwonThread("201704"));//1,2번은 15,23컬럼, 3,4번은 15,16번 컬럼이 이미지 파일.
		
		t1.start();
		t2.start();
		
	}

}