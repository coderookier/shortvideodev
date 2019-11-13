package org.video.mapper;

import org.video.common.utils.MyMapper;
import org.video.pojo.SearchRecords;

import java.util.List;



public interface SearchRecordsMapper extends MyMapper<SearchRecords> {
	
	public List<String> getHotwords();
}