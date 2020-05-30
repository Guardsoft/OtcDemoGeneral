package com.otc.model.request.retrieve;

public class Merchant{
	private String merchantId;

	public void setMerchantId(String merchantId){
		this.merchantId = merchantId;
	}

	public String getMerchantId(){
		return merchantId;
	}

	@Override
 	public String toString(){
		return 
			"Merchant{" + 
			"merchantId = '" + merchantId + '\'' + 
			"}";
		}
}
