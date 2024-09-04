<#-- @ftlvariable name="articles" type="ru.itmo.wp.model.domain.Article[]" -->
<#import "commons.ftlh" as c>

<@c.page>
    <div class="articles datatable">
        <div class="caption">My Articles</div>
        <table>
            <thead>
            <tr>
                <th>Id</th>
                <th>Title</th>
                <th>Hide/Show</th>
            </tr>
            </thead>
            <tbody>
            <#list articles as article>
                <tr>
                    <td>${article.id}</td>
                    <td>${article.title}</td>
                    <td><input type="button" name="${article.id}" value="<#if article.hidden>Show<#else>Hide</#if>"></td>
                </tr>
            </#list>
            </tbody>
        </table>
    </div>
</@c.page>

<script>
    $(function () {
        $('.articles tr td input').click(function () {
            const $button = $(this);
            const id = $button.attr('name');
            const hidden = $button.val() === 'Show';

            ajax({
                action: 'showOrHide',
                id,
                hidden
            },
            function (response) {
                if (response['error']) {
                    notify(response['error'], 'error');
                } else {
                    $button.val(hidden ? 'Hide' : 'Show');
                    notify('Article status has been successfully changed', 'success');
                }
            });
        });
    });
</script>
