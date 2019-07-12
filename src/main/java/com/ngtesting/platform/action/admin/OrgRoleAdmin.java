package com.ngtesting.platform.action.admin;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ngtesting.platform.action.BaseAction;
import com.ngtesting.platform.config.Constant;
import com.ngtesting.platform.model.*;
import com.ngtesting.platform.service.intf.OrgRoleGroupRelationService;
import com.ngtesting.platform.service.intf.OrgRolePrivilegeRelationService;
import com.ngtesting.platform.service.intf.OrgRoleService;
import com.ngtesting.platform.service.intf.OrgRoleUserRelationService;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping(Constant.API_PATH_ADMIN + "org_role/")
public class OrgRoleAdmin extends BaseAction {
	@Autowired
    OrgRoleService orgRoleService;
	@Autowired
    OrgRolePrivilegeRelationService orgRolePrivilegeRelationService;
    @Autowired
    OrgRoleUserRelationService orgRoleUserRelationService;
	@Autowired
	OrgRoleGroupRelationService orgRoleGroupRelationService;

	@RequestMapping(value = "list", method = RequestMethod.POST)

	public Map<String, Object> list(HttpServletRequest request, @RequestBody JSONObject json) {
		Map<String, Object> ret = new HashMap<String, Object>();
		TstUser user = (TstUser) SecurityUtils.getSubject().getPrincipal();
		Integer orgId = user.getDefaultOrgId();

		String keywords = json.getString("keywords");
		Boolean disabled = json.getBoolean("disabled");

		List<TstOrgRole> ls = orgRoleService.list(orgId, keywords, disabled); // 总是取当前用户的org，不需要再鉴权

        ret.put("data", ls);
		ret.put("code", Constant.RespCode.SUCCESS.getCode());
		return ret;
	}

	@RequestMapping(value = "get", method = RequestMethod.POST)

	public Map<String, Object> get(HttpServletRequest request, @RequestBody JSONObject json) {
		Map<String, Object> ret = new HashMap<String, Object>();

		TstUser user = (TstUser) SecurityUtils.getSubject().getPrincipal();
		Integer orgId = user.getDefaultOrgId();
		Integer orgRoleId = json.getInteger("id");

		TstOrgRole po;
		if (orgRoleId == null) {
			po = new TstOrgRole();
			po.setOrgId(orgId);
		} else {
			po = orgRoleService.get(orgRoleId, orgId);
		}
        if (po == null) { // 当对象不是默认org的，此处为空
            return authorFail();
        }

		List<TstOrgRolePrivilegeRelation> privileges =
                orgRolePrivilegeRelationService.listRelationsByOrgRole(orgId, orgRoleId);
        List<TstOrgRoleUserRelation> users =
                orgRoleUserRelationService.listRelationsByOrgRole(orgId, orgRoleId);
		List<TstOrgRoleGroupRelation> groups =
				orgRoleGroupRelationService.listRelationsByOrgRole(orgId, orgRoleId);

		if (orgRoleId == null) {
			ret.put("orgRole", new TstOrgRole());
		} else {
            ret.put("orgRole", po);
        }

        ret.put("privileges", privileges);
        ret.put("users", users);
		ret.put("groups", groups);
        ret.put("code", Constant.RespCode.SUCCESS.getCode());
        return ret;
	}

	@RequestMapping(value = "save", method = RequestMethod.POST)

	public Map<String, Object> save(HttpServletRequest request, @RequestBody JSONObject json) {
		Map<String, Object> ret = new HashMap<String, Object>();

		TstUser user = (TstUser) SecurityUtils.getSubject().getPrincipal();
		Integer orgId = user.getDefaultOrgId();

		TstOrgRole orgRoleVo = JSON.parseObject(JSON.toJSONString(json.get("orgRole")), TstOrgRole.class);
		TstOrgRole po = orgRoleService.save(orgRoleVo, orgId);
		if (po == null) { // 当对象不是默认org的，update的结果会返回空
			return authorFail();
		}

		List<TstOrgRolePrivilegeRelation> privileges = (List<TstOrgRolePrivilegeRelation>) json.get("privileges");
		orgRolePrivilegeRelationService.saveRelationsForRole(orgId, po.getId(), privileges);

        List<TstOrgRoleUserRelation> users = (List<TstOrgRoleUserRelation>) json.get("users");
        orgRoleUserRelationService.saveRelationsForRole(orgId, po.getId(), users);

		List<TstOrgRoleGroupRelation> groups = (List<TstOrgRoleGroupRelation>) json.get("groups");
		orgRoleGroupRelationService.saveRelationsForRole(orgId, po.getId(), groups);

		ret.put("code", Constant.RespCode.SUCCESS.getCode());
		return ret;
	}

	@RequestMapping(value = "delete", method = RequestMethod.POST)

	public Map<String, Object> delete(HttpServletRequest request, @RequestBody JSONObject json) {
		Map<String, Object> ret = new HashMap<String, Object>();
        TstUser user = (TstUser) SecurityUtils.getSubject().getPrincipal();
        Integer orgId = user.getDefaultOrgId();

		Boolean result = orgRoleService.delete(json.getInteger("id"), orgId);
        if (!result) { // 当对象不是默认org的，结果会返回false
            return authorFail();
        }

		ret.put("code", Constant.RespCode.SUCCESS.getCode());
		return ret;
	}

}
