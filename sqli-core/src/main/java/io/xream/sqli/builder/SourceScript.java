/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.xream.sqli.builder;

import io.xream.sqli.core.CriteriaToSql;
import io.xream.sqli.core.SqlBuildingAttached;
import io.xream.sqli.util.SqliStringUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author Sim
 */
public final class SourceScript implements ConditionCriteriaToSql, ConditionCriteriaToSql.Pre {

    private String source;
    private Criteria.ResultMapCriteria subCriteria;
    private JoinType joinType;
    private String joinStr;
    private On on;
    private String alia;
    private List<BuildingBlock> buildingBlockList = new ArrayList<>();

    private transient boolean used;
    private transient boolean targeted;

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public Criteria.ResultMapCriteria getSubCriteria() {
        return subCriteria;
    }

    public void setSubCriteria(Criteria.ResultMapCriteria subCriteria) {
        this.subCriteria = subCriteria;
    }

    public JoinType getJoinType() {
        return joinType;
    }

    public void setJoinType(JoinType joinType) {
        this.joinType = joinType;
    }

    public String getJoinStr() {
        return joinStr;
    }

    public void setJoinStr(String joinStr) {
        this.joinStr = joinStr;
    }

    public List<BuildingBlock> getBuildingBlockList() {
        return buildingBlockList;
    }

    public void setBuildingBlockList(List<BuildingBlock> buildingBlocks) {
        this.buildingBlockList = buildingBlocks;
    }

    public On getOn() {
        return on;
    }

    public void setOn(On on) {
        this.on = on;
    }

    public String getAlia() {
        return alia;
    }

    public void setAlia(String alia) {
        this.alia = alia;
    }

    public boolean isUsed() {
        return this.used;
    }

    public void used() {
        this.used = true;
    }

    public boolean isTargeted() {
        return targeted;
    }

    public void targeted() {
        this.targeted = true;
    }

    public String alia() {
        return alia == null ? source : alia;
    }


    public void pre(SqlBuildingAttached attached, CriteriaToSql criteriaToSql) {

        if (subCriteria != null) {
            final SqlBuilt sqlBuilt = new SqlBuilt();
            attached.getSubList().add(sqlBuilt);
            criteriaToSql.toSql(true, subCriteria, sqlBuilt, attached);
        }

        pre(attached.getValueList(), buildingBlockList);
    }

    public String sql() {
        if (SqliStringUtil.isNullOrEmpty(source) && subCriteria == null)
            return "";
        if (subCriteria != null) {
            source = SqlScript.SUB;
        }
        if (joinStr == null && (joinType == null || joinType == JoinType.MAIN)) {
            if (alia != null && !alia.equals(source)) {
                return source + " " + alia;
            }
            return source;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(joinStr == null ? joinType.sql() : joinStr + SqlScript.SPACE).append(source);

        if (alia != null && !alia.equals(source))
            sb.append(SqlScript.SPACE).append(alia);

        if (on != null) {
            sb.append(SqlScript.ON);
            String aliaName = alia == null ? source : alia;
            String key = on.getKey();
            if (SqliStringUtil.isNotNull(key)) {
                sb.append(on.getJoinFrom().getAlia()).append(".").append(on.getJoinFrom().getKey())
                        .append(SqlScript.SPACE).append(on.getOp()).append(SqlScript.SPACE)
                        .append(aliaName)
                        .append(".")
                        .append(key);
            }
        }

        buildConditionSql(sb, buildingBlockList);

        return sb.toString();
    }

    @Override
    public String toString() {
        return "SourceScript{" +
                "source='" + source + '\'' +
                ", subCriteria=" + subCriteria +
                ", joinType=" + joinType +
                ", joinStr='" + joinStr + '\'' +
                ", on=" + on +
                ", alia='" + alia + '\'' +
                ", buildingBlockList=" + buildingBlockList +
                ", used=" + used +
                ", targeted=" + targeted +
                '}';
    }
}
