package bokwon;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.AlgorithmParameterSpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

public class BokwonThread implements Runnable {
	//멀티쓰레드 구현 클래스

	String PICYEAR; // 해당 사진의 연월. 201701
	
	BokwonThread(String picyear){
		PICYEAR = picyear;
	}
	
	@Override
	public void run() {
		try {
			bokbok(PICYEAR);
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void bokbok(String PICYEAR) throws IOException, InvalidKeyException, NoSuchAlgorithmException,
			NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
		String FILEPATH;
		String IMGPATH;
		String REGPATH;
		String ROOTPATH;
		FILEPATH = "D:\\PictureBackup\\" + PICYEAR + "\\backup.back"; // 읽어들일 대용량 파일의 위치.
		IMGPATH = "D:\\PictureBackup\\" + PICYEAR + "\\img\\gongimg"; // jpg를 출력할 위치
		REGPATH = "D:\\PictureBackup\\" + PICYEAR + "\\img\\regimg"; // jpg를 출력할 위치
		ROOTPATH = "D:\\PictureBackup\\" + PICYEAR + "\\img";

		// 파일에서 읽어 들인다.
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(FILEPATH), "euc-kr"));// 백업 파일
																												// 리더
		FileOutputStream fos = null;
		String line = null;// 백업 파일 내 한 행 스트링
		String[] sarr;// line 스트링을 tab을 기준으로 배열화
		String imgname; // 15컬럼 이미지 파일 이름
		String mkimgPath; // 이미지 파일 디렉토리
		
		String mkregPath; //23컬럼 이미지 파일 디렉토리
		String imgname23; //23컬럼 이미지 파일 이름

		while ((line = br.readLine()) != null) {
			try {
				int index = 0;// 동일인물 이미지에 대한 인덱스
				sarr = line.split("\t");
				/*
				 * 1번 : 대상 식별 번호, 2번 : 대상 이름, 15번 : 이미지
				 */

				if (!sarr[11].equals("O")) {// 이미지 식별
					imgname = "";
					mkimgPath = "";
					mkimgPath = IMGPATH + sarr[2].trim() + " " + sarr[1].trim() + " " + PICYEAR;
					mkregPath = REGPATH + sarr[2].trim() + " " + sarr[1].trim() + " " + PICYEAR;

					
					File Folder = new File(mkimgPath);
					File REGFolder = new File(mkregPath);
					File RootFolder = new File(ROOTPATH);
					
					// 해당 디렉토리가 없을경우 디렉토리를 생성합니다.
					if (!RootFolder.exists()) {
						try {
							RootFolder.mkdir(); // 폴더 생성합니다.
						} catch (Exception e) {
							e.getStackTrace();
						}
					}

					// 해당 디렉토리가 없을경우 디렉토리를 생성합니다.
					if (!Folder.exists()) {
						try {
							Folder.mkdir(); // 폴더 생성합니다.
						} catch (Exception e) {
							e.getStackTrace();
						}
					}
					if (!REGFolder.exists()) {
						try {
							REGFolder.mkdir(); // 폴더 생성합니다.
						} catch (Exception e) {
							e.getStackTrace();
						}
					}

					while (true) {// 동일인물 사진 파일에 대한 식별
						imgname = mkimgPath + "\\"+ sarr[2].trim() + " " + sarr[1].trim() + " " + PICYEAR
								+ " " + " (" + index + ")" + ".jpg";
						File f = new File(imgname);
						if (f.isFile()) {// 해당 인덱스의 파일이 이미 존재하면 인덱스를 1 올리고 다시 판별.
							index++;
						} else {
							imgname23 = mkregPath + "\\"+ sarr[2].trim() + " " + sarr[1].trim() + " " + PICYEAR
									+ " " + " (" + index + ")" + ".jpg";
							break;
						}
					}
					System.out.println(imgname);
					System.out.println(imgname23+"(23)");
					
					//15번 컬럼의 이미지 추출
					try {
						byte[] bt = byteArrDecode(sarr[15], "s8LiEwT3if89Yq3i90hIo3HepqPfOhVd"); // 암호화된 이미지를 디코드
						fos = new FileOutputStream(imgname);// 새파일 생성
						fos.write(bt);// 생성된 파일에 바이너리 바이트 덮어쓰기.
					}catch (Exception e) {
						e.printStackTrace();
						// 빈 폴더면 지우기
						deleteEmptyDir(Folder);
					}
					///
					
					//23번 컬럼의 이미지 추출
					try {
						byte[] bt = byteArrDecode(sarr[16], "s8LiEwT3if89Yq3i90hIo3HepqPfOhVd"); // 암호화된 이미지를 디코드
						fos = new FileOutputStream(imgname23);// 새파일 생성
						fos.write(bt);// 생성된 파일에 바이너리 바이트 덮어쓰기.
					}catch (Exception e) {
						e.printStackTrace();
						// 빈 폴더면 지우기
						deleteEmptyDir(Folder);
					}
					///

				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		System.out.println("--------------복구 완료---------------");
		br.close();
		fos.close();
	}

	public byte[] byteArrDecode(String str, String key)
			throws java.io.UnsupportedEncodingException, NoSuchAlgorithmException, NoSuchPaddingException,
			InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {

		byte[] byteIv = { 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
				0x00 };

		byte[] textBytes = Base64.decodeBase64(str);
		AlgorithmParameterSpec ivSpec = new IvParameterSpec(byteIv);
		SecretKeySpec newKey = new SecretKeySpec(key.getBytes("UTF-8"), "AES");
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		cipher.init(Cipher.DECRYPT_MODE, newKey, ivSpec);
		return cipher.doFinal(textBytes);
	}

	public void deleteEmptyDir(File file) {
		// 빈 디렉토리 삭제
		if (file.isDirectory()) {

			for (File subFile : file.listFiles()) {
				if (subFile.isDirectory()) {
					deleteEmptyDir(subFile);
				}
			}
			if (file.listFiles().length == 0) {
				file.delete();
			}
		}
	}

	

}