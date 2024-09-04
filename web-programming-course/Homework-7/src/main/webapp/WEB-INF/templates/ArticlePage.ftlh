<#import "commons.ftlh" as c>

<@c.page>
    <div class="article form-box">
        <div class="header">Post article</div>
        <div class="body">
            <form method="post" action="">
                <input type="hidden" name="action"/>
                <div class="field">
                    <div class="name">
                        <label for="title">Title</label>
                    </div>
                    <div class="value">
                        <input id="title" name="title" type="text">
                    </div>
                </div>
                <div class="field">
                    <div class="name">
                        <label for="text">Text</label>
                    </div>
                    <div class="value">
                        <textarea id="text" name="text" type="text" rows="5"></textarea>
                    </div>
                </div>
                <div class="error"></div>
                <div class="button-field">
                    <input type="submit" value="Post">
                </div>
            </form>
        </div>
    </div>
</@c.page>

<script>
    $(function () {
        $('.article form').submit(function () {
            const title = $(this).find("input[name='title']").val();
            const text = $(this).find("textarea[name='text']").val();

            ajax({
                    action: 'post',
                    title,
                    text
                },
                success(this));

            return false;
        });
    });
</script>
