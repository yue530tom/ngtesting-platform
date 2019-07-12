package com.ngtesting.platform.action.admin;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.ngtesting.platform.action.BaseAction;
import com.ngtesting.platform.config.Constant;
import com.ngtesting.platform.model.TstOrgGroupUserRelation;
import com.ngtesting.platform.model.TstUser;
import com.ngtesting.platform.service.intf.OrgGroupUserRelationService;
import com.ngtesting.platform.service.intf.PushSettingsService;
import com.ngtesting.platform.service.intf.UserService;
import org.apache.shiro.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = Constant.API_PATH_ADMIN + "user")
public class UserAdmin extends BaseAction {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private UserService userService;

    @Autowired
    OrgGroupUserRelationService orgGroupUserRelationService;

    @Autowired
    PushSettingsService pushSettingsService;

    @PostMapping(value = "list")
    public Map<String, Object> list(HttpServletRequest request, @RequestBody JSONObject json) {
        Map<String, Object> ret = new HashMap<String, Object>();

        TstUser user = (TstUser) SecurityUtils.getSubject().getPrincipal();
        Integer orgId = user.getDefaultOrgId();

        String keywords = json.getString("keywords");
        Boolean disabled = json.getBoolean("disabled");
        Integer pageNum = json.getInteger("page");
        Integer pageSize = json.getInteger("pageSize");

        Page page = PageHelper.startPage(pageNum, pageSize);
        List<TstUser> users = userService.list(orgId, keywords, disabled, pageNum, pageSize); // 总是取当前用户的org，不需要再鉴权

        ret.put("total", page.getTotal());
        ret.put("data", users);
        ret.put("code", Constant.RespCode.SUCCESS.getCode());
        return ret;
    }

    @RequestMapping(value = "get", method = RequestMethod.POST)
    public Map<String, Object> get(HttpServletRequest request, @RequestBody JSONObject json) {
        Map<String, Object> ret = new HashMap<String, Object>();

        TstUser user = (TstUser) SecurityUtils.getSubject().getPrincipal();
        Integer orgId = user.getDefaultOrgId();
        Integer userId = json.getInteger("id");

        List<TstOrgGroupUserRelation> relations = orgGroupUserRelationService.listRelationsByUser(orgId, userId);

        if (userId == null) { // 新增用户
            ret.put("user", new TstUser());
            ret.put("relations", relations);
            ret.put("code", Constant.RespCode.SUCCESS.getCode());
            return ret;
        }

        TstUser po = userService.get(userId);

        ret.put("user", po);
        ret.put("relations", relations);
        ret.put("code", Constant.RespCode.SUCCESS.getCode());
        return ret;
    }

    @RequestMapping(value = "invite")
    public Map<String, Object> invite(HttpServletRequest request, @RequestBody JSONObject json) {
        Map<String, Object> ret = new HashMap<String, Object>();

        TstUser user = (TstUser) SecurityUtils.getSubject().getPrincipal();

        TstUser vo = JSON.parseObject(JSON.toJSONString(json.get("user")), TstUser.class);
        List<TstOrgGroupUserRelation> relations = (List<TstOrgGroupUserRelation>) json.get("relations");
        TstUser po = userService.invite(user, vo, relations);

        if (po == null) {
            ret.put("code", Constant.RespCode.BIZ_FAIL.getCode());
            ret.put("msg", "邮箱已加入当前组织");
            return ret;
        }

        ret.put("code", Constant.RespCode.SUCCESS.getCode());
        return ret;
    }

    // 管理员修改用户信息
    @PostMapping(value = "update")
    public Map<String, Object> update(HttpServletRequest request, @RequestBody JSONObject json) {
        Map<String, Object> ret = new HashMap<String, Object>();

        TstUser user = (TstUser) SecurityUtils.getSubject().getPrincipal();
        Integer orgId = user.getDefaultOrgId();

        TstUser vo = JSON.parseObject(JSON.toJSONString(json.get("user")), TstUser.class);

        TstUser existUser = userService.getByEmail(vo.getEmail());
        if (existUser != null && existUser.getId() != vo.getId()) {
            ret.put("code", Constant.RespCode.BIZ_FAIL.getCode());
            ret.put("msg", "邮箱已被占用");
            return ret;
        }

        TstUser po = userService.update(vo);

        List<TstOrgGroupUserRelation> relations = (List<TstOrgGroupUserRelation>) json.get("relations");
        orgGroupUserRelationService.saveRelationsForUser(orgId, vo.getId(), relations);
        ret.put("code", Constant.RespCode.SUCCESS.getCode());

//        pushSettingsService.pushUserSettings(po);
        return ret;
    }

    @PostMapping(value = "removeFromOrg")
    public Map<String, Object> removeFromOrg(HttpServletRequest request, @RequestBody JSONObject json) {
        Map<String, Object> ret = new HashMap<String, Object>();
        TstUser user = (TstUser) SecurityUtils.getSubject().getPrincipal();
        Integer orgId = user.getDefaultOrgId();

        Integer userId = json.getInteger("userId");

        Boolean result = userService.removeFromOrg(userId, orgId);

        ret.put("code", Constant.RespCode.SUCCESS.getCode());
        return ret;
    }

}
