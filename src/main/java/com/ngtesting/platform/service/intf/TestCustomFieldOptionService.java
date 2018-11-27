package com.ngtesting.platform.service.intf;

import com.ngtesting.platform.model.TstCustomFieldOption;

import java.util.List;

public interface TestCustomFieldOptionService extends BaseService {
    List<TstCustomFieldOption> listVos(Integer fieldId);
    TstCustomFieldOption save(TstCustomFieldOption option, Integer orgId);
    Boolean delete(Integer id, Integer fieldId, Integer orgId);
    Boolean changeOrder(Integer id, String act, Integer fieldId, Integer orgId);

    Boolean setDefault(Integer id, Integer fieldId, Integer orgId);
}
