package bokwon;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class FileArchitectRead {

	public static void main(String[] args) throws IOException, InvalidKeyException, NoSuchAlgorithmException,
			NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
		// 백업파일내 구조를 알아보기위한 로직.
		// 해당 파일을 읽고, 컬럼별로 나눠서 architecture.txt 파일로 출력해줌.
		// 해당 텍스트 파일을 읽고 컬럼 분석이 가능.

		final String PICYEAR = "201704"; // 해당 사진의 연월.
		final String FILEPATH = "D:\\PictureBackup\\" + PICYEAR + "\\backup.back"; // 읽어들일 대용량 파일의 위치.
		final String DOCPATH = "D:\\PictureBackup\\" + PICYEAR; // jpg를 출력할 위치

		// 추출 제외할 컬럼
		// 1,2 번은 15, 23컬럼
		// 3,4 번은 15, 16컬럼
		final int EXCEPTCOL1 = 15;
		final int EXCEPTCOL2 = 16;

		// 추출할 갯수 설정
		final int EXTNUM = 1000;

		// 파일에서 읽어 들인다.
		final BufferedReader BR = new BufferedReader(new InputStreamReader(new FileInputStream(FILEPATH), "euc-kr"));// 백업
																														// 파일

		String line = null;// 백업 파일 내 한 행 스트링
		String[] sarr;// line 스트링을 tab을 기준으로 배열화
		final String DOCNAME = DOCPATH + "\\"+PICYEAR+"_architecture.txt"; // 이미지 파일 이름

		final File DOCFILE = new File(DOCNAME);
		final BufferedWriter BW = new BufferedWriter(new FileWriter(DOCFILE, true));
		final PrintWriter PW = new PrintWriter(BW, true);
		int index = 0;

		while ((line = BR.readLine()) != null) {
			System.out.println(index);

			if (index >= EXTNUM) {// 추출할 갯수 설정
				break;
			}

			sarr = line.split("\t");
			for (int i = 0; i < sarr.length; i++) {
				if (i != EXCEPTCOL1 && i != EXCEPTCOL2) {
					PW.write(i + " : " + sarr[i] + "\n");
				} else {
					PW.write(i + " : (Image)\n");
				}
			}
			PW.write("\n//////////////////////////////////////////////////////////////////\n");
			index++;
		}

		PW.close();
		BR.close();
	}

}