package com.maxivetech.backoffice.util;

import java.util.List;

public class Page<O> {
	private int totalRows; // 记录总数
	private int totalPages; // 总页数
	private int pageSize; // 每页显示数据条数，默认为10条记录
	private int currentPage; // 当前正在显示的页面
	private String buttons;
	private List<O> list = null;// 每次请求后从数据库取得的将要显示数据

	public Page(int totalRows, int pageSize, int currentPage, List<O> list) {
		super();
		this.totalRows = totalRows;
		this.pageSize = pageSize;
		this.currentPage = currentPage;
		this.list = list;
		
		this._countTotalPages();
	}

	private void _countTotalPages() {
		if (totalRows > pageSize) {
			if (totalRows % pageSize == 0) {
				this.totalPages = (totalRows / pageSize);// 行记录数刚好被页大小整除
			} else {
				this.totalPages = (1 + (totalRows / pageSize));// 行记录数刚好不被页大小整除
			}
		} else {
			this.totalPages = 1;// 行记录数小于页大小
		}
		if(currentPage>totalPages){
			currentPage=totalPages;
		}
        if(currentPage<1){
        	currentPage=1;
		}
	}
	
	private String _pageUrl(String format, String value) {
		return format.replace("??", value);
	}
	
	private String _pageUrl(String format, int value) {
		return this._pageUrl(format, String.valueOf(value));
	}
	
	public void generateButtons(String urlFormat) {
		String ret = "";
		
		if (this.totalPages > 1) {
			int pageNext = currentPage < totalPages ? currentPage + 1 : 1;
			int pagePrev = currentPage > 1 ? currentPage - 1 : totalPages;
			int pageBegin = currentPage > 3 ? currentPage - 3 : 1;
			int pageEnd = pageBegin + 6 < totalPages ? pageBegin + 6 : totalPages;

			
			ret += "<a href=\""+_pageUrl(urlFormat, pagePrev)+"\">&#171; 上一页</a>";
			if (pageBegin >= 2) {
				ret += "<a href=\""+_pageUrl(urlFormat, 1)+"\">1</a>"; 
			}
			if (pageBegin > 2) {
				ret += "<span>...</span>"; 
			}
			
			for (int i=pageBegin;i<=pageEnd;i++) {
				if (currentPage == i) {
					ret += "<span class=\"disabled\">"+i+"</span>";
				} else {
					ret += "<a href=\""+_pageUrl(urlFormat, i)+"\">"+i+"</a>";
				}
			}
			
			ret += "<a href=\""+_pageUrl(urlFormat, pageNext)+"\">下一页 &#187;</a>";
			
			//跳转器
			String url = _pageUrl(urlFormat, "'+parseInt(document.getElementById('page_to').value)+'");
			int size = String.valueOf(totalPages).length();
			int width = size * 20;
			ret += "<span><input onchange='javascript:if(this.value>"+totalPages+"){this.value="+totalPages+";}' type=\"text\" id=\"page_to\" style=\"text-align:center;width:"+width+"px\" "
				+ "size=\""+size+"\" maxlength=\""+size+"\" value=\""+currentPage+"\" /><input type=\"button\" value=\"跳转\" "
				+ "onclick=\"javascript:window.open('"+url+"', '_self');\" /></span>";
		}
		
		this.buttons = ret;
	}

	public int getCurrentPage() {
		return currentPage;
	}

	public int getPageSize() {
		return pageSize;
	}

	public int getTotalPages() {
		return totalPages;
	}

	public int getTotalRows() {
		return totalRows;
	}

	public List<O> getList() {
		return list;
	}

	public String getButtons() {
		return buttons;
	}

}
