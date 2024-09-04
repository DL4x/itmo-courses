<#-- @ftlvariable name="handle" type="java.lang.String" -->
<#-- @ftlvariable name="user_id" type="java.lang.String" -->
<#-- @ftlvariable name="logged_user_id" type="java.lang.String" -->
<#import "./common.ftlh" as c>

<@c.page>
    <#if handle??>
        <#assign u = c.findBy(users, "handle", handle, 0)!/>
    <#elseif user_id??>
        <#assign u = c.findBy(users, "id", user_id, 0)!/>
    <#elseif logged_user_id??>
        <#assign u = c.findBy(users, "id", logged_user_id, 0)!/>
    </#if>

    <#if u?has_content>
        <@c.createUser u/>
        <div class="arrows">
            <@c.createArrow content="←" position=c.findBy(users, "handle", u.handle, -1)!/>
            <@c.createArrow content="→" position=c.findBy(users, "handle", u.handle, +1)!/>
        </div>
    <#else>
        No such user
    </#if>
</@c.page>
