package com.ngtesting.platform.dao;

import com.ngtesting.platform.model.TstCaseComments;
import org.apache.ibatis.annotations.Param;

public interface CaseCommentsDao {
    TstCaseComments get(@Param("id") Integer id);

    void save(TstCaseComments vo);

    void update(@Param("vo") TstCaseComments vo,
                @Param("userId") Integer userId);

    boolean delete(@Param("id") Integer id,
                   @Param("userId") Integer userId);
}
