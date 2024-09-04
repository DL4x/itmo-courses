<#import "commons.ftlh" as c>

<@c.page>
    <#if user??>
        <div class="user">
            <header>${user.login}</header>
            <ul>
                <li>Id: ${user.id}</li>
                <li>Login: ${user.login}</li>
                <li>Creation time: ${user.creationTime}</li>
            </ul>
        </div>
    <#else>
        No such user
    </#if>
</@c.page>
