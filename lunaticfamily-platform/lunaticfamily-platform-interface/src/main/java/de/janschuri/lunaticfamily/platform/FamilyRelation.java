package de.janschuri.lunaticfamily.platform;

import de.janschuri.lunaticfamily.common.commands.family.Family;

import java.util.List;

public class FamilyRelation {

    String relation;
    String relationLang;
    String name;
    String skinUrl;
    List<FamilyRelation> children;
    List<FamilyRelation> parents;
    List<FamilyRelation> siblings;
    List<FamilyRelation> partners;

    public FamilyRelation(String relation, String relationLang, String name, String skinUrl, List<FamilyRelation> children, List<FamilyRelation> parents, List<FamilyRelation> siblings, List<FamilyRelation> partners) {
        this.relation = relation;
        this.relationLang = relationLang;
        this.name = name;
        this.skinUrl = skinUrl;
        this.children = children;
        this.parents = parents;
        this.siblings = siblings;
        this.partners = partners;
    }

    public String getRelation() {
        return relation;
    }

    public String getRelationLang() {
        return relationLang;
    }

    public String getName() {
        return name;
    }

    public String getSkinUrl() {
        return skinUrl;
    }

    public List<FamilyRelation> getChildren() {
        return children;
    }

    public List<FamilyRelation> getParents() {
        return parents;
    }

    public List<FamilyRelation> getSiblings() {
        return siblings;
    }

    public List<FamilyRelation> getPartners() {
        return partners;
    }
}
