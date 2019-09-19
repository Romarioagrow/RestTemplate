<template>
    <div>
        <i>({{item.id}})</i>
        <strong>{{item.name}}</strong>
        {{item.type}}
        {{item.attributes}}
    </div>
</template>

<script>
    export default {
        props: ['item']
    }
</script>

<style scoped>
</style>