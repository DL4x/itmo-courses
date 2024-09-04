<#-- @ftlvariable name="comment" type="ru.itmo.wp.domain.Comment" -->

<#import "commons.ftlh" as c>

<@c.page>
    <#if post??>
        <@c.post post></@c.post>

        <#if user??>
            <div class="body" style="margin-bottom: 2rem">
                <form method="post" action="">
                    <div class="field">
                        <div class="name">
                            <label for="text">Write comment</label>
                        </div>
                        <div class="value">
                            <textarea id="text" name="text" type="text" rows="3" style="width: 25%">${comment.text!}</textarea>
                        </div>
                        <@c.error "comment.text"/>
                    </div>
                    <div class="button-field">
                        <input type="submit" value="Send">
                    </div>
                </form>
            </div>
        </#if>

        <div class="users datatable">
            <div class="caption">Comments</div>
            <table>
                <thead>
                <tr>
                    <th>User</th>
                    <th>Text</th>
                </tr>
                </thead>
                <tbody>
                <#list post.comments as comment>
                    <tr>
                        <td>${comment.user.login}</td>
                        <td>
                            <#list comment.text?split("\n") as paragraph>
                                <p>${paragraph}</p>
                            </#list>
                        </td>
                    </tr>
                </#list>
                </tbody>
            </table>
        </div>
    <#else>
        No such post
    </#if>
</@c.page>
