package com.ngtesting.platform.service.impl;

import com.ngtesting.platform.dao.IssueTqlDao;
import com.ngtesting.platform.service.intf.IssueJqlBuildService;
import com.ngtesting.platform.tql.query.builder.SqlQueryBuilderFactory;
import com.ngtesting.platform.tql.query.builder.support.builder.SqlBuilder;
import com.ngtesting.platform.tql.query.builder.support.model.JsonRule;
import com.ngtesting.platform.tql.query.builder.support.model.enums.EnumOperator;
import com.ngtesting.platform.tql.query.builder.support.model.enums.EnumRuleType;
import com.ngtesting.platform.tql.query.builder.support.model.result.SqlQueryResult;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

@Service
public class IssueJqlBuildServiceImpl extends BaseServiceImpl implements IssueJqlBuildService {
    Log logger = LogFactory.getLog(IssueJqlBuildServiceImpl.class);

    @Autowired
    IssueTqlDao isuTqlDao;

    @Override
    public JsonRule genJsonRule(String id, String field, String input, String val,
                                EnumOperator operator, EnumRuleType type) {

        JsonRule rule = new JsonRule();

        rule.setId(id);
        rule.setField(field);
        rule.setInput(input);
        rule.setValue(val);

        rule.setType(type.value());
        rule.setOperator(operator.value());

        return rule;
    }

//    @Override
//    public JsonRule genJsonRuleGroup(String id, String field, String input, String val,
//                                EnumCondition condition, EnumOperator operator, EnumRuleType type) {
//
//        JsonRule rule = new JsonRule();
//        rule.setCondition(condition.value());
//        rule.setRules(new LinkedList<>());
//
//        rule.setId(id);
//        rule.setField(field);
//        rule.setInput(input);
//        rule.setValue(val);
//
//        rule.setType(type.value());
//        rule.setOperator(operator.value());
//
//        return rule;
//    }

    @Override
    public JsonRule genJsonRuleRoot() {
        JsonRule ret = new JsonRule();
        ret.setCondition("AND");

        List<JsonRule> rules = new LinkedList<>();
        ret.setRules(rules);

        return ret;
    }

    @Override
    public SqlQueryResult buildSqlQuery(String jql) {
        SqlQueryBuilderFactory sqlQueryBuilderFactory = new SqlQueryBuilderFactory();
        SqlBuilder sqlBuilder = sqlQueryBuilderFactory.builder();

        SqlQueryResult sqlQueryResult = null;
        try {
            sqlQueryResult = sqlBuilder.build(jql);
        } catch (IOException e) {
            e.printStackTrace();
        }

        logger.info(sqlQueryResult.getQuery());
        logger.info(sqlQueryResult.getParams());

        return sqlQueryResult;
    }

}
