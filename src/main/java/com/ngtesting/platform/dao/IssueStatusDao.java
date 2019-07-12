package com.ngtesting.platform.dao;

import com.ngtesting.platform.model.IsuStatus;
import com.ngtesting.platform.model.IsuStatusCategoryDefine;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface IssueStatusDao {
    List<IsuStatus> list(@Param("orgId") Integer orgId);

    List<IsuStatusCategoryDefine> listCategory();

    IsuStatus get(@Param("id") Integer id,
                @Param("orgId") Integer orgId);

    IsuStatusCategoryDefine getCategoryById(@Param("id") Integer id);


    Integer save(IsuStatus vo);
    Integer update(IsuStatus vo);
    Integer delete(@Param("id") Integer id,
                   @Param("orgId") Integer orgId);

    Integer removeDefault(@Param("orgId") Integer orgId);
    Integer setDefault(@Param("id") Integer id,
                       @Param("orgId") Integer orgId);
    Integer setOrder(@Param("id") Integer id,
                     @Param("ordr") Integer ordr,
                     @Param("orgId") Integer orgId);

    IsuStatus getPrev(@Param("ordr") Integer ordr, @Param("orgId") Integer orgId);

    IsuStatus getNext(@Param("ordr") Integer ordr, @Param("orgId") Integer orgId);

    Integer getMaxOrdrNumb(@Param("orgId") Integer orgId);
}
