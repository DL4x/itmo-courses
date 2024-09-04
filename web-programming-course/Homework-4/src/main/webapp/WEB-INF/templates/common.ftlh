<#macro page>
    <!DOCTYPE html>
    <html lang="ru">
    <head>
        <meta charset="UTF-8">
        <title>Codeforces</title>
        <link rel="stylesheet" href="/css/normalize.css">
        <link rel="stylesheet" href="/css/style.css">
        <link rel="icon" href="/favicon.ico">
    </head>
    <body>
    <@header/>
    <div class="middle">
        <@sidebar/>
        <main>
            <#nested/>
        </main>
    </div>
    <@footer/>
    </body>
    </html>
</#macro>

<#macro header>
    <header>
        <a href="#" class="logo"><img alt="Codeforces" title="Codeforces"
                                      src="/img/codeforces-logo-with-telegram.png"></a>
        <div class="languages">
            <a href="#"><img alt="In English" title="In English"
                             src="/img/gb.png"></a>
            <a href="#"><img alt="In Russian" title="In Russian"
                             src="/img/ru.png"></a>
        </div>
        <div class="enter-or-register-box">
            <#if user??>
                <@userlink user user.handle true/>
                |
                <a href="/logout">Logout</a>
            <#else>
                <a href="/enter">Enter</a>
                |
                <a href="/register">Register</a>
            </#if>
        </div>
        <nav>
            <#assign items=[
            {"href": "/index", "name": "Home"},
            {"href": "/misc/help", "name": "Help"},
            {"href": "/contests", "name": "Contests"},
            {"href": "/users", "name": "Users"}
            ]/>
            <ul>
                <#list items as item>
                    <li>
                        <#if item.href != currentPage>
                            <a href="${item.href}">${item.name}</a>
                        <#else>
                            <a class="currentPage" href="${item.href}">${item.name}</a>
                        </#if>
                    </li>
                </#list>
            </ul>
        </nav>
    </header>
</#macro>

<#macro sidebar>
    <aside>
        <#list posts as p>
            <section>
                <div class="header">
                    Post #${p.id}
                </div>
                <div class="body">
                    <@format p.text true/>
                </div>
                <div class="footer">
                    <@postlink p "View all"/>
                </div>
            </section>
        </#list>
    </aside>
</#macro>

<#macro footer>
    <footer>
        2010-2021 &copy; by Mike Mirzayanov
    </footer>
</#macro>

<#macro userlink user field nameOnly>
<#-- @ftlvariable name="user" type="ru.itmo.web.hw4.model.User" -->
    <a href="/user?handle=${user.handle}"
       <#if !nameOnly>
           style="${user.color.toString()}; font-weight: bold; text-decoration: none"
       </#if>
    >${field}</a>
</#macro>

<#macro postlink post field>
<#-- @ftlvariable name="post" type="ru.itmo.web.hw4.model.Post" -->
    <a href="/post?post_id=${post.id}">${field}</a>
</#macro>

<#macro createUser user>
    <div class="user">
        <header>
            <@userlink user user.name false/>
        </header>
        <ul>
            <li>id: ${user.id}</li>
            <li>handle: ${user.handle}</li>
            <li>name: ${user.name}</li>
            <li><a href="/posts?user_id=${user.id}">${countBy(posts, "userId", user.id)}</a></li>
        </ul>
        <img alt="user logo" src="/img/no-title.jpeg">
    </div>
</#macro>

<#macro createArrow content position>
    <#if position?has_content>
        <@userlink position content true/>
    <#else>
        <a style="color: var(--border-color)">${content}</a>
    </#if>
</#macro>

<#macro format text cut>
    <#if cut && 350 < text?length>
        ${text?substring(0, 350) + "..."}
    <#else>
        ${text}
    </#if>
</#macro>

<#macro createPost post cut>
    <article>
        <div class="title">
            <@postlink post post.title/>
        </div>
        <div class="information">
            <#assign u = findBy(users, "id", post.userId, 0)!/>
            By <#if u?has_content>
                <@userlink u u.handle false!/>
            <#else>
                Unknown user
            </#if>, 2 days ago, translation
        </div>
        <div class="body">
            <p><@format post.text cut/></p>
        </div>
        <ul class="attachment">
            <li>Announcement of <a href="#">Codeforces Round #510 (Div. 1)</a></li>
            <li>Announcement of <a href="#">Codeforces Round #510 (Div. 2)</a></li>
        </ul>
        <div class="footer">
            <div class="left">
                <img src="img/voteup.png" title="Vote Up" alt="Vote Up"/>
                <span class="positive-score">+173</span>
                <img src="img/votedown.png" title="Vote Down" alt="Vote Down"/>
            </div>
            <div class="right">
                <img src="img/date_16x16.png" title="Publish Time" alt="Publish Time"/>
                2 days ago
                <img src="img/comments_16x16.png" title="Comments" alt="Comments"/>
                <a href="#">68</a>
            </div>
        </div>
    </article>
</#macro>

<#function findBy items key value const>
    <#list items as item>
        <#if item[key] == value>
            <#return items[item_index + const]/>
        </#if>
    </#list>
</#function>

<#function countBy items key value>
    <#assign result = 0>
    <#list items as item>
        <#if item[key] == value>
            <#assign result++>
        </#if>
    </#list>
    <#return result>
</#function>
