<#import "./common.ftlh" as c>

<@c.page>
    <div class="datatable">
        <div class="caption">Users</div>
            <table>
                <thead>
                <tr>
                    <th>Id</th>
                    <th>Handle</th>
                    <th>Name</th>
                </tr>
                </thead>
                <#list users as u>
                    <#if user?? && user.id == u.id>*</#if>
                    <tr>
                        <td>${u.id}</td>
                        <td><@c.userlink u u.handle false/></td>
                        <td>${u.name}</td>
                    </tr>
                </#list>
            </table>
    </div>
</@c.page>
