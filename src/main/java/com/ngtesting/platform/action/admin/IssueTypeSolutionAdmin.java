package com.ngtesting.platform.action.admin;

import com.alibaba.fastjson.JSONObject;
import com.ngtesting.platform.action.BaseAction;
import com.ngtesting.platform.config.Constant;
import com.ngtesting.platform.model.IsuType;
import com.ngtesting.platform.model.IsuTypeSolution;
import com.ngtesting.platform.model.TstUser;
import com.ngtesting.platform.service.intf.IssueTypeService;
import com.ngtesting.platform.service.intf.IssueTypeSolutionService;
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
@RequestMapping(Constant.API_PATH_ADMIN + "issue_type_solution/")
public class IssueTypeSolutionAdmin extends BaseAction {
	private static final Log log = LogFactory.getLog(CaseTypeAdmin.class);

	@Autowired
    IssueTypeSolutionService solutionService;

	@Autowired
	IssueTypeService typeService;

	@RequestMapping(value = "list", method = RequestMethod.POST)
	public Map<String, Object> list(HttpServletRequest request, @RequestBody JSONObject json) {
		Map<String, Object> ret = new HashMap<String, Object>();

		TstUser user = (TstUser) SecurityUtils.getSubject().getPrincipal();
		Integer orgId = user.getDefaultOrgId();

		List<IsuTypeSolution> vos = solutionService.list(orgId);

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
		IsuTypeSolution po;
		if (id == null) {
			po = new IsuTypeSolution();
		} else {
			po = solutionService.getDetail(id, orgId);
		}

		if (po == null) { // 当对象不是默认org的，此处为空
			return authorFail();
		}

		List<IsuType> otherItems = typeService.listNotInSolution(id, orgId);

		ret.put("data", po);
		ret.put("otherItems", otherItems);
		ret.put("code", Constant.RespCode.SUCCESS.getCode());
		return ret;
	}

	@RequestMapping(value = "save", method = RequestMethod.POST)
	public Map<String, Object> save(HttpServletRequest request, @RequestBody JSONObject json) {
		Map<String, Object> ret = new HashMap<String, Object>();

		TstUser user = (TstUser) SecurityUtils.getSubject().getPrincipal();
		Integer orgId = user.getDefaultOrgId();

		IsuTypeSolution vo = json.getObject("model", IsuTypeSolution.class);

		IsuTypeSolution po = solutionService.save(vo, orgId);
		if (po == null) {	// 当对象不是默认org的，update的结果会返回空
			return authorFail();
		}

		ret.put("data", po);
		ret.put("code", Constant.RespCode.SUCCESS.getCode());
		return ret;
	}

	@RequestMapping(value = "addType", method = RequestMethod.POST)
    public Map<String, Object> addType(HttpServletRequest request, @RequestBody JSONObject json) {
        Map<String, Object> ret = new HashMap<String, Object>();

        TstUser user = (TstUser) SecurityUtils.getSubject().getPrincipal();
        Integer orgId = user.getDefaultOrgId();

        Integer typeId = json.getInteger("typeId");
        Integer solutionId = json.getInteger("solutionId");

        if (solutionId == null) {
            IsuTypeSolution vo = new IsuTypeSolution("新问题类型方案");
            IsuTypeSolution po = solutionService.save(vo, orgId);

            solutionId = po.getId();
        }

        solutionService.addType(typeId, solutionId, orgId);

        IsuTypeSolution po = solutionService.getDetail(solutionId, orgId);
        List<IsuType> otherItems = typeService.listNotInSolution(solutionId, orgId);

        ret.put("data", po);
        ret.put("otherItems", otherItems);
        ret.put("code", Constant.RespCode.SUCCESS.getCode());
        return ret;
    }

    @RequestMapping(value = "removeType", method = RequestMethod.POST)
    public Map<String, Object> removeType(HttpServletRequest request, @RequestBody JSONObject json) {
        Map<String, Object> ret = new HashMap<String, Object>();

        TstUser user = (TstUser) SecurityUtils.getSubject().getPrincipal();
        Integer orgId = user.getDefaultOrgId();

        Integer typeId = json.getInteger("typeId");
        Integer solutionId = json.getInteger("solutionId");

        solutionService.removeType(typeId, solutionId, orgId);

        IsuTypeSolution po = solutionService.getDetail(solutionId, orgId);
        List<IsuType> otherItems = typeService.listNotInSolution(solutionId, orgId);

        ret.put("data", po);
        ret.put("otherItems", otherItems);
        ret.put("code", Constant.RespCode.SUCCESS.getCode());
        return ret;
    }

    @RequestMapping(value = "addAll", method = RequestMethod.POST)
    public Map<String, Object> addAll(HttpServletRequest request, @RequestBody JSONObject json) {
        Map<String, Object> ret = new HashMap<String, Object>();

        TstUser user = (TstUser) SecurityUtils.getSubject().getPrincipal();
        Integer orgId = user.getDefaultOrgId();

        Integer solutionId = json.getInteger("solutionId");

        if (solutionId == null) {
            IsuTypeSolution vo = new IsuTypeSolution("新问题类型方案");
            IsuTypeSolution po = solutionService.save(vo, orgId);

            solutionId = po.getId();
        }

        solutionService.addAll(solutionId, orgId);

        IsuTypeSolution po = solutionService.getDetail(solutionId, orgId);
        List<IsuType> otherItems = typeService.listNotInSolution(solutionId, orgId);

        ret.put("data", po);
        ret.put("otherItems", otherItems);
        ret.put("code", Constant.RespCode.SUCCESS.getCode());
        return ret;
    }

    @RequestMapping(value = "removeAll", method = RequestMethod.POST)
    public Map<String, Object> removeAll(HttpServletRequest request, @RequestBody JSONObject json) {
        Map<String, Object> ret = new HashMap<String, Object>();

        TstUser user = (TstUser) SecurityUtils.getSubject().getPrincipal();
        Integer orgId = user.getDefaultOrgId();

        Integer solutionId = json.getInteger("solutionId");

        solutionService.removeAll(solutionId, orgId);

        IsuTypeSolution po = solutionService.getDetail(solutionId, orgId);
        List<IsuType> otherItems = typeService.listNotInSolution(solutionId, orgId);

        ret.put("data", po);
        ret.put("otherItems", otherItems);
        ret.put("code", Constant.RespCode.SUCCESS.getCode());
        return ret;
    }

    @RequestMapping(value = "delete", method = RequestMethod.POST)
    public Map<String, Object> delete(HttpServletRequest request, @RequestBody JSONObject json) {
        Map<String, Object> ret = new HashMap<String, Object>();

        TstUser user = (TstUser) SecurityUtils.getSubject().getPrincipal();
        Integer orgId = user.getDefaultOrgId();

        Integer id = json.getInteger("id");

        solutionService.delete(id, orgId);

        ret.put("code", Constant.RespCode.SUCCESS.getCode());
        return ret;
    }

    @RequestMapping(value = "setDefault", method = RequestMethod.POST)
    public Map<String, Object> setDefault(HttpServletRequest request, @RequestBody JSONObject json) {
        Map<String, Object> ret = new HashMap<String, Object>();

        TstUser user = (TstUser) SecurityUtils.getSubject().getPrincipal();
        Integer orgId = user.getDefaultOrgId();

        Integer id = json.getInteger("id");

        Boolean result = solutionService.setDefault(id, orgId);
        if (!result) { // 当对象不是默认org的，结果会返回false
            return authorFail();
        }

        List<IsuTypeSolution> vos = solutionService.list(orgId);

        ret.put("data", vos);
        ret.put("code", Constant.RespCode.SUCCESS.getCode());

        return ret;
    }

}
