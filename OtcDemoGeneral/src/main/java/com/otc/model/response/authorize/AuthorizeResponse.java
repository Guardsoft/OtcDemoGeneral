package com.otc.model.response.authorize;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class AuthorizeResponse implements Parcelable {

	@SerializedName("customFields")
	private CustomFields customFields;

	@SerializedName("header")
	private Header header;

	@SerializedName("merchant")
	private Merchant merchant;

	@SerializedName("device")
	private Device device;

	@SerializedName("order")
	private Order order;

	protected AuthorizeResponse(Parcel in) {
	}

	public static final Creator<AuthorizeResponse> CREATOR = new Creator<AuthorizeResponse>() {
		@Override
		public AuthorizeResponse createFromParcel(Parcel in) {
			return new AuthorizeResponse(in);
		}

		@Override
		public AuthorizeResponse[] newArray(int size) {
			return new AuthorizeResponse[size];
		}
	};

	public void setCustomFields(CustomFields customFields){
		this.customFields = customFields;
	}

	public CustomFields getCustomFields(){
		return customFields;
	}

	public void setHeader(Header header){
		this.header = header;
	}

	public Header getHeader(){
		return header;
	}

	public void setMerchant(Merchant merchant){
		this.merchant = merchant;
	}

	public Merchant getMerchant(){
		return merchant;
	}

	public void setDevice(Device device){
		this.device = device;
	}

	public Device getDevice(){
		return device;
	}

	public void setOrder(Order order){
		this.order = order;
	}

	public Order getOrder(){
		return order;
	}

	@Override
 	public String toString(){
		return 
			"AuthorizeResponse{" +
			"customFields = '" + customFields + '\'' + 
			",header = '" + header + '\'' + 
			",merchant = '" + merchant + '\'' + 
			",device = '" + device + '\'' + 
			",order = '" + order + '\'' + 
			"}";
		}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel parcel, int i) {
	}
}