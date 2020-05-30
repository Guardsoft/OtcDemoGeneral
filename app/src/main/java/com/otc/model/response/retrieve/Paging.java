package com.otc.model.response.retrieve;

public class Paging{
	private int pageNumber;
	private int totalPages;
	private int pageSize;

	public void setPageNumber(int pageNumber){
		this.pageNumber = pageNumber;
	}

	public int getPageNumber(){
		return pageNumber;
	}

	public void setTotalPages(int totalPages){
		this.totalPages = totalPages;
	}

	public int getTotalPages(){
		return totalPages;
	}

	public void setPageSize(int pageSize){
		this.pageSize = pageSize;
	}

	public int getPageSize(){
		return pageSize;
	}

	@Override
 	public String toString(){
		return 
			"Paging{" + 
			"pageNumber = '" + pageNumber + '\'' + 
			",totalPages = '" + totalPages + '\'' + 
			",pageSize = '" + pageSize + '\'' + 
			"}";
		}
}
