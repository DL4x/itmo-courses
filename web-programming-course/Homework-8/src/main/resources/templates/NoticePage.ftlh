<#import "commons.ftlh" as c>

<@c.page>
    <div class="form-box">
        <div class="header">Notice</div>
        <div class="body">
            <form method="post" action="">
                <input type="hidden" name="action"/>
                <div class="field">
                    <div class="name">
                        <label for="content">Content</label>
                    </div>
                    <div class="value">
                        <textarea id="content" name="content" type="text" rows="5" style="resize: none"></textarea>
                    </div>
                    <@c.error "noticeForm.content"/>
                </div>
                <div class="button-field">
                    <input type="submit" value="Add">
                </div>
            </form>
        </div>
    </div>
</@c.page>
