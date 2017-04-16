package com.suucee.www.util;

import com.suucee.www.entity.Columns;


public class HelperColumn {
	public static String getTemplate(Columns column, int pageNo, int contentId) {
		if (contentId > 0) {
			return column.getContentTemplate();
		} else if (pageNo > 0) {
			return column.getListTemplate();
		} else {
			return column.getChannelTemplate();
		}
	}
}
