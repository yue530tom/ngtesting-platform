package com.ngtesting.platform.service.impl;

import com.github.pagehelper.PageHelper;
import com.ngtesting.platform.dao.TestPlanDao;
import com.ngtesting.platform.model.*;
import com.ngtesting.platform.service.intf.ProjectHistoryService;
import com.ngtesting.platform.service.intf.TestPlanService;
import com.ngtesting.platform.service.intf.TestTaskService;
import com.ngtesting.platform.utils.MsgUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TestPlanServiceImpl extends BaseServiceImpl implements TestPlanService {
    @Autowired
    ProjectHistoryService historyService;

    @Autowired
    TestPlanDao testPlanDao;
    @Autowired
    TestTaskService taskService;

    @Override
    public List<TstPlan> listByPage(Integer projectId, String keywords, String status) {
        List<TstPlan> pos = testPlanDao.query(projectId, keywords, status);
        return pos;
    }

    @Override
    public TstPlan getById(Integer id, Integer projectId) {
        TstPlan po;
        if (id != null) {
            po = testPlanDao.get(id, projectId);
            genVo(po);
        } else {
            po = new TstPlan();
        }
        return po;
    }

    @Override
    public TstPlan save(TstPlan vo, TstUser user, Integer projectId) {
        vo.setUserId(user.getId());
        vo.setProjectId(projectId);

        MsgUtil.MsgAction action;
        if (vo.getId() == null) {
            action = MsgUtil.MsgAction.create;

            testPlanDao.save(vo);
        } else {
            action = MsgUtil.MsgAction.update;
            Integer count = testPlanDao.update(vo);
            if (count == 0) {
                return null;
            }
        }

        historyService.create(vo.getProjectId(), user, action.msg, TstHistory.TargetType.plan,
                vo.getId(), vo.getName());

        return vo;
    }

    @Override
    public Boolean delete(Integer id, Integer projectId) {
        Integer count = testPlanDao.delete(id, projectId);
        return count > 0;
    }

    @Override
    public List<TstPlan> listByOrg(Integer orgId) {
        PageHelper.startPage(0, 10);
        List<TstPlan> ls = testPlanDao.listByOrg(orgId);

        return ls;
    }

    @Override
    public List<TstPlan> listByProject(Integer projectId, TstProject.ProjectType projectType) {
        PageHelper.startPage(0, 10);

        List<TstPlan> pos;

        if (projectType.equals(TstProject.ProjectType.project)) {
            pos = testPlanDao.listByProject(projectId);
        } else {
            pos = testPlanDao.listByProjectGroup(projectId);
        }

        return pos;
    }

    @Override
    public void genVos(List<TstPlan> pos) {
        for (TstPlan po : pos) {
            genVo(po);
        }
    }

    @Override
    public void genVo(TstPlan po) {
        List<TstTask> tasks = taskService.listByPlan(po.getId());
        po.setTasks(tasks);
    }

}

