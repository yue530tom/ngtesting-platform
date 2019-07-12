package com.ngtesting.platform.action.client;

import com.ngtesting.platform.action.BaseAction;
import com.ngtesting.platform.config.Constant;
import com.ngtesting.platform.service.intf.IssueMiscService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(Constant.API_PATH_CLIENT + "issue_misc/")
public class IssueMiscAction extends BaseAction {

    @Autowired
    IssueMiscService issueMiscService;

}
