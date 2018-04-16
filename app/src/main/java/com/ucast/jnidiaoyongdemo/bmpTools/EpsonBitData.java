package com.ucast.jnidiaoyongdemo.bmpTools;

public class EpsonBitData {
	public int with = 384;
	public String stringDatas;
	public byte[] datasByte = null;
	public int getWith() {
		return with;
	}
	public void setWith(int with) {
		this.with = with;
	}
	public String getStringDatas() {
		return stringDatas;
	}
	public void setStringDatas(String stringDatas) {
		this.stringDatas = stringDatas.trim();
	}

	public byte[] getDatasByte() {
		return datasByte;
	}

	public void setDatasByte(byte[] datasByte) {
		this.datasByte = datasByte;
	}

	public byte[] getByteFromStringDatas() {
		if(this.stringDatas == null)
			return null;
		String [] bytes = this.stringDatas.trim().split(" ");
		byte [] data =new byte[bytes.length];
		int data_index = -1;
		for (int i = 0; i < bytes.length; i++) {
			int temp = -1;
			if(bytes[i].equals("00")){
				data_index ++;
				data[data_index] = 0x00;
				continue;
			}else if(bytes[i].equals("FF")){
				data_index ++;
				data[data_index] = (byte)0xFF;
				continue;
			}

			try {
				temp = Integer.parseInt(bytes[i].substring(0), 16);
				data_index ++;
				data[data_index] = (byte) temp;
			} catch (Exception e) {

			}
		}
		return data;
	}
	
	public void addStringDatas(String newDatas) {
		if (this.stringDatas == null) {
			this.stringDatas = newDatas.trim();
		}
		this.stringDatas = this.stringDatas + " " + newDatas.trim();
	}
	public void addDatasByte(byte[] newDatas) {
		if (this.datasByte == null){
			this.datasByte = newDatas;
		}
		byte[] tem = new byte[this.datasByte.length];
		System.arraycopy(this.datasByte,0,tem,0,this.datasByte.length);
		this.datasByte = new byte[newDatas.length + tem.length];
		System.arraycopy(tem,0,this.datasByte,0,tem.length);
		System.arraycopy(newDatas,0,this.datasByte,tem.length,newDatas.length);
	}


}
