package com.example.filewriteintentservice;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.util.Log;

public class FileWriteIntentService extends IntentService{
	
	private static final  String TAG = 
			FileWriteIntentService.class.getSimpleName();
	
	/**
	 * ファイル書き込みタイプの指定
	 */
	//指定なし(内蔵ストレージ優先)
	public static final int TYPE_NONE = 0;
	//アプリケーション専用領域
	public static final int TYPE_APPLICATION_AREA = 1;
	//内蔵ストレージ領域
	public static final int TYPE_EXTERNAL_STORAGE = 2;
	
	/**
	 * IntentのExtra
	 */
	//ファイルの書き込みタイプ
	public static final String EXTRA_WRITE_TYPE = 
			FileWriteIntentService.class.getCanonicalName() + ".WRITE_TYPE";
	//ファイル名
	public static final String EXTRA_FILE_NAME = 
			FileWriteIntentService.class.getCanonicalName() + ".FILE_NAME";
	//書き込み内容
	public static final String EXTRA_WRITE_VALUE = 
			FileWriteIntentService.class.getCanonicalName() + ".WRITE_VALUE";

	/**
	 * コンストラクタ
	 * @param name
	 */
	public FileWriteIntentService(String name) {
		super(name);
		// TODO 自動生成されたコンストラクター・スタブ
	}
	
	/**
	 * コンストラクタ
	 */
	public FileWriteIntentService() {
		super("MyIntentService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		
		//ファイル名の取得
		String fileName = intent.getStringExtra(EXTRA_FILE_NAME);
		if(null == fileName) {
			Log.e(TAG, "file name is null.");
			return;
		}
		
		//書き込み内容の取得
		String writeValue = intent.getStringExtra(EXTRA_WRITE_VALUE);
		if(null == writeValue) {
			Log.e(TAG, "write value is null.");
			return;
		}
		if(writeValue.isEmpty()) {
			Log.w(TAG, "write value length is 0");
			return;
		}
		
		//ファイル書き込みタイプによる振り分け
		int writeType = intent.getIntExtra(EXTRA_WRITE_TYPE, TYPE_NONE);
		
		switch(writeType) {
		//指定なし(内蔵ストレージ優先)
		case TYPE_NONE:
			try {
				//内蔵ストレージ領域へ書き込み試行
				if(!writeFile2ExternalStorage(getApplicationContext(),
						fileName, writeValue)) {
					//失敗した場合はアプリケーション専用領域で試行
					writeFile2ExternalStorage(getApplicationContext(),
						fileName, writeValue);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			break;
			
		//アプリケーション専用領域
		case TYPE_APPLICATION_AREA:
			try {
				this.writeFile2ApplicationArea(getApplicationContext(),
						fileName, writeValue);
			} catch (IOException e) {
				e.printStackTrace();
			}
			break;
			
		//内蔵ストレージ領域
		case TYPE_EXTERNAL_STORAGE:
			try {
				this.writeFile2ExternalStorage(getApplicationContext(),
						fileName, writeValue);
			} catch (IOException e) {
				e.printStackTrace();
			}
			break;
			
		default:
			Log.e(TAG, "unknown write_type.");
			break;
		}
	}
	
	/**
	 * アプリケーション専用領域への書き込み
	 * @param context
	 * @param fileName
	 * @param writeValue
	 * @throws IOException 
	 * @return true:success / false:failed
	 */
	private boolean writeFile2ApplicationArea(Context context,
			String fileName,String writeValue) throws IOException {
		boolean ret = false;
		
		FileOutputStream output = null;
		try {
			output = context.openFileOutput(fileName,
					MODE_PRIVATE | MODE_APPEND);
			output.write(writeValue.getBytes());
			ret = true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			ret = false;
		} catch (IOException e) {
			e.printStackTrace();
			ret = false;
		} finally {
			if(null != output) output.close();
		}
		
		return ret;
	}
	
	/**
	 * 内蔵ストレージ領域への書き込み
	 * @param context
	 * @param fileName
	 * @param writeValue
	 * @throws IOException 
	 * @return true:success / false:failed
	 */
	private boolean writeFile2ExternalStorage(Context context,
			String fileName,String writeValue) throws IOException {
		boolean ret = false;
		
		//内蔵ストレージの絶対パスを取得
		File externalStorageDir = Environment.getExternalStorageDirectory();
		if(null == externalStorageDir) {
			Log.e(TAG, "getExternalStorageDirectory() is null.");
			return ret;
		}
		
		//絶対パスの存在有無
		if(!externalStorageDir.exists()) {
			Log.e(TAG, "exists() is false.");
			return ret;	
		}
		
		//書き込み可能の有無
		if(!externalStorageDir.canWrite()) {
			Log.e(TAG, "canWrite() is false.");
			return ret;	
		}
		
		//ファイル格納先ディレクトリのパス生成
		StringBuilder dirPath = new StringBuilder(0);
		dirPath.append(externalStorageDir.getAbsolutePath());
		dirPath.append(File.separator);
		dirPath.append(context.getPackageName());
		
		//ファイル格納先ディレクトリの生成
		File fileDir = new File(dirPath.toString());
		if(!fileDir.exists()) {
			//ディレクトリ未作成の場合は作成
			fileDir.mkdir();
		}
		
		//フルパスの生成
		StringBuilder fullPath = new StringBuilder(0);
		fullPath.append(dirPath.toString());
		fullPath.append(File.separator);
		fullPath.append(fileName);
		
		//ファイルへ書き込み
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(fullPath.toString(),true),"UTF-8"));
			writer.write(writeValue);
			ret = true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			ret = false;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			ret = false;
		} finally {
			if(null != writer) writer.close();
		}
		
		return ret;
	}
	

}
