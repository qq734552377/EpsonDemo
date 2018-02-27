package com.ucast.jnidiaoyongdemo.bmpTools;

public class EpsonBitData {
	public int with = 384;
	public String datas;
	public int getWith() {
		return with;
	}
	public void setWith(int with) {
		this.with = with;
	}
	public String getDatas() {
		return datas;
	}
	public void setDatas(String datas) {
		this.datas = datas.trim();
	}

	public byte[] getByteDatas() {
		if(this.datas == null)
			return null;
		String [] bytes = this.datas.trim().split(" ");
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
	
	public void addDatas(String newDatas) {
		if (datas == null) {
			this.datas = newDatas.trim();
		}
		this.datas = this.datas + " " + newDatas.trim();
	}
}
