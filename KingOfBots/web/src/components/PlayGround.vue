<template>
    <div class="playground">
        <GameMap></GameMap>
        <div class="hinit" v-if="$store.state.user.id == $store.state.pk.a_id">你的snake为蓝色</div>
        <div class="hinit" v-else>你的snake为红色</div>
    </div>

</template>

<script>
import GameMap from './GameMap.vue'

export default{
    components:{
        GameMap,
    }
}
</script>

<style scoped>
.playground{
    height: 70vh;
    width: 60vw;
    margin: 40px auto;
}
.hinit{
    color: white;
    text-align: center;
    margin-top: 10px;
}
</style>