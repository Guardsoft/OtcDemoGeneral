package com.otc.model.request.cancel;

public class Header{
	private String externalId;

	public void setExternalId(String externalId){
		this.externalId = externalId;
	}

	public String getExternalId(){
		return externalId;
	}

	@Override
 	public String toString(){
		return 
			"Header{" + 
			"externalId = '" + externalId + '\'' + 
			"}";
		}
}
