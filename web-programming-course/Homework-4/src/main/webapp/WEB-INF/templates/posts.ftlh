<#-- @ftlvariable name="user_id" type="java.lang.String" -->
<#import "./common.ftlh" as c>

<@c.page>
    <#if user_id??>
        <#assign u = c.findBy(users, "id", user_id, 0)!/>
    </#if>

    <#if u?has_content>
        <#assign user_posts = posts?filter(p -> p.userId == user_id)?reverse>
        <#if user_posts?size != 0>
            <#list user_posts as p>
                <@c.createPost p true/>
            </#list>
        <#else>
            User don't have posts
        </#if>
    <#else>
        No such post
    </#if>
</@c.page>
