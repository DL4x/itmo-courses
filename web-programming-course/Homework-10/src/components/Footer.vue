<template>
    <footer>
        <p>Number of users: {{ users_len }}</p>
        <p>Number of posts: {{ posts_len }}</p>
        <a href="/">Codeforces</a> &copy; 2022 by Mike Mirzayanov
    </footer>
</template>

<script>
export default {
    name: "Footer",
    props: ["users_len", "posts_len"]
}
</script>

<style scoped>

</style>
