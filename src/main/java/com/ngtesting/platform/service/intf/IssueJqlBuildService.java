package com.ngtesting.platform.service.intf;

import com.ngtesting.platform.tql.query.builder.support.model.JsonRule;
import com.ngtesting.platform.tql.query.builder.support.model.enums.EnumOperator;
import com.ngtesting.platform.tql.query.builder.support.model.enums.EnumRuleType;
import com.ngtesting.platform.tql.query.builder.support.model.result.SqlQueryResult;

public interface IssueJqlBuildService extends BaseService {

//    JsonRule genJsonRuleGroup(String id, String field, String input, String val,
//                              EnumCondition condition, EnumOperator operator, EnumRuleType type);

    JsonRule genJsonRule(String id, String field, String input, String val,
                         EnumOperator operator, EnumRuleType type);

    JsonRule genJsonRuleRoot();

    SqlQueryResult buildSqlQuery(String jql);
}
