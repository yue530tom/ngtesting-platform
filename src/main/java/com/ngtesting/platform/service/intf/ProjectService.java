package com.ngtesting.platform.service.intf;

import com.ngtesting.platform.model.TstProject;
import com.ngtesting.platform.model.TstProjectAccessHistory;
import com.ngtesting.platform.model.TstUser;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

public interface ProjectService extends BaseService {

	List<TstProject> list(Integer orgId, Integer userId, String keywords, Boolean disabled);
	List<TstProject> listProjectGroups(Integer orgId, Integer groupId);

	List<TstProjectAccessHistory> listRecentProject(Integer orgId, Integer userId);

	TstProject get(Integer id);

	TstProject getWithPrivs(Integer id, Integer userId);

	TstProject save(TstProject vo, Integer orgId, TstUser userVo);
	Boolean delete(Integer id, TstUser user);

    @Transactional
    TstProject changeDefaultPrj(TstUser user, Integer projectId, Boolean pushMsg);

    void updateNameInHisoty(Integer projectId, Integer userId);

    void setUserDefaultPrjToNullForDelete(Integer id);

    @Transactional
    void changeToAnotherPrj(TstUser user);

    TstProject changeDefaultPrj(TstUser user, Integer prjId);

	boolean isLastestProjectGroup(Integer orgId, Integer projectGroupId);

	TstProject genVo(TstProject po, Map<String, Map<String, Boolean>> privs);
	List<TstProject> genVos(List<TstProject> pos, Map<String, Map<String, Boolean>> privs);
	List<TstProject> genGroupVos(List<TstProject> pos);
}
