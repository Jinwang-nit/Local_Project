<template>
    <div ref="parent" class = "gamemap">
        <canvas ref="canvas" tabindex="0">

        </canvas>
    </div>
</template>

<script >
import { GameMap } from '@/assets/script/GameMap';
import {ref, onMounted} from 'vue';
import { useStore } from 'vuex';
export default{
    setup(){
        let parent = ref(null);
        let canvas = ref(null);
        const store = useStore();
        //  组件加载完成之后进行的事
        onMounted(()=>{
            store.commit("updateGameObject", new GameMap(canvas.value.getContext('2d'),parent.value,store));
        });

        return {
            parent,
            canvas,
        }
    }
}
</script>

<style scoped>
div.gamemap{
    width: 100%;
    height: 100%;
    display: flex;
    justify-content: center;
}
</style>