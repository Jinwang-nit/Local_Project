<template>
<PlayGround v-if="$store.state.pk.status === 'playing'"></PlayGround>

<MatchGround v-if="$store.state.pk.status === 'matching'"></MatchGround>
<ResultBoard v-if="$store.state.pk.loser !== 'none'"></ResultBoard>

</template>

<script>
import PlayGround from '../../components/PlayGround.vue';
import { onMounted,onUnmounted } from 'vue';
import { useStore } from 'vuex';
import MatchGround from '@/components/MatchGround.vue';
import ResultBoard from '../../components/ResultBoard.vue';
export default{
    components:{
        PlayGround,
        MatchGround,
        ResultBoard,
    },
    setup(){
        const store = useStore();
        let socket = null;
        const socketUrl = `ws://127.0.0.1:3000/websocket/${store.state.user.token}/`;
        store.commit("updateIsRecord",false);

        onMounted(()=>{ // 组件被挂载时执行
            store.commit("updateLoser", "none");
            socket = new WebSocket(socketUrl);

            socket.onopen=()=>{ // 连接后端
                console.log("连接成功");
                store.commit("updateSocket",socket);
            };

            socket.onmessage=(msg)=>{ // 获取后端的信息
                const data = JSON.parse(msg.data);
                if (data.event === "start-matching"){
                    store.commit("updateOpponent",{
                        username:data.opponent_username,
                        photo:data.opponent_photo,
                    });
                    setTimeout(() => {
                        store.commit("updateStatus","playing");
                    }, 200);

                    store.commit("updateMap",data.game);
                    
                }else if (data.event === "move"){
                    const game = store.state.pk.gameObject;
                    const [snake0,snake1] = game.snakes;
                    snake0.set_direction(data.a_direction);
                    snake1.set_direction(data.b_direction);
                }else if (data.event === "result"){
                    const game = store.state.pk.gameObject;
                    const [snake0, snake1] = game.snakes;
                    if (data.loser === "all" || data.loser === "A")
                        snake0.status = "die";
                    if (data.loser === "all" || data.loser === "B")
                        snake1.status = "die";  
                        
                    store.commit("updateLoser",data.loser);
                }
            };

            socket.onclose=()=>{
                console.log("连接关闭");
            };
        });
        
        onUnmounted(()=>{  // 组件被卸载时执行
            socket.close();
            store.commit("updateStatus", "matching");

        });

    }
}
</script>

<style scoped>

</style>