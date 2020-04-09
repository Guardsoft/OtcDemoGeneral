package com.otc.model.response.send;

public class Transaction{
	private String identifier;

	public void setIdentifier(String identifier){
		this.identifier = identifier;
	}

	public String getIdentifier(){
		return identifier;
	}

	@Override
 	public String toString(){
		return 
			"Transaction{" + 
			"identifier = '" + identifier + '\'' + 
			"}";
		}
}
