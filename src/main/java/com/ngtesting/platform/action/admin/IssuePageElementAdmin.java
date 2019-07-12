package com.ngtesting.platform.action.admin;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ngtesting.platform.action.BaseAction;
import com.ngtesting.platform.config.Constant;
import com.ngtesting.platform.model.IsuField;
import com.ngtesting.platform.model.IsuPage;
import com.ngtesting.platform.model.TstUser;
import com.ngtesting.platform.service.intf.IssueDynamicFormService;
import com.ngtesting.platform.service.intf.IssueFieldService;
import com.ngtesting.platform.service.intf.IssuePageElementService;
import com.ngtesting.platform.service.intf.IssuePageService;
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
@RequestMapping(Constant.API_PATH_ADMIN + "issue_page_elem/")
public class IssuePageElementAdmin extends BaseAction {

	@Autowired
    IssuePageService pageService;
	@Autowired
	IssuePageElementService elementService;

    @Autowired
	IssueFieldService fieldService;
    @Autowired
    IssueDynamicFormService dynamicFormService;

	@RequestMapping(value = "saveAll", method = RequestMethod.POST)

	public Map<String, Object> saveAll(HttpServletRequest request, @RequestBody JSONObject json) {
		Map<String, Object> ret = new HashMap<String, Object>();

		TstUser user = (TstUser) SecurityUtils.getSubject().getPrincipal();
		Integer orgId = user.getDefaultOrgId();
        Integer projectId = user.getDefaultPrjId();

        Integer pageId = json.getInteger("pageId");
        List<Map> maps = JSON.parseArray(json.getJSONArray("elems").toJSONString(), Map.class) ;

        elementService.saveAll(orgId, pageId, maps);

		IsuPage page = pageService.get(pageId, orgId);
		List<IsuField> fields = dynamicFormService.listNotUsedField(orgId, projectId, pageId);

		ret.put("page", page);
		ret.put("fields", fields);

		ret.put("code", Constant.RespCode.SUCCESS.getCode());
		return ret;
	}

    @RequestMapping(value = "updateProp", method = RequestMethod.POST)
    public Map<String, Object> updateProp(HttpServletRequest request, @RequestBody JSONObject json) {
        Map<String, Object> ret = new HashMap<String, Object>();

        TstUser user = (TstUser) SecurityUtils.getSubject().getPrincipal();
        Integer orgId = user.getDefaultOrgId();

        Integer id = json.getInteger("id");
        String prop = json.getString("prop");
        String val = json.getString("val");

        elementService.updateProp(id, prop, val, orgId);

        ret.put("code", Constant.RespCode.SUCCESS.getCode());
        return ret;
    }

}
