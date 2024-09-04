<#-- @ftlvariable name="post_id" type="java.lang.Long" -->
<#import "./common.ftlh" as c>

<@c.page>
    <#if post_id??>
        <#assign p = c.findBy(posts, "id", post_id, 0)!/>
    </#if>

    <#if p?has_content>
        <@c.createPost p false/>
    <#else>
        No such post
    </#if>
</@c.page>
