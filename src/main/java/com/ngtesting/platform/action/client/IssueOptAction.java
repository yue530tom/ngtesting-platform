package com.ngtesting.platform.action.client;

import com.alibaba.fastjson.JSONObject;
import com.ngtesting.platform.action.BaseAction;
import com.ngtesting.platform.config.Constant;
import com.ngtesting.platform.model.TstUser;
import com.ngtesting.platform.service.intf.*;
import com.ngtesting.platform.servlet.PrivPrj;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping(Constant.API_PATH_CLIENT + "issue_opt/")
public class IssueOptAction extends BaseAction {
    @Autowired
    IssueOptService issueOptService;
    @Autowired
    IssueService issueService;

    @Autowired
    IssueFieldService fieldService;
	@Autowired
	IssueDynamicFormService dynamicFormService;
    @Autowired
    MsgService msgService;

    @PrivPrj(perms = {"issue:maintain"})
    @RequestMapping(value = "assign", method = RequestMethod.POST)
    public Map<String, Object> assign(HttpServletRequest request, @RequestBody JSONObject json) {
        Map<String, Object> ret = new HashMap<>();
        TstUser user = (TstUser) SecurityUtils.getSubject().getPrincipal();
        Integer prjId = user.getDefaultPrjId();

        Integer id = json.getInteger("id");
        Integer userId = json.getInteger("userId");
        String comments = json.getString("comments");

        issueOptService.assign(id, userId, comments, user);

        ret.put("code", Constant.RespCode.SUCCESS.getCode());
        return ret;
    }

    @RequestMapping(value = "statusTran", method = RequestMethod.POST)
    @PrivPrj(perms = {"issue:maintain"})
    public Map<String, Object> statusTran(HttpServletRequest request, @RequestBody JSONObject json) {
        Map<String, Object> ret = new HashMap<>();
        TstUser user = (TstUser) SecurityUtils.getSubject().getPrincipal();
        Integer prjId = user.getDefaultPrjId();

        Integer id = json.getInteger("id");
        Integer dictStatusId = json.getInteger("dictStatusId");
        String dictStatusName = json.getString("dictStatusName");

        issueOptService.statusTran(id, dictStatusId, dictStatusName, user);

        ret.put("code", Constant.RespCode.SUCCESS.getCode());
        return ret;
    }

    @RequestMapping(value = "updateThenStatusTran", method = RequestMethod.POST)
    @PrivPrj(perms = {"issue:maintain"})
    public Map<String, Object> updateThenStatusTran(HttpServletRequest request, @RequestBody JSONObject json) {
        Map<String, Object> ret = new HashMap<>();
        TstUser user = (TstUser) SecurityUtils.getSubject().getPrincipal();

        issueOptService.updateThenStatusTran(json, user);

        ret.put("code", Constant.RespCode.SUCCESS.getCode());
        return ret;
    }

}
