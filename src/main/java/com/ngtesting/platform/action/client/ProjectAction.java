package com.ngtesting.platform.action.client;

import com.alibaba.fastjson.JSONObject;
import com.ngtesting.platform.action.BaseAction;
import com.ngtesting.platform.config.Constant;
import com.ngtesting.platform.model.TstHistory;
import com.ngtesting.platform.model.TstPlan;
import com.ngtesting.platform.model.TstProject;
import com.ngtesting.platform.model.TstUser;
import com.ngtesting.platform.service.intf.*;
import com.ngtesting.platform.servlet.PrivOrg;
import com.ngtesting.platform.servlet.PrivPrj;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = Constant.API_PATH_CLIENT + "/project")
public class ProjectAction extends BaseAction {
  @Autowired
  private ProjectService projectService;

  @Autowired
  private TestPlanService planService;
  @Autowired
  private ProjectHistoryService historyService;
  @Autowired
  AuthService authService;

  @Autowired
  CustomFieldService customFieldService;
  @Autowired
  CasePropertyService casePropertyService;
  @Autowired
  ProjectPrivilegeService projectPrivilegeService;

  @Autowired
  IssueDynamicFormService dynamicFormService;
  @Autowired
  IssueWorkflowTransitionService issueWorkflowTransitionService;

  @PostMapping("/list")
  @PrivOrg(perms = {"org_project:*"})
  public Object list(HttpServletRequest request, @RequestBody JSONObject json) {
    Map<String, Object> ret = new HashMap<String, Object>();
    TstUser user = (TstUser) SecurityUtils.getSubject().getPrincipal();
    Integer orgId = user.getDefaultOrgId();

    String keywords = json.getString("keywords");
    Boolean disabled = json.getBoolean("disabled");

    List<TstProject> vos = projectService.list(orgId, user.getId(), keywords, disabled);

    ret.put("data", vos);
    ret.put("code", Constant.RespCode.SUCCESS.getCode());

    return ret;
  }

  @PostMapping("/get")
  @PrivPrj(perms = {"belongs_to:project"})
  public Map<String, Object> get(HttpServletRequest request, @RequestBody JSONObject json) {
    Map<String, Object> ret = new HashMap<String, Object>();
    TstUser user = (TstUser) SecurityUtils.getSubject().getPrincipal();

    Integer projectId = json.getInteger("projectId");

    if (projectId != null) {
      TstProject project = projectService.get(projectId);

      ret.put("data", project);
    }

    ret.put("code", Constant.RespCode.SUCCESS.getCode());
    return ret;
  }

  @PostMapping("/getInfo")
  @PrivPrj(perms = {"belongs_to:project"})
  public Map<String, Object> getInfo(HttpServletRequest request, @RequestBody JSONObject json) {
    Map<String, Object> ret = new HashMap<String, Object>();
    TstUser user = (TstUser) SecurityUtils.getSubject().getPrincipal();
    Integer orgId = user.getDefaultOrgId();

    Integer projectId = json.getInteger("projectId");
    Integer groupId = null;

    if (projectId != null) {
      TstProject project = projectService.get(projectId);

      TstProject vo = projectService.genVo(project, null);

      if (TstProject.ProjectType.group.equals(project.getType())) {
        vo.setLastestProjectGroup(projectService.isLastestProjectGroup(orgId, projectId));
      }
      groupId = vo.getParentId();
      ret.put("data", vo);
    }

    List<TstProject> groups = projectService.listProjectGroups(orgId, groupId);
    ret.put("groups", groups);

    ret.put("code", Constant.RespCode.SUCCESS.getCode());
    return ret;
  }

  @PostMapping("/view")
  @PrivPrj(perms = {"belongs_to:project"})
  public Map<String, Object> view(HttpServletRequest request, @RequestBody JSONObject json) {
    Map<String, Object> ret = new HashMap<String, Object>();
    TstUser user = (TstUser) SecurityUtils.getSubject().getPrincipal();
    Integer orgId = user.getDefaultOrgId();
    Integer projectId = json.getInteger("projectId");

    TstProject po = projectService.getWithPrivs(projectId, user.getId());

    List<TstPlan> planPos = planService.listByProject(projectId, po.getType());
    planService.genVos(planPos);

    List<TstHistory> historyPos = historyService.listByProject(projectId, po.getType());
    Map<String, List<TstHistory>> historyVos = historyService.genVosByDate(historyPos);

    ret.put("code", Constant.RespCode.SUCCESS.getCode());
    ret.put("project", po);
    ret.put("plans", planPos);
    ret.put("histories", historyVos);

    return ret;
  }

  @PostMapping("/save")
  @PrivOrg(perms = {"org_project:*"})
  public Map<String, Object> save(HttpServletRequest request, @RequestBody JSONObject json) {
    Map<String, Object> ret = new HashMap<String, Object>();
    TstUser user = (TstUser) SecurityUtils.getSubject().getPrincipal();
    Integer orgId = user.getDefaultOrgId();

    TstProject vo = json.getObject("model", TstProject.class);

    TstProject po = projectService.save(vo, orgId, user);

    ret.put("data", po);
    ret.put("code", Constant.RespCode.SUCCESS.getCode());
    return ret;
  }

  @PostMapping(value = "delete")
  @PrivOrg(perms = {"org_project:*"})
  public Map<String, Object> delete(HttpServletRequest request, @RequestBody JSONObject json) {
    Map<String, Object> ret = new HashMap<String, Object>();
    TstUser user = (TstUser) SecurityUtils.getSubject().getPrincipal();

    Integer projectId = json.getInteger("projectId");
    TstProject project = projectService.get(projectId);

    projectService.delete(projectId, user);

    ret.put("code", Constant.RespCode.SUCCESS.getCode());
    return ret;
  }

  // 来源于前端上下文的变化
  @PostMapping("/initContext")
  @PrivPrj(perms = {"belongs_to:project"})
  public Map<String, Object> initContext(HttpServletRequest request, @RequestBody JSONObject json) {
    Map<String, Object> ret = new HashMap<String, Object>();

    TstUser user = (TstUser) SecurityUtils.getSubject().getPrincipal();
    Integer orgId = user.getDefaultOrgId();
    Integer projectId = json.getInteger("projectId");

    TstProject po = projectService.changeDefaultPrj(user, projectId, false);

    if (po != null && po.getType().equals(TstProject.ProjectType.project)) {
      prjConf(ret, orgId, projectId, user.getId());
    }

    ret.put("type", po.getType());
    ret.put("code", Constant.RespCode.SUCCESS.getCode());

    return ret;
  }

  // 来源于前端上下文的变化
  @PostMapping("/changeContext")
  @PrivPrj(perms = {"belongs_to:project"})
  public Map<String, Object> changeContext(HttpServletRequest request, @RequestBody JSONObject json) {
    Map<String, Object> ret = new HashMap<String, Object>();

    TstUser user = (TstUser) SecurityUtils.getSubject().getPrincipal();
    Integer orgId = user.getDefaultOrgId();
    Integer projectId = json.getInteger("projectId");

    TstProject po = projectService.changeDefaultPrj(user, projectId, true);

    ret.put("type", po.getType());
    ret.put("code", Constant.RespCode.SUCCESS.getCode());

    return ret;
  }

  private void prjConf(Map<String, Object> ret, Integer orgId, Integer projectId, Integer userId) {
    // 权限
    Map<String, Boolean> prjPrivileges = projectPrivilegeService.listByUser(userId, projectId, orgId);
    ret.put("prjPrivileges", prjPrivileges);

    // 用例
    Map<String, Object> map = customFieldService.fetchProjectFieldForCase(orgId, projectId);
    ret.put("caseCustomFields", map.get("fields"));
    ret.put("casePropMap", map.get("props"));
    Map<String, Map<String, String>> casePropValMap = casePropertyService.getMap(orgId);
    ret.put("casePropValMap", casePropValMap);

    // 缺陷
    Map issuePropMap = dynamicFormService.genIssuePropMap(orgId, projectId);
    ret.put("issuePropMap", issuePropMap);
    Map<String, Object> issuePropValMap = dynamicFormService.genIssueBuldInPropValMap(orgId, projectId);
    ret.put("issuePropValMap", issuePropValMap);

    Map issueTransMap = issueWorkflowTransitionService.getStatusTrainsMap(projectId, userId);
    ret.put("issueTransMap", issueTransMap);
  }

}
