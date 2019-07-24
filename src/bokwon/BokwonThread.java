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

public class BokwonThread {
	// 대용량 파일 복원 프로그램
	public static void main(String[] args) throws IOException, InvalidKeyException, NoSuchAlgorithmException,
			NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {

		// 사용 변수

		final BufferedReader BR;// 백업 파일 정보를 가져올 버퍼 리더
		final String BACKPATH;// 백업파일이 위치한 곳.
		final String PICYEAR; // 해당 사진의 연월. 예 : 201701
		final String BACKFILENAME;// 백업 파일 이름
		final String AESCode;// AES 암호화 코드

		// 년월 디렉토리 안의 Success 디렉토리명
		final String SUCCESS_DIR;
		final File SUCCESS_DIR_FILE;
		final String SUCCESS_REG_DIR;
		final File SUCCESS_REG_DIR_FILE;
		final String SUCCESS_LIVE_DIR;
		final File SUCCESS_LIVE_DIR_FILE;

		// 년월 디렉토리 안의 Fail 디렉토리명
		final String FAIL_DIR;
		final File FAIL_DIR_FILE;
		final String FAIL_REG_DIR;
		final File FAIL_REG_DIR_FILE;
		final String FAIL_LIVE_DIR;
		final File FAIL_LIVE_DIR_FILE;

		// 컬럼 변수
		final int IMG_REG_COL;// reg 이미지 컬럼
		final int IMG_LIVE_COL;// live 이미지 컬럼
		final int IMG_EXIST;// 이미지가 있는지 확인하는 컬럼
		final int IMG_SUCCESS;// 확인 성공 여부 컬럼
		
		
		//유저 변경 사항 시작///////////////////////////////////////////////////////////////////////////////////////////
		
		// 변수 초기화. 컬럼 아키텍쳐 추출해서 확인후 변경.
		BACKPATH = "D:\\PictureBackup"; //백업 파일이 위치한 디렉토리 1
		PICYEAR = "201704";//백업파일이 위치한 디렉토리 2
		BACKFILENAME = "backup.back";//백업파일 명
		AESCode = "s8LiEwT3if89Yq3i90hIo3HepqPfOhVd";//AES키

		IMG_REG_COL = 15;// reg 이미지 컬럼
		IMG_LIVE_COL = 16;// live 이미지 컬럼
		IMG_EXIST = 11;// 이미지가 있는지 확인하는 컬럼
		IMG_SUCCESS = 18;// 확인 성공 여부 컬럼
		
		//유저 변경 사항 끝///////////////////////////////////////////////////////////////////////////////////////////

		
		// 자동 생성 디렉토리
		// (BACKPATH)/(PICYEAR)/Success/Reg
		SUCCESS_DIR = BACKPATH + "\\" + PICYEAR + "\\Success";
		SUCCESS_REG_DIR = SUCCESS_DIR + "\\Reg";
		SUCCESS_LIVE_DIR = SUCCESS_DIR + "\\Live";

		SUCCESS_DIR_FILE = new File(SUCCESS_DIR);
		SUCCESS_REG_DIR_FILE = new File(SUCCESS_REG_DIR);
		SUCCESS_LIVE_DIR_FILE = new File(SUCCESS_LIVE_DIR);

		// (BACKPATH)/(PICYEAR)/Fail/Reg
		FAIL_DIR = BACKPATH + "\\" + PICYEAR + "\\Fail";
		FAIL_REG_DIR = FAIL_DIR + "\\Reg";
		FAIL_LIVE_DIR = FAIL_DIR + "\\Live";

		FAIL_DIR_FILE = new File(FAIL_DIR);
		FAIL_REG_DIR_FILE = new File(FAIL_REG_DIR);
		FAIL_LIVE_DIR_FILE = new File(FAIL_LIVE_DIR);

		// Success 내부 디렉토리 생성
		if (!SUCCESS_DIR_FILE.exists()) {
			// Success 디렉토리 생성
			try {
				SUCCESS_DIR_FILE.mkdir();
			} catch (Exception e) {
				e.getStackTrace();
			}
		}
		if (!SUCCESS_REG_DIR_FILE.exists()) {
			// Success/reg 디렉토리 생성
			try {
				SUCCESS_REG_DIR_FILE.mkdir();
			} catch (Exception e) {
				e.getStackTrace();
			}
		}
		if (!SUCCESS_LIVE_DIR_FILE.exists()) {
			// Success/Live 디렉토리 생성
			try {
				SUCCESS_LIVE_DIR_FILE.mkdir();
			} catch (Exception e) {
				e.getStackTrace();
			}
		}

		// Fail 내부 디렉토리 생성
		if (!FAIL_DIR_FILE.exists()) {
			// Fail 디렉토리 생성
			try {
				FAIL_DIR_FILE.mkdir();
			} catch (Exception e) {
				e.getStackTrace();
			}
		}
		if (!FAIL_REG_DIR_FILE.exists()) {
			// Fail/Reg 디렉토리 생성
			try {
				FAIL_REG_DIR_FILE.mkdir();
			} catch (Exception e) {
				e.getStackTrace();
			}
		}
		if (!FAIL_LIVE_DIR_FILE.exists()) {
			// Fail/Live 디렉토리 생성
			try {
				FAIL_LIVE_DIR_FILE.mkdir();
			} catch (Exception e) {
				e.getStackTrace();
			}
		}

		// 파일에서 읽어 들인다.
		BR = new BufferedReader(
				new InputStreamReader(new FileInputStream(BACKPATH + "\\" + PICYEAR + "\\" + BACKFILENAME), "euc-kr"));// 리딩
																														// 객체
																														// 초기화
		String line;// 버퍼 리더에서 한줄씩 읽어온 스트링

		while ((line = BR.readLine()) != null) {

			try {
				String[] sarr = line.split("\t"); // 버퍼리더에서 가져온 스트링을 \t단위로 컬럼을 나눔.
				/*
				 * 주요 컬럼 :
				 * 
				 * 공통 : 1번 : 대상 식별 번호, 2번 : 대상 이름, 15번 : 이미지
				 * 
				 * 01, 02 파일 : 23번 : 이미지, 17번 : 매칭 성공 여부
				 * 
				 * 03, 04 파일 : 16번 : 이미지, 18번 : 매칭 성공 여부
				 */

				if (sarr[IMG_EXIST].equals("I")) {// 이미지 유무 확인
					if (sarr[IMG_SUCCESS].equals("Y")) {
						// 매칭 성공시

						if (!sarr[IMG_REG_COL].equals("\0")) {
							// reg컬럼 이미지가 \0이 아니면

							// Success-Reg 디렉토리 생성
							String succRegImgDir = SUCCESS_REG_DIR + "\\" + sarr[1] + "_" + sarr[2];// 각 이미지별 디렉토리 생성
							File succRegImgDirFile = new File(succRegImgDir);

							if (!succRegImgDirFile.exists()) {
								try {
									succRegImgDirFile.mkdir(); // 폴더 생성합니다.
								} catch (Exception e) {
									e.getStackTrace();
								}
							}

							// TODO : 이미지 인덱스 확인, 로직 변경 요망
							int index = 0;
							String sucRegImgName;
							File imgFile;

							while (true) {// 동일인물 사진 파일에 대한 식별
								sucRegImgName = succRegImgDir + "\\" + sarr[1] + "_" + sarr[2] + "_" + PICYEAR + "_"
										+ "(" + index + ")" + ".jpg";
								imgFile = new File(sucRegImgName);
								if (imgFile.isFile()) {// 해당 인덱스의 파일이 이미 존재하면 인덱스를 1 올리고 다시 판별.
									index++;
								} else {
									break;
								}
							}
							System.out.println(sucRegImgName);

							// 이미지 추출
							extractColumnImg(AESCode, sarr[IMG_REG_COL], imgFile);

						}

					}
					if (!sarr[IMG_LIVE_COL].equals("\0")) {
						// live 컬럼 이미지가 \0이 아니면

						// Success-Live 디렉토리 생성
						String succLiveImgDir = SUCCESS_LIVE_DIR + "\\" + sarr[1] + "_" + sarr[2];// 각 이미지별 디렉토리 생성
						File succLiveImgDirFile = new File(succLiveImgDir);

						if (!succLiveImgDirFile.exists()) {
							try {
								succLiveImgDirFile.mkdir(); // 폴더 생성합니다.
							} catch (Exception e) {
								e.getStackTrace();
							}

							// TODO : 이미지 인덱스 확인, 로직 변경 요망
							int index = 0;
							String sucLiveImgName;
							File imgFile;

							while (true) {// 동일인물 사진 파일에 대한 식별
								sucLiveImgName = succLiveImgDir + "\\" + sarr[1] + "_" + sarr[2] + "_" + PICYEAR + "_"
										+ "(" + index + ")" + ".jpg";
								imgFile = new File(sucLiveImgName);
								if (imgFile.isFile()) {// 해당 인덱스의 파일이 이미 존재하면 인덱스를 1 올리고 다시 판별.
									index++;
								} else {
									break;
								}
							}
							System.out.println(sucLiveImgName);

							// 이미지 추출
							extractColumnImg(AESCode, sarr[IMG_LIVE_COL], imgFile);

						}

					} else {
						// 매칭 실패시
						if (!sarr[IMG_REG_COL].equals("\0")) {
							// reg컬럼 이미지가 \0이 아니면

							// Fail-Reg 디렉토리 생성
							String failRegImgDir = FAIL_REG_DIR + "\\" + sarr[1] + "_" + sarr[2];// 각 이미지별 디렉토리 생성
							File failRegImgDirFile = new File(failRegImgDir);

							if (!failRegImgDirFile.exists()) {
								try {
									failRegImgDirFile.mkdir(); // 폴더 생성합니다.
								} catch (Exception e) {
									e.getStackTrace();
								}
							}

							// TODO : 이미지 인덱스 확인, 로직 변경 요망
							int index = 0;
							String failRegImgName;
							File imgFile;

							while (true) {// 동일인물 사진 파일에 대한 식별
								failRegImgName = failRegImgDir + "\\" + sarr[1] + "_" + sarr[2] + "_" + PICYEAR + "_"
										+ "(" + index + ")" + ".jpg";
								imgFile = new File(failRegImgName);
								if (imgFile.isFile()) {// 해당 인덱스의 파일이 이미 존재하면 인덱스를 1 올리고 다시 판별.
									index++;
								} else {
									break;
								}
							}
							System.out.println(failRegImgName);

							// 이미지 추출
							extractColumnImg(AESCode, sarr[IMG_REG_COL], imgFile);
						}

						if (!sarr[IMG_LIVE_COL].equals("\0")) {
							// live 컬럼 이미지가 \0이 아니면

							// Fail-Live 디렉토리 생성
							String failLiveImgDir = SUCCESS_LIVE_DIR + "\\" + sarr[1] + "_" + sarr[2];// 각 이미지별 디렉토리 생성
							File failLiveImgDirFile = new File(failLiveImgDir);

							if (!failLiveImgDirFile.exists()) {
								try {
									failLiveImgDirFile.mkdir(); // 폴더 생성합니다.
								} catch (Exception e) {
									e.getStackTrace();
								}
							}

							// TODO : 이미지 인덱스 확인, 로직 변경 요망
							int index = 0;
							String failLiveImgName;
							File imgFile;

							while (true) {// 동일인물 사진 파일에 대한 식별
								failLiveImgName = failLiveImgDir + "\\" + sarr[1] + "_" + sarr[2] + "_" + PICYEAR + "_"
										+ "(" + index + ")" + ".jpg";
								imgFile = new File(failLiveImgName);
								if (imgFile.isFile()) {// 해당 인덱스의 파일이 이미 존재하면 인덱스를 1 올리고 다시 판별.
									index++;
								} else {
									break;
								}
							}
							System.out.println(failLiveImgName);

							// 이미지 추출
							extractColumnImg(AESCode, sarr[IMG_LIVE_COL], imgFile);

						}

					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		System.out.println("-------------------추출 완료----------------------");
		BR.close();
	}

	private static void extractColumnImg(String AESCode, String sarrColString, File imgFile) {
		// 이미지 파일 추출 메소드
		try {
			if (!sarrColString.equals("\0")) {
				byte[] bt = byteArrDecode(sarrColString, AESCode); // 암호화된 이미지를 디코드
				FileOutputStream fos = new FileOutputStream(imgFile);// 새파일 생성
				fos.write(bt);// 생성된 파일에 바이너리 바이트 덮어쓰기.
				fos.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static byte[] byteArrDecode(String str, String key)
			throws java.io.UnsupportedEncodingException, NoSuchAlgorithmException, NoSuchPaddingException,
			InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
		// AES 암호 해독 메소드, 아파치 코덱 필요.(org.apache.commons.codec.binary.Base64를 구글링해서 다운받고
		// 프로젝트에 빌드패스할것.)

		byte[] byteIv = { 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
				0x00 };

		byte[] textBytes = Base64.decodeBase64(str);
		AlgorithmParameterSpec ivSpec = new IvParameterSpec(byteIv);
		SecretKeySpec newKey = new SecretKeySpec(key.getBytes("UTF-8"), "AES");
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		cipher.init(Cipher.DECRYPT_MODE, newKey, ivSpec);
		return cipher.doFinal(textBytes);
	}

}