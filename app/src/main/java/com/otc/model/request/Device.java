package com.otc.model.request;

public class Device{
	private String serialNumber;
	private boolean reloadKeys;

	public void setSerialNumber(String serialNumber){
		this.serialNumber = serialNumber;
	}

	public String getSerialNumber(){
		return serialNumber;
	}

	public void setReloadKeys(boolean reloadKeys){
		this.reloadKeys = reloadKeys;
	}

	public boolean isReloadKeys(){
		return reloadKeys;
	}

	@Override
 	public String toString(){
		return 
			"Device{" + 
			"serialNumber = '" + serialNumber + '\'' + 
			",reloadKeys = '" + reloadKeys + '\'' + 
			"}";
		}
}
