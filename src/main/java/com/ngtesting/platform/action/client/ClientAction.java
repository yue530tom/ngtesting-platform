package com.ngtesting.platform.action.client;

import com.alibaba.fastjson.JSONObject;
import com.ngtesting.platform.action.BaseAction;
import com.ngtesting.platform.config.Constant;
import com.ngtesting.platform.model.IsuQuery;
import com.ngtesting.platform.model.TstOrg;
import com.ngtesting.platform.model.TstProjectAccessHistory;
import com.ngtesting.platform.model.TstUser;
import com.ngtesting.platform.service.intf.*;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = Constant.API_PATH_CLIENT + "/client")
public class ClientAction extends BaseAction {
    @Autowired
    private UserService userService;

    @Autowired
    private OrgService orgService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private IssueQueryService issueQueryService;

    @Autowired
    SysPrivilegeService sysPrivilegeService;
    @Autowired
    OrgPrivilegeService orgPrivilegeService;

    @Autowired
    PushSettingsService pushSettingsService;

    @PostMapping(value = "getProfile")
    public Map<String, Object> getProfile(HttpServletRequest request, @RequestBody JSONObject json) {
        Map<String, Object> ret = new HashMap<String, Object>();

        TstUser user = (TstUser) SecurityUtils.getSubject().getPrincipal();

        Integer orgId = user.getDefaultOrgId();
        Integer prjId = user.getDefaultPrjId();
        Integer userId = user.getId();

        Integer orgIdNew = json.getInteger("orgId");
        Integer projectIdNew = json.getInteger("projectId");

        // 前端上下文变了
        if (orgIdNew != null && orgIdNew.longValue() != orgId.longValue()) { // org不能为空
            orgService.changeDefaultOrg(user, orgId);
        }
        if (projectIdNew != null && (prjId == null || projectIdNew.longValue() != prjId.longValue())) { // prj可能为空
            projectService.changeDefaultPrj(user, projectIdNew);
        }

        // 个人层面
        ret.put("profile", user);
        Map<String, Boolean> sysPrivileges = sysPrivilegeService.listByUser(userId);
        ret.put("sysPrivileges", sysPrivileges);
        List<TstOrg> orgs = orgService.listByUser(userId);

        ret.put("myOrgs", orgs);
        List<IsuQuery> recentQueries = issueQueryService.listRecentQuery(orgId, userId);
        ret.put("recentQueries", recentQueries);

        // 组织层面
        Map<String, Boolean> orgPrivileges = orgPrivilegeService.listByUser(user.getId(), orgId);
        ret.put("orgPrivileges", orgPrivileges);

        List<TstProjectAccessHistory> recentProjects = projectService.listRecentProject(orgId, userId);
        ret.put("recentProjects", recentProjects);

        ret.put("code", Constant.RespCode.SUCCESS.getCode());

        return ret;
    }

    // 用户修改某个字段
    @RequestMapping(value = "saveInfo", method = RequestMethod.POST)
    public Map<String, Object> saveInfo(HttpServletRequest request, @RequestBody JSONObject json) {
        Map<String, Object> ret = new HashMap<String, Object>();

        TstUser user = (TstUser) SecurityUtils.getSubject().getPrincipal();
        json.put("id", user.getId()); // 强制设置当前用户的属性

        TstUser po = userService.modifyProp(json);
        userService.updateUserInfoToPrincipal(po, user);

        pushSettingsService.pushUserSettings(po);

        ret.put("data", po);
        ret.put("code", Constant.RespCode.SUCCESS.getCode());
        return ret;
    }

    @PostMapping(value = "setLeftSize")
    public Map<String, Object> setLeftSize(HttpServletRequest request, @RequestBody JSONObject json) {
        Map<String, Object> ret = new HashMap<String, Object>();

        TstUser user = (TstUser) SecurityUtils.getSubject().getPrincipal();

        Integer left = json.getInteger("left");
        String prop = json.getString("prop");

        TstUser po = userService.setLeftSize(user, left, prop);
        userService.updateUserInfoToPrincipal(po, user);

        ret.put("data", user);
        ret.put("code", Constant.RespCode.SUCCESS.getCode());
        return ret;
    }

    @PostMapping(value = "setIssueView")
    public Map<String, Object> setIssueView(HttpServletRequest request, @RequestBody JSONObject json) {
        Map<String, Object> ret = new HashMap<String, Object>();

        TstUser user = (TstUser) SecurityUtils.getSubject().getPrincipal();

        String issueView = json.getString("issueView");

        TstUser po = userService.setIssueView(user, issueView);
        userService.updateUserInfoToPrincipal(po, user);

        ret.put("data", user);
        ret.put("code", Constant.RespCode.SUCCESS.getCode());
        return ret;
    }

}
