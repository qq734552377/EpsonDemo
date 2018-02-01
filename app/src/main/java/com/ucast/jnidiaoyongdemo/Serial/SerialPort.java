/*
 * Copyright 2009 Cedric Priscal
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */

package com.ucast.jnidiaoyongdemo.Serial;

import android.util.Log;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class SerialPort {
	private static final String TAG = "SerialPort";
	/*
	 * Do not remove or rename the field mFd: it is used by native method close();
	 */
	private FileDescriptor mFd;
	private FileInputStream mFileInputStream;
	private FileOutputStream mFileOutputStream;

	public SerialPort(File device, int baudrate, int flags) throws SecurityException, IOException {

		/* Check access permission */
		if (!device.canRead() || !device.canWrite()) {
			try {
				/* Missing read/write permission, trying to chmod the file */
				Process su;
				su = Runtime.getRuntime().exec("/system/bin/su");
				String cmd = "chmod 666 " + device.getAbsolutePath() + "\n"
						+ "exit\n";
				su.getOutputStream().write(cmd.getBytes());
				if ((su.waitFor() != 0) || !device.canRead()
						|| !device.canWrite()) {
					throw new SecurityException("等待返回不为0");
				}
			} catch (Exception e) {
				throw new SecurityException("获取运行时错误");
			}
		}

//		mFd = open(device.getAbsolutePath(), baudrate, flags);
		mFd = openPrint(device.getAbsolutePath(), baudrate, flags);
		Log.e("calm", "------mfd------"+mFd);

		if (mFd == null) {
			Log.e(TAG, "native open returns null");
			throw new IOException("打开设备串口错误，错误原因大概没找到");
		}
		mFileInputStream = new FileInputStream(mFd);
		mFileOutputStream = new FileOutputStream(mFd);
	}
	 public void closeSerialPort(){
		 close();
		 Log.e("calm","-----关闭串口-------");
	 }

	// Getters and setters
	public InputStream getInputStream() throws IOException {

		return mFileInputStream;
	}

	public OutputStream getOutputStream() throws IOException {
		return mFileOutputStream;
	}

	// JNI
	private native static FileDescriptor openPrint(String path, int baudrate, int flags);
	private native static FileDescriptor open(String path, int baudrate, int flags);
	public native void close();
	static {
		System.loadLibrary("serial_port_c");
	}
}
