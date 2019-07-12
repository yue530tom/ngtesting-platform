package com.ngtesting.platform.action.client;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.ngtesting.platform.action.BaseAction;
import com.ngtesting.platform.bean.websocket.WsFacade;
import com.ngtesting.platform.config.Constant;
import com.ngtesting.platform.config.WsConstant;
import com.ngtesting.platform.model.TstMsg;
import com.ngtesting.platform.model.TstUser;
import com.ngtesting.platform.service.intf.MsgService;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping(Constant.API_PATH_CLIENT + "msg/")
public class MsgAction extends BaseAction {
    @Autowired
    private WsFacade optFacade;

	@Autowired
    MsgService msgService;

	@RequestMapping(value = "list", method = RequestMethod.POST)

	public Map<String, Object> list(HttpServletRequest request, @RequestBody JSONObject json) {
		Map<String, Object> ret = new HashMap<String, Object>();

		TstUser user = (TstUser) SecurityUtils.getSubject().getPrincipal();

        String keywords = json.getString("keywords");
        Boolean isRead = json.getBoolean("isRead");
        Integer pageNum = json.getInteger("page");
        Integer pageSize = json.getInteger("pageSize");

        com.github.pagehelper.Page page = PageHelper.startPage(pageNum, pageSize);
        List<TstMsg> ls = msgService.list(user.getId(), isRead, keywords);

        ret.put("total", page.getTotal());
        ret.put("data", ls);
		ret.put("code", Constant.RespCode.SUCCESS.getCode());
		return ret;
	}

	@RequestMapping(value = "delete", method = RequestMethod.POST)
	public Map<String, Object> delete(HttpServletRequest request, @RequestBody JSONObject json) {
		Map<String, Object> ret = new HashMap<String, Object>();

		Integer id = json.getInteger("id");

		TstUser user = (TstUser) SecurityUtils.getSubject().getPrincipal();

		Boolean result = msgService.delete(id, user.getId());
		if (!result) {
            return authorFail();
        }

		ret.put("code", Constant.RespCode.SUCCESS.getCode());
		return ret;
	}

    @RequestMapping(value = "markRead", method = RequestMethod.POST)
    public Map<String, Object> markRead(HttpServletRequest request, @RequestBody JSONObject json) {
        Map<String, Object> ret = new HashMap<String, Object>();
        TstUser user = (TstUser) SecurityUtils.getSubject().getPrincipal();

        Integer id = json.getInteger("id");
        Boolean result = msgService.markRead(id, user.getId());
        if (!result) {
            return authorFail();
        }

        optFacade.opt(WsConstant.WS_TODO, user);

        ret.put("code", Constant.RespCode.SUCCESS.getCode());
        return ret;
    }

    @RequestMapping(value = "markAllRead", method = RequestMethod.POST)
    public Map<String, Object> markAllRead(HttpServletRequest request, @RequestBody JSONObject json) {
        Map<String, Object> ret = new HashMap<String, Object>();

        TstUser user = (TstUser) SecurityUtils.getSubject().getPrincipal();
		msgService.markAllRead(user.getId());
		optFacade.opt(WsConstant.WS_TODO, user);

        ret.put("code", Constant.RespCode.SUCCESS.getCode());
        return ret;
    }

}
