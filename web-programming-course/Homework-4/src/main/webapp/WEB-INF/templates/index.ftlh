<#import "./common.ftlh" as c>

<@c.page>
    <#list posts?reverse as p>
        <#if post?? && post.id == p.id>*</#if>
        <@c.createPost p true/>
    </#list>
</@c.page>
