package org.video.common.utils;

import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

/**
 * @author gutongxue
 * @date 2019/11/13 14:18
 **/

/**
 * Mapper是Mybatis提供的通用mapper类，提供大量sql语句实现
 * @param <T>
 */
public interface MyMapper<T> extends Mapper<T>, MySqlMapper<T> {
    //TODO
    //FIXME 特别注意，该接口不能被扫描到，否则会出错
}
