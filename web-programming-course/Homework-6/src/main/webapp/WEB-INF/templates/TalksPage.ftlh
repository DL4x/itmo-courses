<#-- @ftlvariable name="text" type="java.lang.String" -->
<#-- @ftlvariable name="error" type="java.lang.String" -->
<#-- @ftlvariable name="currentUser" type="java.lang.String" -->
<#-- @ftlvariable name="users" type="ru.itmo.wp.model.domain.User[]" -->
<#-- @ftlvariable name="talks" type="ru.itmo.wp.model.domain.Talk[]" -->
<#import "commons.ftlh" as c>

<@c.page>
    <div class="form-box">
        <div class="header">Send message</div>
        <div class="body">
            <form method="post" action="">
                <input type="hidden" name="action" value="send"/>
                <div class="field">
                    <div class="name">
                        <label for="targetUser">Select</label>
                    </div>
                    <div class="value">
                        <select id="targetUser" name="targetUser">
                            <#list users as user>
                                <#if user.login != currentUser>
                                    <option value="${user.id!}">${user.login}</option>
                                </#if>
                            </#list>
                        </select>
                    </div>
                </div>
                <div class="field">
                    <div class="name">
                        <label for="targetText">Your message</label>
                    </div>
                    <div class="value">
                        <textarea id="targetText" name="targetText" type="text" value="${text!}" rows="5"></textarea>
                    </div>
                </div>
                <#if error??>
                    <div class="error">
                        ${error!}
                    </div>
                </#if>
                <div class="button-field">
                    <input type="submit" value="Send">
                </div>
            </form>
        </div>
    </div>
    <div>
        <div class="datatable" style="width: 30rem; margin: 0 auto; padding-top: 1rem">
            <div class="caption">Talk</div>
            <table>
                <thead>
                <tr>
                    <th>Id</th>
                    <th>Source user</th>
                    <th>Target user</th>
                    <th>Message</th>
                    <th>Created</th>
                </tr>
                </thead>
                <tbody>
                <#list talks as talk>
                    <tr>
                        <td>${talk.id}</td>
                        <td>${talk.sourceId}</td>
                        <td>${talk.targetId}</td>
                        <td>${talk.text}</td>
                        <td>${talk.creationTime}</td>
                    </tr>
                </#list>
                </tbody>
            </table>
        </div>
    </div>
</@c.page>
