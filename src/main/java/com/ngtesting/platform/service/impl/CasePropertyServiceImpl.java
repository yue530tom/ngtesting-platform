package com.ngtesting.platform.service.impl;

import com.ngtesting.platform.dao.CaseExeStatusDao;
import com.ngtesting.platform.dao.CasePriorityDao;
import com.ngtesting.platform.dao.CaseTypeDao;
import com.ngtesting.platform.dao.CustomFieldDao;
import com.ngtesting.platform.model.CustomField;
import com.ngtesting.platform.model.CustomFieldOption;
import com.ngtesting.platform.model.TstCasePriority;
import com.ngtesting.platform.model.TstCaseType;
import com.ngtesting.platform.service.intf.CasePropertyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class CasePropertyServiceImpl extends BaseServiceImpl implements CasePropertyService {

	@Autowired
    CasePriorityDao casePriorityDao;

	@Autowired
    CaseTypeDao caseTypeDao;

	@Autowired
    CaseExeStatusDao caseExeStatusDao;

    @Autowired
    CustomFieldDao customFieldDao;

	@Override
	public Map<String,Map<String,String>> getMap(Integer orgId) {
		Map<String,String> typeMap = getTypeMap(orgId);
		Map<String,String> priorityMap = getPriorityMap(orgId);
//		Map<String,String> exeStatusMap = getExeStatusMap(orgId);

		Map map = new LinkedHashMap();
		map.put("typeId", typeMap);
		map.put("priorityId", priorityMap);
//		map.put("status", exeStatusMap);

		List<CustomField> fields = customFieldDao.listForCase(orgId);
		for (CustomField field : fields) {
            Map<String,String> mapOption = new LinkedHashMap();
            for (CustomFieldOption option : field.getOptions()) {
                mapOption.put(option.getId().toString(), option.getLabel());
            }

			map.put(field.getColCode(), mapOption);
		}

		return map;
	}

	@Override
	public Map<String,String> getTypeMap(Integer orgId) {
		List<TstCaseType> ls = caseTypeDao.list(orgId);
		Map<String,String> map = new LinkedHashMap();
		for (TstCaseType item : ls) {
			map.put(item.getId().toString(), item.getLabel());
		}

		return map;
	}

	@Override
	public Map<String,String> getPriorityMap(Integer orgId) {
        List<TstCasePriority> ls = casePriorityDao.list(orgId);

        Map<String,String> map = new LinkedHashMap();
		for (TstCasePriority item : ls) {
			map.put(item.getId().toString(), item.getLabel());
		}

		return map;
	}

//	@Override
//	public Map<String,String> getExeStatusMap(Integer orgId) {
//        List<TstCaseExeStatus> ls = caseExeStatusDao.listExeStatus(orgId);
//        Map<String,String> map = new LinkedHashMap();
//		for (TstCaseExeStatus item : ls) {
//			map.put(item.getId().toString(), item.getLabel());
//		}
//
//		return map;
//	}
}
