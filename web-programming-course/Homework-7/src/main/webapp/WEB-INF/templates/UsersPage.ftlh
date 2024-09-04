<#-- @ftlvariable name="admin" type="java.lang.Boolean" -->
<#import "commons.ftlh" as c>

<@c.page>
    <div class="users datatable">
        <div class="caption">User</div>
        <table>
            <thead>
            <tr>
                <th>Id</th>
                <th>Login</th>
                <#if admin>
                    <th>Admin</th>
                    <th>Allow</th>
                </#if>
            </tr>
            </thead>
            <tbody>
            <template>
                <tr>
                    <td class="user-id"></td>
                    <td class="user-login"></td>
                    <#if admin>
                        <td class="user-admin"></td>
                        <td class="button">
                            <input type="button">
                        </td>
                    </#if>
                </tr>
            </template>
            <tr class="noData">
                <td colspan="3">
                    No data
                </td>
            </tr>
            </tbody>
        </table>
    </div>

    <script>
        $(function () {
            const $noData = $('.users .noData');
            $noData.find('td').text('Loading...');

            ajax({
                    action: 'findAll'
                },
                function(response) {
                    response['users'].forEach(function(user) {
                        $noData.hide();
                        const $tr = $($('.users template').prop('content')).clone();
                        $tr.find('.user-id').text(user['id']);
                        $tr.find('.user-login').text(user['login']);
                        $tr.find('.user-admin').text(user['admin']);
                        $tr.find('tr').attr('data-userId', user['id']);
                        $tr.find('.button input').val(user['admin'] ? 'disable' : 'enable');
                        $('.users tbody').append($tr);
                    });
                    $('.users tr').click(function() {
                        ajax({
                                action: 'findUser',
                                userId: $(this).attr('data-userId')
                            },
                            function (response) {
                                notify(response['user']['creationTime'], 'info');
                            }
                        );
                    });
                    $('.users tr .button input').click(function () {
                        const $button = $(this);
                        const $admin = $(this).closest('tr').find('.user-admin');
                        const admin = $button.val() === 'disable';
                        const userId = $(this).closest('tr').find('.user-id').text();

                        ajax({
                            action: 'enableOrDisable',
                            userId,
                            admin
                        },
                        function (response) {
                            if (response['error']) {
                                notify(response['error'], 'error');
                            } else {
                                $admin.text(!admin);
                                $button.val(admin ? 'enable' : 'disable');
                                notify('User rights have been successfully changed', 'success');
                            }
                        });
                    });
                }
            );
        })
    </script>
</@c.page>
