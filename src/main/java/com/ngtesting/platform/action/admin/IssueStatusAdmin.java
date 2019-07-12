package com.ngtesting.platform.action.admin;

import com.alibaba.fastjson.JSONObject;
import com.ngtesting.platform.action.BaseAction;
import com.ngtesting.platform.config.Constant;
import com.ngtesting.platform.model.IsuStatus;
import com.ngtesting.platform.model.IsuStatusCategoryDefine;
import com.ngtesting.platform.model.TstUser;
import com.ngtesting.platform.service.intf.IssuePropertyService;
import com.ngtesting.platform.service.intf.IssueStatusService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping(Constant.API_PATH_ADMIN + "issue_status/")
public class IssueStatusAdmin extends BaseAction {
	private static final Log log = LogFactory.getLog(IssueStatusAdmin.class);

	@Autowired
	IssueStatusService issueStatusService;

	@Autowired
	IssuePropertyService issuePropertyService;

	@RequestMapping(value = "list", method = RequestMethod.POST)
	public Map<String, Object> list(HttpServletRequest request, @RequestBody JSONObject json) {
		Map<String, Object> ret = new HashMap<String, Object>();

		TstUser user = (TstUser) SecurityUtils.getSubject().getPrincipal();
		Integer orgId = user.getDefaultOrgId();

		List<IsuStatus> vos = issueStatusService.list(orgId);

		ret.put("data", vos);
		ret.put("code", Constant.RespCode.SUCCESS.getCode());

		return ret;
	}


	@RequestMapping(value = "get", method = RequestMethod.POST)
	public Map<String, Object> get(HttpServletRequest request, @RequestBody JSONObject json) {
		Map<String, Object> ret = new HashMap<String, Object>();

		TstUser user = (TstUser) SecurityUtils.getSubject().getPrincipal();
		Integer orgId = user.getDefaultOrgId();

		Integer id = json.getInteger("id");
		IsuStatus po;
		if (id == null) {
			po = new IsuStatus();
		} else {
			po = issueStatusService.get(id, orgId);
		}

		if (po == null) { // 当对象不是默认org的，此处为空
			return authorFail();
		}

		List<IsuStatusCategoryDefine> categories = issueStatusService.listCategory();

		ret.put("data", po);
		ret.put("categories", categories);
		ret.put("code", Constant.RespCode.SUCCESS.getCode());
		return ret;
	}

	@RequestMapping(value = "save", method = RequestMethod.POST)
	public Map<String, Object> save(HttpServletRequest request, @RequestBody JSONObject json) {
		Map<String, Object> ret = new HashMap<String, Object>();

		TstUser user = (TstUser) SecurityUtils.getSubject().getPrincipal();
		Integer orgId = user.getDefaultOrgId();

		IsuStatus vo = json.getObject("model", IsuStatus.class);

		IsuStatus po = issueStatusService.save(vo, orgId);
		if (po == null) {    // 当对象不是默认org的，update的结果会返回空
			return authorFail();
		}

		ret.put("data", po);
		ret.put("code", Constant.RespCode.SUCCESS.getCode());
		return ret;
	}

	@RequestMapping(value = "delete", method = RequestMethod.POST)
	public Map<String, Object> delete(HttpServletRequest request, @RequestBody JSONObject json) {
		Map<String, Object> ret = new HashMap<String, Object>();

		TstUser user = (TstUser) SecurityUtils.getSubject().getPrincipal();
		Integer orgId = user.getDefaultOrgId();

		Integer id = json.getInteger("id");

		Boolean result = issueStatusService.delete(id, orgId);
		if (!result) { // 当对象不是默认org的，结果会返回false
			return authorFail();
		}

		ret.put("code", Constant.RespCode.SUCCESS.getCode());
		return ret;
	}


	@RequestMapping(value = "setDefault", method = RequestMethod.POST)
	public Map<String, Object> setDefault(HttpServletRequest request, @RequestBody JSONObject json) {
		Map<String, Object> ret = new HashMap<String, Object>();

		TstUser user = (TstUser) SecurityUtils.getSubject().getPrincipal();
		Integer orgId = user.getDefaultOrgId();

		Integer id = json.getInteger("id");

		Boolean result = issueStatusService.setDefault(id, orgId);
		if (!result) { // 当对象不是默认org的，结果会返回false
			return authorFail();
		}

		List<IsuStatus> vos = issueStatusService.list(orgId);

		ret.put("data", vos);
		ret.put("code", Constant.RespCode.SUCCESS.getCode());

		return ret;
	}

	@RequestMapping(value = "changeOrder", method = RequestMethod.POST)
	public Map<String, Object> changeOrder(HttpServletRequest request, @RequestBody JSONObject json) {
		Map<String, Object> ret = new HashMap<String, Object>();

		TstUser user = (TstUser) SecurityUtils.getSubject().getPrincipal();
		Integer orgId = user.getDefaultOrgId();

		Integer id = json.getInteger("id");
		String act = json.getString("act");

		Boolean result = issueStatusService.changeOrder(id, act, orgId);
		if (!result) { // 当对象不是默认org的，结果会返回false
			return authorFail();
		}

		List<IsuStatus> vos = issueStatusService.list(orgId);

		ret.put("data", vos);
		ret.put("code", Constant.RespCode.SUCCESS.getCode());

		return ret;
	}
}
