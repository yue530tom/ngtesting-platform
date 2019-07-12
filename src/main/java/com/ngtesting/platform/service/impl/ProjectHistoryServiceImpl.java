package com.ngtesting.platform.service.impl;

import com.github.pagehelper.PageHelper;
import com.ngtesting.platform.dao.HistoryDao;
import com.ngtesting.platform.model.TstHistory;
import com.ngtesting.platform.model.TstProject;
import com.ngtesting.platform.model.TstUser;
import com.ngtesting.platform.service.intf.ProjectHistoryService;
import com.ngtesting.platform.utils.DateUtil;
import com.ngtesting.platform.utils.MsgUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.*;

@Service
public class ProjectHistoryServiceImpl extends BaseServiceImpl implements ProjectHistoryService {
    @Autowired
	HistoryDao historyDao;

	@Override
	public List<TstHistory> listByOrg(Integer orgId) {
        PageHelper.startPage(0, 30);

        List<TstHistory> ls = historyDao.listByOrg(orgId);

		return ls;
	}

	@Override
	public List<TstHistory> listByProject(Integer projectId, TstProject.ProjectType projectType) {
        PageHelper.startPage(0, 30);

        List<TstHistory> ls;
        if (projectType.equals(TstProject.ProjectType.project)) {
            ls = historyDao.listByProject(projectId);
		} else {
            ls = historyDao.listByProjectGroup(projectId);
		}

		return ls;
	}

    @Override
    public TstHistory getById(Integer id) {
		TstHistory po = historyDao.get(id);

		return po;
    }
    @Override
    public TstHistory create(Integer projectId, TstUser optUser, String action,
							 TstHistory.TargetType entityType, Integer entityId, String entityName) {
		String title = MessageFormat.format(MsgUtil.HistoryMsgTemplate.opt_project.msg,
				optUser.getNickname(), action, entityType.name, entityName);

        TstHistory history = new TstHistory();
		history.setTitle(title);
        history.setProjectId(projectId);
        history.setEntityId(entityId);
        history.setEntityType(entityType);
        history.setUserId(optUser.getId());

		historyDao.create(history);

        return history;
    }

	@Override
	public Map<String, List<TstHistory>> genVosByDate(List<TstHistory> historyPos) {
		Map<String, List<TstHistory>> map = new LinkedHashMap();
		for(TstHistory his: historyPos) {

		Date createDate = his.getCreateTime();
		String date = DateUtil.FormatDate(createDate, "yyyy-MM-dd");
		if (!map.containsKey(date)) {
			map.put(date, new LinkedList());
		}
		map.get(date).add(his);

		}
		return map;
	}

}

