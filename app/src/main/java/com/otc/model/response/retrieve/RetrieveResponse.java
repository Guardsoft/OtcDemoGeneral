package com.otc.model.response.retrieve;

import java.util.List;

public class RetrieveResponse{
	private Header header;
	private Paging paging;
	private List<TransactionsItem> transactions;

	public void setHeader(Header header){
		this.header = header;
	}

	public Header getHeader(){
		return header;
	}

	public void setPaging(Paging paging){
		this.paging = paging;
	}

	public Paging getPaging(){
		return paging;
	}

	public void setTransactions(List<TransactionsItem> transactions){
		this.transactions = transactions;
	}

	public List<TransactionsItem> getTransactions(){
		return transactions;
	}

	@Override
 	public String toString(){
		return 
			"RetrieveRequest{" +
			"header = '" + header + '\'' + 
			",paging = '" + paging + '\'' + 
			",transactions = '" + transactions + '\'' + 
			"}";
		}
}