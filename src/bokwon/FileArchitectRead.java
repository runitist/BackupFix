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

		final String PICYEAR = "201704"; // 해당 사진의 연월.
		final String FILEPATH = "D:\\PictureBackup\\" + PICYEAR + "\\backup.back"; // 읽어들일 대용량 파일의 위치.
		final String DOCPATH = "D:\\PictureBackup\\" + PICYEAR + "\\"; // jpg를 출력할 위치

		// 파일에서 읽어 들인다.
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(FILEPATH), "euc-kr"));// 백업 파일
										
		String line = null;// 백업 파일 내 한 행 스트링
		String[] sarr;// line 스트링을 tab을 기준으로 배열화
		String docname; // 이미지 파일 이름

		docname = DOCPATH+"architecture.txt";
		File docfile = new File(docname);
		BufferedWriter bw = new BufferedWriter(new FileWriter(docfile, true));
		PrintWriter pw = new PrintWriter(bw, true);
		int index = 0;
		
		while ((line = br.readLine()) != null) {
			System.out.println(index);
			if(index > 100) {
				break;
			}
			sarr = line.split("\t");
			for(int i =0; i < sarr.length; i++) {
				pw.write(i+" : "+sarr[i] +"\n");
			}
			pw.write("\n//////////////////////////////////////////////////////////////////\n");
			index++;
		}
		
		pw.close();
		br.close();
	}

}